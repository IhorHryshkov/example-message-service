/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-13T09:35
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

  public void existOrIfNoneMatch(String key) {
    if (this.cacheDAO.exist(key)) {
      throw new ResponseIfNoneMatchException();
    }
  }

  public void setKeyForCheckWithTtlDivider(String key, Integer divider) {
    this.cacheDAO.setTtl(key, "", divider);
  }

  public void hexistOrIfNoneMatch(String key, String hash) {
    if (this.cacheDAO.hexist(key, hash)) {
      throw new ResponseIfNoneMatchException();
    }
  }

  public void hset(String key, String hash, Object value) {
    this.cacheDAO.hset(key, hash, value);
  }
}
