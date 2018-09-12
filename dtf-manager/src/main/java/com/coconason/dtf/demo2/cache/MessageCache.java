package com.coconason.dtf.demo2.cache;

import com.coconason.dtf.demo2.message.MessageInfo;
import com.coconason.dtf.demo2.message.TransactionMessageGroup;
import com.coconason.dtf.demo2.message.TransactionMessageGroupAsync;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/24-16:34
 */
public class MessageCache {

    public MessageCache() {
        cache = CacheBuilder.newBuilder().maximumSize(1000000L).build();
    }

    private Cache<Object,Object> cache;

    public synchronized Object get(Object id){
        return cache.getIfPresent(id);
    }

    public synchronized void put(Object id,Object msg){
        cache.put(id, msg);
    }

    public synchronized void putDependsOnCondition(TransactionMessageGroup group){
        Object element = cache.getIfPresent(group.getGroupId());
        if(element==null){
            cache.put(group.getGroupId(),group);
        }else{
            TransactionMessageGroup transactionMessageGroup = (TransactionMessageGroup) element;
            transactionMessageGroup.getMemberList().add(group.getMemberList().get(0));
            transactionMessageGroup.getMemberSet().add(group.getMemberList().get(0).getGroupMemberId());
            cache.put(transactionMessageGroup.getGroupId(),transactionMessageGroup);
        }
    }

    public synchronized void putDependsOnConditionAsync(TransactionMessageGroupAsync groupAsync){
        Object o = cache.getIfPresent(groupAsync.getGroupId());
        if(o==null){
            cache.put(groupAsync.getGroupId(),groupAsync);
        }else{
            TransactionMessageGroupAsync transactionMessageGroupAsync = (TransactionMessageGroupAsync)o;
            if(transactionMessageGroupAsync.equals(groupAsync)){
                return;
            }
            Set<MessageInfo> memberSet = groupAsync.getMemberSet();
            for(MessageInfo messageInfo:memberSet){
                transactionMessageGroupAsync.addMember(messageInfo.getMemberId(),messageInfo.getUrl(),messageInfo.getObj());
            }
            cache.put(transactionMessageGroupAsync.getGroupId(),transactionMessageGroupAsync);
        }
    }

    public long getSize(){
        return cache.size();
    }

    public void clear(Object id){
        cache.invalidate(id);
    }

}
