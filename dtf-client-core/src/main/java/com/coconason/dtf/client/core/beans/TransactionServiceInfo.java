package com.coconason.dtf.client.core.beans;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/21-17:15
 */
public class TransactionServiceInfo {

    private final static ThreadLocal<TransactionServiceInfo> current = new ThreadLocal<>();

    String id;
    JSONObject info;
    ActionType action;

    public String getId() {
        return id;
    }

    public JSONObject getInfo() {
        return info;
    }

    public ActionType getAction() {
        return action;
    }

    public static TransactionServiceInfo getCurrent() {
        return current.get();
    }

    public static void setCurrent(TransactionServiceInfo transactionServiceInfo){
        current.set(transactionServiceInfo);
    }

    public static TransactionServiceInfo newInstanceForRestful(String id, ActionType action, String groupId, Long groupMemberId, String url, Object obj){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberId",groupMemberId);
        info.put("url",url);
        info.put("obj",obj);
        return new TransactionServiceInfo(id,info,action);
    }

    public static TransactionServiceInfo newInstanceForSyncAdd(String id, ActionType action, String groupId, Long groupMemberId, Method method, Object[] args){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberId",groupMemberId);
        info.put("method",method);
        info.put("args",args);
        return new TransactionServiceInfo(id,info,action);
    }

    public static TransactionServiceInfo newInstanceForAsyncCommit(String id, ActionType action, String groupId){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        return new TransactionServiceInfo(id,info,action);
    }

    public static TransactionServiceInfo newInstanceWithGroupidSet(String id, ActionType action, String groupId, Set<Long> groupMemberSet){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberSet",groupMemberSet.toString());
        return new TransactionServiceInfo(id,info,action);
    }

    public static TransactionServiceInfo newInstanceForSubSccuessStrong(String id, ActionType action, String groupId, Set<Long> groupMemberSet,Long memberId){
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberSet",groupMemberSet.toString());
        info.put("memberId",memberId.toString());
        return new TransactionServiceInfo(id,info,action);
    }

    private TransactionServiceInfo(String id, JSONObject info, ActionType action) {
        this.id = id;
        this.info = info;
        this.action = action;
    }

}
