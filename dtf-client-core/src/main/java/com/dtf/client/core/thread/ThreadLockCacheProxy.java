package com.dtf.client.core.thread;

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
 * @author wangguangyuan
 */
@Component
public final class ThreadLockCacheProxy implements Cache {
    
    private final Cache<String, ClientLockAndConditionInterface> cache;
    
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
    @SuppressWarnings("unchecked")
    @Override
    public ClientLockAndConditionInterface get(final Object o, final Callable callable) throws ExecutionException {
        return cache.get((String) o, callable);
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    @Override
    public void put(final Object o, final Object o2) {
        cache.put((String) o, (ClientLockAndConditionInterface) o2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void putAll(final Map map) {
        cache.putAll(map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invalidate(final Object o) {
        cache.invalidate(o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invalidateAll(final Iterable iterable) {
        cache.invalidateAll(iterable);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @SuppressWarnings("unchecked")
    @Override
    public long size() {
        return cache.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CacheStats stats() {
        return cache.stats();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ConcurrentMap asMap() {
        return cache.asMap();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void cleanUp() {
        cache.cleanUp();
    }
    
}
