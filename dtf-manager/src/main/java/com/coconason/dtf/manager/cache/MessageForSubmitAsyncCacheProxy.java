package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/8/24-16:34
 */
public class MessageForSubmitAsyncCacheProxy implements MessageCacheInterface{

    private Cache<String,TransactionMessageGroupInterface> cache;

    public MessageForSubmitAsyncCacheProxy() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(600, TimeUnit.SECONDS).maximumSize(1000000L).build();
    }
    @Override
    public TransactionMessageGroupInterface get(String id){
        return cache.getIfPresent(id);
    }

    @Override
    public void put(String id,TransactionMessageGroupInterface msg){
        cache.put(id, msg);
    }

    @Override
    public void putDependsOnCondition(TransactionMessageGroupInterface group) {
    }

    @Override
    public long getSize() {
        return cache.size();
    }

    @Override
    public void invalidate(Object id) {
        cache.invalidate(id);
    }
}
