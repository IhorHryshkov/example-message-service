/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-13T09:35
 */
package com.example.ems.services;

import com.example.ems.config.redis.RedisSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CacheService {
	private final RedisTemplate<Object, Object> redisTemplate;
	private final RedisSettings redisSettings;

	CacheService(RedisTemplate<Object, Object> redisTemplate, RedisSettings redisSettings) {
		this.redisTemplate = redisTemplate;
		this.redisSettings = redisSettings;
	}

	//	@Transactional
	public Boolean exist(String key) {
		log.debug("exist: {}", key);
		return this.redisTemplate.hasKey(key);
	}

//	@Transactional
	public void set(String key, Object value) {
		this.redisTemplate.boundValueOps(key).set(value, this.redisSettings.getCacheTtl() / 2, TimeUnit.SECONDS);
	}
}
