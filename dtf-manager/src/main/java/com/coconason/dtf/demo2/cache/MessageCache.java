package com.coconason.dtf.demo2.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

/**
 * @Author: Jason
 * @date: 2018/8/24-16:34
 */
@Component
public class MessageCache {

    public MessageCache() {
        cache = CacheBuilder.newBuilder().maximumSize(1000000L).build();
    }

    private Cache<Object,Object> cache;

    public Object get(Object id){
        return cache.getIfPresent(id);
    }

    public void put(Object id,Object msg){
        cache.put(id, msg);
    }

    public void clear(Object id){
        cache.invalidate(id);
    }

}
