package com.coconason.dtf.client.core.thread;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * proxy of guava cache.
 * 
 * @Author: Jason
 */
@Component(value = "threadLockCacheProxy")
public final class ThreadLockCacheProxy implements Cache {
    
    private Cache<String, ClientLockAndConditionInterface> cache;
    
    public ThreadLockCacheProxy() {
        cache = CacheBuilder.newBuilder().maximumSize(1000000L).build();
    }
    
    /**
     * Get ClientLockAndCondition object from cache.
     * @param o key
     * @return client lock and condition
     */
    @Override
    public ClientLockAndConditionInterface getIfPresent(final Object o) {
        return cache.getIfPresent(o);
    }
    
    /**
     * Get ClientLockAndCondition object from cache.
     * If value exist, return the value.
     * If value not exist, tha callable parameter will be execute, and the result will be used as value..
     * 
     * @param o key
     * @param callable if value not exist, tha callable parameter will be execute
     * @return client lock and condition
     */
    @Override
    public ClientLockAndConditionInterface get(final Object o, final Callable callable) throws ExecutionException {
        return cache.get((String) o, callable);
    }
    
    @Override
    public ImmutableMap getAllPresent(final Iterable iterable) {
        return cache.getAllPresent(iterable);
    }

    /**
     * Put (key,value) pair into the map.
     * 
     * @param o key
     * @param o2 value
     */
    @Override
    public void put(final Object o, final Object o2) {
        cache.put((String) o, (ClientLockAndConditionInterface) o2);
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
