package com.coconason.dtf.client.core.dbconnection;

import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:09
 */
@Component(value="threadLockCacheProxy")
public class ThreadLockCacheProxy implements Cache{

    private Cache<String,ClientLockAndCondition> cache;

    public ThreadLockCacheProxy() {
        cache = CacheBuilder.newBuilder().maximumSize(1000000L).build();
    }

    public ClientLockAndCondition get(String id){
        return cache.getIfPresent(id);
    }

    @Override
    public Object getIfPresent(Object o) {
        return cache.getIfPresent(o);
    }

    @Override
    public Object get(Object o, Callable callable) throws ExecutionException {
        return cache.get((String)o,callable);
    }

    @Override
    public ImmutableMap getAllPresent(Iterable iterable) {
        return cache.getAllPresent(iterable);
    }

    @Override
    public void put(Object o, Object o2) {
        cache.put((String)o,(ClientLockAndCondition)o2);
    }

    @Override
    public void putAll(Map map) {
        cache.putAll(map);
    }

    @Override
    public void invalidate(Object o) {
        cache.invalidate(o);
    }

    @Override
    public void invalidateAll(Iterable iterable) {
        cache.invalidateAll(iterable);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public CacheStats stats() {
        return cache.stats();
    }

    @Override
    public ConcurrentMap asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }
}
