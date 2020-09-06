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

/** Component for work with redis DB cache data */
@Slf4j
@Component
public class CacheDAO {
  private final RedisTemplate<Object, Object> redisTemplate;
  private final RedisSettings redisSettings;

  CacheDAO(RedisTemplate<Object, Object> redisTemplate, RedisSettings redisSettings) {
    this.redisTemplate = redisTemplate;
    this.redisSettings = redisSettings;
  }

  /**
   * Check if exist this key in redis DB
   *
   * @param key This is key for check
   * @return return true if this key exist or false if not exist
   */
  public Boolean exist(String key) {
    log.debug("exist key: {}", key);
    return this.redisTemplate.hasKey(key);
  }

  /**
   * Cache check if exist this hash and key in redis DB
   *
   * @param hash This is hash for check
   * @param key This is key for check
   * @return return true if this hash and key exist or false if not exist
   */
  public Boolean hexist(String hash, String key) {
    log.debug("hexist hash: {} and key: {}", hash, key);
    if (key == null) {
      return false;
    }
    return this.redisTemplate.opsForHash().hasKey(hash, key);
  }

  /**
   * Cache add key with TTL and value to redis DB
   *
   * @param key This is key
   * @param value This is value for key
   * @param divider TThis is divider for divide default value getting from settings
   */
  public void setTtl(String key, Object value, Integer divider) {
    this.redisTemplate
        .boundValueOps(key)
        .set(value, Duration.ofSeconds(this.redisSettings.getCacheTtl() / divider));
  }

  /**
   * Cache add key and value to redis DB
   *
   * @param key This is key
   * @param value This is value for key
   */
  public void set(String key, Object value) {
    this.redisTemplate.boundValueOps(key).set(value);
  }

  /**
   * Cache add hash with key and value to redis DB
   *
   * @param hash This is hash for key
   * @param key This is key
   * @param value This is value for hash and key
   */
  public void hset(String hash, String key, Object value) {
    this.redisTemplate.opsForHash().put(hash, key, value);
  }
}
