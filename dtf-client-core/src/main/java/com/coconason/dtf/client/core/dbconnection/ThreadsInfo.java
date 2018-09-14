package com.coconason.dtf.client.core.dbconnection;

import org.springframework.stereotype.Component;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:09
 */
@Component(value="threadsInfo")
public class ThreadsInfo {

    private Cache<String,LockAndCondition> cache;

    public ThreadsInfo() {
        cache = CacheBuilder.newBuilder().maximumSize(1000000L).build();
    }

    public synchronized LockAndCondition get(String id){
        return cache.getIfPresent(id);
    }

    public synchronized void put(String id,LockAndCondition msg){
        cache.put(id, msg);
    }
}
