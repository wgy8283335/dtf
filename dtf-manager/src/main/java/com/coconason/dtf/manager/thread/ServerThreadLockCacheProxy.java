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
 * Proxy of cache for storing lock and condition.
 * 
 * @Author: Jason
 */
public final class ServerThreadLockCacheProxy implements Cache {
    
    private Cache<String, LockAndConditionInterface> cache;
    
    public ServerThreadLockCacheProxy() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(600, TimeUnit.SECONDS).maximumSize(1000000L).build();
    }
    
    @Override
    public LockAndConditionInterface getIfPresent(final Object o) {
        return cache.getIfPresent(o);
    }
    
    @Override
    public LockAndConditionInterface get(final Object o, final Callable callable) throws ExecutionException {
        return cache.get((String) o, callable);
    }
    
    @Override
    public void put(final Object o, final Object o2) {
        cache.put((String) o, (LockAndConditionInterface) o2);
    }
    
    @Override
    public ImmutableMap getAllPresent(final Iterable iterable) {
        return cache.getAllPresent(iterable);
    }
    
    @Override
    public void putAll(final Map map) {
        cache.putAll(map);
    }
    
    @Override
    public void invalidate(final Object o) {
        cache.invalidate(o);
    }
    
    @Override
    public void invalidateAll(final Iterable iterable) {
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
