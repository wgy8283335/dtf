package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.TransactionMessageForAdding;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/8/24-16:34
 */
public class MessageSyncCacheProxy implements MessageCacheInterface{

    private Cache<String,TransactionMessageGroupInterface> cache;

    public MessageSyncCacheProxy() {
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
    public void putDependsOnCondition(TransactionMessageGroupInterface group){
        Object element = cache.getIfPresent(group.getGroupId());
        if(element==null){
            cache.put(group.getGroupId(),group);
        }else{
            TransactionMessageGroupInterface transactionMessageGroup = (TransactionMessageGroupInterface) element;
            TransactionMessageForAdding transactionMessageForAdding = (TransactionMessageForAdding)group.getMemberList().get(0);
            transactionMessageGroup.getMemberList().add(transactionMessageForAdding);
            transactionMessageGroup.getMemberSet().add(transactionMessageForAdding.getGroupMemberId());
            cache.put(transactionMessageGroup.getGroupId(),transactionMessageGroup);
        }
    }
    @Override
    public long getSize(){
        return cache.size();
    }
    @Override
    public void invalidate(Object id){
        cache.invalidate(id);
    }

}
