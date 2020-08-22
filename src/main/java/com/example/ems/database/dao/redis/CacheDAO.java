/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-13T09:35
 */
package com.example.ems.database.dao.redis;

import com.example.ems.config.redis.RedisSettings;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
    log.debug("exist key: {}", key);
    return this.redisTemplate.hasKey(key);
  }

  public Boolean hexist(String key, String hash) {
    log.debug("hexist hash: {} and key: {}", hash, key);
    if (hash == null) {
      return false;
    }
    return this.redisTemplate.opsForHash().hasKey(key, hash);
  }

  public void setTtl(String key, Object value, Integer divider) {
    this.redisTemplate
        .boundValueOps(key)
        .set(value, Duration.ofSeconds(this.redisSettings.getCacheTtl() / divider));
  }

  public void set(String key, Object value) {
    this.redisTemplate.boundValueOps(key).set(value);
  }

  public void hset(String key, String hash, Object value) {
    this.redisTemplate.opsForHash().put(key, hash, value);
  }
}
