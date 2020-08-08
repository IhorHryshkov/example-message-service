/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-13T09:35
 */
package com.example.ems.database.dao.redis;

import com.example.ems.config.redis.RedisSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CacheDAO {
	private final RedisTemplate<Object, Object> redisTemplate;
	private final RedisSettings redisSettings;

	CacheDAO(RedisTemplate<Object, Object> redisTemplate, RedisSettings redisSettings) {
		this.redisTemplate = redisTemplate;
		this.redisSettings = redisSettings;
	}

	public Boolean exist(String key) {
		log.debug("exist: {}", key);
		return this.redisTemplate.hasKey(key);
	}

	public void setTtl(String key, Object value, Integer divider) {
		this.redisTemplate.boundValueOps(key).set(value, this.redisSettings.getCacheTtl() / divider, TimeUnit.SECONDS);
	}

	public void set(String key, Object value) {
		this.redisTemplate.boundValueOps(key).set(value);
	}
}
