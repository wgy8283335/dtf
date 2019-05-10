package com.coconason.dtf.manager.thread;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:09
 */
public final class ServerThreadLockCacheProxy implements Cache{

    private Cache<String,LockAndConditionInterface> cache;

    public ServerThreadLockCacheProxy() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(600, TimeUnit.SECONDS).maximumSize(1000000L).build();
    }
    @Override
    public LockAndConditionInterface getIfPresent(Object o) {
        return cache.getIfPresent(o);
    }

    @Override
    public LockAndConditionInterface get(Object o, Callable callable) throws ExecutionException {
        return cache.get((String)o,callable);
    }

    @Override
    public void put(Object o, Object o2) {
        cache.put((String)o,(LockAndConditionInterface)o2);
    }

    @Override
    public ImmutableMap getAllPresent(Iterable iterable) {
        return cache.getAllPresent(iterable);
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
