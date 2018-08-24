package com.coconason.dtf.server.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

/**
 * @Author: Jason
 * @date: 2018/8/24-16:34
 */
@Component
public class MessageCache {
    private final Cache<String,Object> cache = CacheBuilder.newBuilder().maximumSize(1000000L).build();

    public Object get(String id){
        return cache.getIfPresent(id);
    }

    public void put(String id,Object msg){
        cache.put(id, msg);
    }

    public void clear(String id){
        cache.invalidate(id);
    }
}
