/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-13T09:35
 *     <p>Service for work with cache data in redis DB
 */
package com.example.ems.services;

import com.example.ems.database.dao.redis.CacheDAO;
import com.example.ems.network.controllers.exceptions.global.ResponseIfNoneMatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheService {
  private final CacheDAO cacheDAO;

  CacheService(CacheDAO cacheDAO) {
    this.cacheDAO = cacheDAO;
  }

  /**
   * Check ifNoneMatch key exist in redis DB if exist then do noting but if not then return
   * exception for return specific response in controller
   *
   * @param key Key ifNoneMatch
   * @throws ResponseIfNoneMatchException If ifNoneMatch key exist in DB
   */
  public void existOrIfNoneMatch(String key) {
    if (this.cacheDAO.exist(key)) {
      throw new ResponseIfNoneMatchException();
    }
  }

  /**
   * Add ifNoneMatch key to redis DB if values is wrong then return exception
   *
   * @param key Key ifNoneMatch
   * @param divider Divider for TTL this key
   * @throws IllegalArgumentException If some wrong in values
   */
  public void setKeyForCheckWithTtlDivider(String key, Integer divider) {
    this.cacheDAO.setTtl(key, "", divider);
  }

  /**
   * Check ifNoneMatch key and hash exist in redis DB if exist then do noting but if not then return
   * exception for return specific response in controller
   *
   * @param key Key ifNoneMatch
   * @param hash Hash value of ifNoneMatch key
   * @throws ResponseIfNoneMatchException If ifNoneMatch key and hash exist in DB
   */
  public void hexistOrIfNoneMatch(String key, String hash) {
    if (this.cacheDAO.hexist(key, hash)) {
      throw new ResponseIfNoneMatchException();
    }
  }

  /**
   * Add to ifNoneMatch key this hash value with data in redis DB if values is wrong then return
   * exception
   *
   * @param key Key ifNoneMatch
   * @param hash Hash value of ifNoneMatch key
   * @param value Data of ifNoneMatch key
   * @throws NullPointerException If some values is null
   */
  public void hset(String key, String hash, Object value) {
    this.cacheDAO.hset(key, hash, value);
  }
}
