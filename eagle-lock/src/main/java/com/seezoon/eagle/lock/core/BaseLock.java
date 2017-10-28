package com.seezoon.eagle.lock.core;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.seezoon.eagle.core.service.BaseService;

/**
 * 锁作为单独一层
 * @author hdf
 *
 */
public class BaseLock extends BaseService{

	@Autowired
	protected RedissonClient redissonClient;
}
