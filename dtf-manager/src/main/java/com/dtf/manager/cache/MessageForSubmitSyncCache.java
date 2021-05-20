package com.dtf.manager.cache;

import com.dtf.manager.message.TransactionMessageGroupInterface;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Cache of message for synchronous submit.
 * 
 * @author wangguangyuan
 */
public final class MessageForSubmitSyncCache implements MessageCacheInterface {
    
    /**
     * Key store group id. 
     * Value store transaction message group interface.
     */
    private Cache<String, TransactionMessageGroupInterface> cache;
    
    public MessageForSubmitSyncCache() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(600, TimeUnit.SECONDS).maximumSize(1000000L).build();
    }
    
    /**
     * Get TransactionMessageGroupInterface by group id.
     *
     * @param id group id
     * @return transaction message group
     */
    @Override
    public TransactionMessageGroupInterface get(final String id) {
        return cache.getIfPresent(id);
    }
    
    /**
     * Put group id and transaction message group in the cache.
     *
     * @param id group id
     * @param msg transaction message
     */
    @Override
    public void put(final String id, final TransactionMessageGroupInterface msg) {
        cache.put(id, msg);
    }
    
    /**
     * Get size of cache.
     *
     * @return size
     */
    @Override
    public long getSize() {
        return cache.size();
    }
    
    /**
     * Invalidate the id related element from the cache.
     *
     * @param id group id.
     */
    @Override
    public void invalidate(final Object id) {
        cache.invalidate(id);
    }
    
    @Override
    public void putDependsOnCondition(final TransactionMessageGroupInterface group) {
    }
    
}
