package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.MessageInfoInterface;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Cache of message asynchronous.
 * 
 * @Author: Jason
 */
public final class MessageAsyncCache implements MessageCacheInterface {
    
    /** 
     * Key store group id. 
     * Value store transaction message group interface.
     */
    private Cache<String, TransactionMessageGroupInterface> cache;
    
    public MessageAsyncCache() {
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
     * Put transaction message group in the cache.
     * 
     * @param groupAsync transaction message group
     */
    @Override
    public void putDependsOnCondition(final TransactionMessageGroupInterface groupAsync) {
        Object o = cache.getIfPresent(groupAsync.getGroupId());
        if (o == null) {
            cache.put(groupAsync.getGroupId(), groupAsync);
            return;
        }
        TransactionMessageGroupInterface transactionMessageGroupAsync = (TransactionMessageGroupInterface) o;
        if (transactionMessageGroupAsync.equals(groupAsync)) {
            return;
        }
        Set<MessageInfoInterface> memberSet = groupAsync.getMemberSet();
        for (MessageInfoInterface messageInfo:memberSet) {
            transactionMessageGroupAsync.addMember(messageInfo.getGroupMemberId(), messageInfo.getUrl(), messageInfo.getObj());
        }
        cache.put(transactionMessageGroupAsync.getGroupId(), transactionMessageGroupAsync);
        return;
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
    
}
