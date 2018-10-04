package com.coconason.dtf.client.core.beans;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/10/4-12:17
 */
public class TransactionServiceInfoFactory {

    public static BaseTransactionServiceInfo newInstanceForRestful(String id, ActionType action, String groupId, Long groupMemberId, String url, Object obj){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberId",groupMemberId);
        info.put("url",url);
        info.put("obj",obj);
        return new TransactionServiceInfo(id,info,action);
    }

    public static BaseTransactionServiceInfo newInstanceForSyncAdd(String id, ActionType action, String groupId, Long groupMemberId, Method method, Object[] args){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberId",groupMemberId);
        info.put("method",method);
        info.put("args",args);
        return new TransactionServiceInfo(id,info,action);
    }

    public static BaseTransactionServiceInfo newInstanceForAsyncCommit(String id, ActionType action, String groupId, Set<Long> groupMemberSet){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberSet",groupMemberSet.toString());
        return new TransactionServiceInfo(id,info,action);
    }

    public static BaseTransactionServiceInfo newInstanceWithGroupidSet(String id, ActionType action, String groupId, Set<Long> groupMemberSet){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberSet",groupMemberSet.toString());
        return new TransactionServiceInfo(id,info,action);
    }

    public static BaseTransactionServiceInfo newInstanceForSub(String id, ActionType action, String groupId, Set<Long> groupMemberSet, Long memberId){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberSet",groupMemberSet.toString());
        info.put("memberId",memberId.toString());
        return new TransactionServiceInfo(id,info,action);
    }

    public static BaseTransactionServiceInfo newInstanceForShortMessage(String id, ActionType action, String groupId){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        return new TransactionServiceInfo(id,info,action);
    }
}
