package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.MessageInfoInterface;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/8/24-16:34
 */
public final class MessageAsyncCacheProxy implements MessageCacheInterface {

    private Cache<String,TransactionMessageGroupInterface> cache;

    public MessageAsyncCacheProxy() {
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
    public void putDependsOnCondition(TransactionMessageGroupInterface groupAsync){
        Object o = cache.getIfPresent(groupAsync.getGroupId());
        if(o==null){
            cache.put(groupAsync.getGroupId(),groupAsync);
        }else{
            TransactionMessageGroupInterface transactionMessageGroupAsync = (TransactionMessageGroupInterface)o;
            if(transactionMessageGroupAsync.equals(groupAsync)){
                return;
            }
            Set<MessageInfoInterface> memberSet = groupAsync.getMemberSet();
            for(MessageInfoInterface messageInfo:memberSet){
                transactionMessageGroupAsync.addMember(messageInfo.getGroupMemberId(),messageInfo.getUrl(),messageInfo.getObj());
            }
            cache.put(transactionMessageGroupAsync.getGroupId(),transactionMessageGroupAsync);
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
