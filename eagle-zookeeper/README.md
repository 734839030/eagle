## eagle-zookeeper 的例子

```
具体使用见src/test/java
```

主要使用curator 4.x  简化zookeeper 操作

```
package com.seezoon.eagle.zookeeper;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CutatorZookeeperServiceBean {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(CutatorZookeeperServiceBean.class);

	/**
	 * 事务操作，等高级特性 ，锁 直接取这个扩展
	 */
	private CuratorFramework curatorFramework;
	
	public CuratorFramework getCuratorFramework() {
		return curatorFramework;
	}

	/**
	 * 
	 * @param connectString 连接串多个有逗号分隔
	 * @param baseSleepTimeMs 重试基础间隔时间
	 * @param retryTimes 操作失败后重试次数
	 * @param sessionTimeoutMs session 超时时间，需要和服务器ticktime的时间来对比判断
	 * @param connectionTimeoutMs
	 * @return
	 */
	public CutatorZookeeperServiceBean(String connectString, int baseSleepTimeMs, int retryTimes, int sessionTimeoutMs,
			int connectionTimeoutMs) {
		this(connectString, baseSleepTimeMs, retryTimes, sessionTimeoutMs, connectionTimeoutMs, null, null);
	}
	/**
	 * 
	 * @param connectString 连接串多个有逗号分隔
	 * @param baseSleepTimeMs 重试基础间隔时间
	 * @param retryTimes 操作失败后重试次数
	 * @param sessionTimeoutMs session 超时时间，需要和服务器ticktime的时间来对比判断
	 * @param connectionTimeoutMs
	 * @param namespace 	CuratorFramework提供了命名空间的概念，这样CuratorFramework会为它的API调用的path加上命名空间：  如aa
	 * @param connectionStateListener 连接状态listener
	 * @return
	 */
	public CutatorZookeeperServiceBean(String connectString, int baseSleepTimeMs, int retryTimes, int sessionTimeoutMs,
			int connectionTimeoutMs,String namespace,ConnectionStateListener connectionStateListener) {
		// 操作重试策略，连接策略内部维护一直会重试
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, retryTimes);
		curatorFramework =  CuratorFrameworkFactory.builder().connectString(connectString)
		.sessionTimeoutMs(sessionTimeoutMs)
		.connectionTimeoutMs(connectionTimeoutMs)
		.namespace(namespace)
		.retryPolicy(retryPolicy).build();
		if (null != connectionStateListener) {
			curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
		}
		//连接监听器
//		curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener(){
//			@Override
//			public void stateChanged(CuratorFramework client, ConnectionState newState) {
//				logger.info("zk state changed:{}",newState.name());
//			}
//		});
		curatorFramework.start();
	}

	/**
	 * 存在的节点不能重复创建
	 * @param createMode
	 * @param path
	 * @param data 可以为null
	 * @throws Exception
	 */
	public void create(CreateMode createMode,String path,String data) throws Exception{
		Stat forPath = this.checkExists(path);
		if (null != forPath) {
			logger.warn("{} is exists ",path);
			curatorFramework.setData()
			.forPath(path,this.toBytes(data));
		} else {
			 curatorFramework.create()
			.creatingParentsIfNeeded()
			.withMode(createMode)//节点类型
			.forPath(path,this.toBytes(data));
		}
	}
	/**
	 * 放入数据
	 * @param path
	 * @param data 可以为null
	 * @throws Exception
	 */
	public void setData(String path,String data) throws Exception{
		Stat forPath = this.checkExists(path);
		if (null == forPath) {
			this.create(CreateMode.PERSISTENT, path, data);
		}
		curatorFramework.setData()
		.forPath(path,this.toBytes(data));
	}
	/**
	 * 获取string 数据
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public String getData(String path) throws Exception{
		Stat forPath = this.checkExists(path);
		if (null == forPath) {
			return null;
		}
		byte[] bytes = curatorFramework.getData()
		.forPath(path);
		return this.toString(bytes);
	}
	/**
	 * 是否存在，返回节点信息
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public Stat checkExists(String path) throws Exception{
		return curatorFramework.checkExists().forPath(path);
	}
	
	/**
	 * 获取子节点名称数组
	 * 节点不存在返回empty list
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<String> list(String path) throws Exception{
		List<String> children = curatorFramework.getChildren().forPath(path);
		return children;
	}
	/**
	 * 删除目录，如果有子节点，子节点一起删除
	 * @param path
	 * @throws Exception
	 */
	public void delete(String path) throws Exception{
		Stat stat = this.checkExists(path);
		if (null != stat) {
			curatorFramework.delete().deletingChildrenIfNeeded().forPath(path);
		}
	}
	
	/**
	 * Path Cache用来监控一个ZNode的子节点. 当一个子节点增加， 更新，删除时， Path Cache会改变它的状态， 会包含最新的子节点， 子节点的数据和状态，而状态的更变将通过PathChildrenCacheListener通知。

	 * @param pathChildrenCacheListener
	 * @throws Exception
	 */
	public PathChildrenCache wathchPath(String path,PathChildrenCacheListener pathChildrenCacheListener) throws Exception{
        PathChildrenCache cache = new PathChildrenCache(curatorFramework, path, true) ;
        cache.start();
        /* PathChildrenCacheListener cacheListener = new PathChildrenCacheListener(){
		@Override
		public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
			logger.info("PathChildrenCacheListener path:{},event:{},childData:{}",path,event.getType().name(),JSON.toJSONString(event.getData()));
			if  (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {//增加节点
				
			} else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {//删除节点
				
			} else if (event.getType() == PathChildrenCacheEvent.Type.CHILD_UPDATED) {//节点更新
				
			}
		}
    };*/
        cache.getListenable().addListener(pathChildrenCacheListener);
        return cache;
	}
	
	/**
	 * 监控指定节点
	 * @param path
	 * @param nodeCacheListener
	 * @return
	 * @throws Exception
	 */
	public NodeCache wathchNode(String path,final NodeCacheListenerHandler nodeCacheListenerHandler) throws Exception{
		final NodeCache cache = new NodeCache(curatorFramework, path);
		cache.start();
		cache.getListenable().addListener(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				nodeCacheListenerHandler.nodeChanged(cache);
			}
		});
		return cache;
	}
	/**
	 * 关闭
	 * @param curatorFramework
	 */
	public  void colse(){
		curatorFramework.close();
	}
	private byte[] toBytes(String str){
		if (StringUtils.isNotEmpty(str)) {
			try {
				return str.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return  null;
	}
	private String toString(byte[] bytes){
		if (null != bytes) {
			try {
				return new String(bytes,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return  null;
	}
}


```