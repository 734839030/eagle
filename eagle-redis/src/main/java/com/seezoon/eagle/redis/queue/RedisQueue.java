package com.seezoon.eagle.redis.queue;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 队列
 * @author hdf
 *
 * @param <T>
 */
public interface RedisQueue<T> extends Runnable,InitializingBean,DisposableBean{
	 public void handleMessage(T value); 
}
