package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.TransactionMessageForAdding;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Cache of synchronous message.
 * 
 * @Author: Jason
 */
public final class MessageSyncCache implements MessageCacheInterface {
    
    private Logger logger = LoggerFactory.getLogger(MessageSyncCache.class);
    
    /**
     * Key store group id. 
     * Value store transaction message group interface.
     */
    private Cache<String, TransactionMessageGroupInterface> cache;
    
    public MessageSyncCache() {
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
    public synchronized void put(final String id, final TransactionMessageGroupInterface msg) {
        cache.put(id, msg);
    }
    
    /**
     * Put transaction message group in the cache.
     *
     * @param group transaction message group
     */
    @Override
    public synchronized void putDependsOnCondition(final TransactionMessageGroupInterface group) {
        Object element = cache.getIfPresent(group.getGroupId());
        if (element == null) {
            cache.put(group.getGroupId(), group);
        } else {
            TransactionMessageGroupInterface transactionMessageGroup = (TransactionMessageGroupInterface) element;
            TransactionMessageForAdding transactionMessageForAdding = (TransactionMessageForAdding) group.getMemberList().get(0);
            transactionMessageGroup.getMemberList().add(transactionMessageForAdding);
            transactionMessageGroup.getMemberSet().add(transactionMessageForAdding.getGroupMemberId());
            cache.put(transactionMessageGroup.getGroupId(), transactionMessageGroup);
        }
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
    public synchronized void invalidate(final Object id) {
        cache.invalidate(id);
    }
    
}
