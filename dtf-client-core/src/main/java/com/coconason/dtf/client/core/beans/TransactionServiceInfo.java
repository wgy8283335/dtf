package com.coconason.dtf.client.core.beans;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author: Jason
 * @date: 2018/8/21-17:15
 */
public class TransactionServiceInfo {

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

    public TransactionServiceInfo(String id, ActionType action, String groupId, Integer groupMemeberId, Method method, Object[] args) {
        this.id = id;
        this.info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemeberId",groupMemeberId);
        info.put("method",method);
        info.put("args",args);
        this.action = action;
    }
    public TransactionServiceInfo(String id, ActionType action, String groupId, List<Integer> groupMemberList) {
        this.id = id;
        this.info = new JSONObject();
        info.put("groupId",groupId);
        info.put("groupMemberList",groupMemberList.toString());
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
