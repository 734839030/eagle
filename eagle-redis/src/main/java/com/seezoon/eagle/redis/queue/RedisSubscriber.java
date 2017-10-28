package com.seezoon.eagle.redis.queue;

/**
 * 订阅者接口
 * @author hdf
 *
 * @param <T>
 */
public interface RedisSubscriber<T> {
     /**
      * redis 默认的方法名，如果自定义在配置listener时需要指定名称
      * @param value
      */
	 public void handleMessage(T value); 
}
