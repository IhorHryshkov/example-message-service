package com.example.ems.services;


import com.example.ems.config.RedisSettings;
import com.example.ems.services.iface.MainService;
import com.example.ems.services.models.cache.AddIn;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class CacheService implements MainService<Object, Object, Object> {

    private final RedisSettings redisSettings;

    CacheService(RedisSettings redisSettings) {
        this.redisSettings = redisSettings;
    }

    @Override
    public Object add(Object data) {
        AddIn addIn = (AddIn) data;
        String etag = DigestUtils.sha256Hex(addIn.getPath());
        String base64path = Base64.getEncoder().encodeToString(addIn.getPath().getBytes());
        String key = String.format("%s:%s:%s", addIn.getHashName(), base64path, etag);

        return null;
    }

    @Override
    public Object update(Object data, Object o) {
        return null;
    }

    @Override
    public Object getById(Object o) {
        return null;
    }

    @Override
    public List<Object> all(Object params) {
        return null;
    }
}
