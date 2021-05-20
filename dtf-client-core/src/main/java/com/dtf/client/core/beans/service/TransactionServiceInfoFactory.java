package com.dtf.client.core.beans.service;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto.Message.ActionType;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Transaction service information factory.
 * 
 * @Author: wangguangyuan
 */
public class TransactionServiceInfoFactory {
    
    /**
     * Create instance with restful request. 
     * 
     * @param id service id
     * @param action transaction action type
     * @param groupId group id
     * @param groupMemberId group member id
     * @param url url
     * @param obj object
     * @param httpAction http action
     * @return base transaction service information
     */
    public static BaseTransactionServiceInfo newInstanceForRestful(final String id, final ActionType action, final String groupId, 
                                                                   final Long groupMemberId, final String url, final Object obj, final String httpAction) {
        JSONObject info = new JSONObject();
        info.put("groupId", groupId);
        info.put("groupMemberId", groupMemberId);
        info.put("url", url);
        info.put("obj", obj);
        info.put("httpAction", httpAction);
        return new TransactionServiceInfo(id, info, action);
    }
    
    /**
     * Create instance with synchronous adding.
     * 
     * @param id service id
     * @param action transaction action type
     * @param groupId group id
     * @param groupMemberId group member id
     * @param method method
     * @param args input parameters of the method
     * @return base transaction service information
     */
    public static BaseTransactionServiceInfo newInstanceForSyncAdd(final String id, final ActionType action, final String groupId, final Long groupMemberId, final Method method, final Object[] args) {
        JSONObject info = new JSONObject();
        info.put("groupId", groupId);
        info.put("groupMemberId", groupMemberId);
        info.put("method", method);
        info.put("args", args);
        return new TransactionServiceInfo(id, info, action);
    }
    
    /**
     * Create instance with asynchronous commit. 
     *
     * @param id service id
     * @param action transaction action type
     * @param groupId group id
     * @param groupMemberSet group member set
     * @return base transaction service information
     */
    public static BaseTransactionServiceInfo newInstanceForAsyncCommit(final String id, final ActionType action, final String groupId, final Set<Long> groupMemberSet) {
        JSONObject info = new JSONObject();
        info.put("groupId", groupId);
        info.put("groupMemberSet", groupMemberSet.toString());
        return new TransactionServiceInfo(id, info, action);
    }
    
    /**
     * Create instance with group id set. 
     *
     * @param id service id
     * @param action transaction action type
     * @param groupId group id
     * @param groupMemberSet group member set
     * @return base transaction service information
     */
    public static BaseTransactionServiceInfo newInstanceWithGroupIdSet(final String id, final ActionType action, final String groupId, final Set<Long> groupMemberSet) {
        JSONObject info = new JSONObject();
        info.put("groupId", groupId);
        info.put("groupMemberSet", groupMemberSet.toString());
        return new TransactionServiceInfo(id, info, action);
    }
    
    /**
     * Create instance with member id. 
     *
     * @param id service id
     * @param action transaction action type
     * @param groupId group id
     * @param groupMemberSet group member set
     * @param memberId member id
     * @return base transaction service information
     */
    public static BaseTransactionServiceInfo newInstanceForSub(final String id, final ActionType action, final String groupId, final Set<Long> groupMemberSet, final Long memberId) {
        JSONObject info = new JSONObject();
        info.put("groupId", groupId);
        info.put("groupMemberSet", groupMemberSet.toString());
        info.put("memberId", memberId.toString());
        return new TransactionServiceInfo(id, info, action);
    }
    
    /**
     * Create instance with short message.
     * 
     * @param id service id
     * @param action transaction action type
     * @param groupId group id
     * @return base transaction service information
     */
    public static BaseTransactionServiceInfo newInstanceForShortMessage(final String id, final ActionType action, final String groupId) {
        JSONObject info = new JSONObject();
        info.put("groupId", groupId);
        return new TransactionServiceInfo(id, info, action);
    }
    
}
