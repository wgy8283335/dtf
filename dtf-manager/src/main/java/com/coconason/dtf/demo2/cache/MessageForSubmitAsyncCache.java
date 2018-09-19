package com.coconason.dtf.demo2.cache;

import com.coconason.dtf.demo2.message.TransactionMessageForSubmit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @Author: Jason
 * @date: 2018/8/24-16:34
 */
public class MessageForSubmitAsyncCache {

    private Cache<String,TransactionMessageForSubmit> cache;

    public MessageForSubmitAsyncCache() {
        cache = CacheBuilder.newBuilder().maximumSize(1000000L).build();
    }

    public synchronized TransactionMessageForSubmit get(String id){
        return cache.getIfPresent(id);
    }

    public synchronized void put(TransactionMessageForSubmit msg){
        cache.put(msg.getGroupId(), msg);
    }

    public long getSize(){
        return cache.size();
    }

    public void clear(String id){
        cache.invalidate(id);
    }

}
