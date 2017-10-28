package com.seezoon.eagle.redis.queue;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;

/**
 * 队列
 * @author hdf
 *
 * @param <T>
 */
public abstract class AbstractRedisQueue<K, V> implements RedisQueue<V>{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected boolean isRun = true;
	
	public abstract ListOperations<K, V> getListOperations() ;
	public abstract void handleMessage(V value);
	/**
	 * queue 名称就是redis list 的key
	 * @return
	 */
    public abstract K getKeyName();
    /**
     * 一次获取等待的超时时间 毫秒
     * @return
     */
    public abstract long getTimeout();
	@Override
	public void run() {
		while(isRun){
			V value = getListOperations().leftPop(getKeyName(), getTimeout(), TimeUnit.MILLISECONDS);
			if (null !=value) {
				this.handleMessage(value);
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		isRun = false;		
		logger.debug(getKeyName()+ " queue stop");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
		logger.debug(getKeyName()+ " queue start");
	} 
}
