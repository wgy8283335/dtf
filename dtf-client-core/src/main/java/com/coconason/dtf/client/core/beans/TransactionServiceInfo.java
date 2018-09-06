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

    public TransactionServiceInfo(String id, ActionType action, String groupId, Long groupMemberId, String url, Object obj) {
        this.id = id;
        this.info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberId",groupMemberId);
        info.put("url",url);
        info.put("obj",obj);
        this.action = action;
    }

    public TransactionServiceInfo(String id, ActionType action, String groupId, Long groupMemberId, Method method, Object[] args) {
        this.id = id;
        this.info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberId",groupMemberId);
        info.put("method",method);
        info.put("args",args);
        this.action = action;
    }
    public TransactionServiceInfo(String id, ActionType action, String groupId, Set<Long> groupMemberSet) {
        this.id = id;
        this.info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberSet",groupMemberSet.toString());
        this.action = action;
    }

    public TransactionServiceInfo(String id, ActionType action, String groupId, Set<Long> groupMemberSet,Long memberId) {
        this.id = id;
        this.info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberSet",groupMemberSet.toString());
        info.put("memberId",memberId.toString());
        this.action = action;
    }

    public TransactionServiceInfo(String id, JSONObject info, ActionType action) {
        this.id = id;
        this.info = info;
        this.action = action;
    }

    public TransactionServiceInfo(String id, ActionType action) {
        this.id = id;
        this.info = null;
        this.action = action;
    }

}
