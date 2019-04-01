package com.coconason.dtf.client.core.beans;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;

/**
 * TransactionServiceInfo is used to deliver the transaction information of client to dtf manager.
 * @Author: Jason
 * @date: 2018/8/21-17:15
 */
public final class TransactionServiceInfo extends BaseTransactionServiceInfo{

    private String id;
    private JSONObject info;
    private ActionType action;

    @Override
    public String getId() {
        return id;
    }
    @Override
    public JSONObject getInfo() {
        return info;
    }
    @Override
    public ActionType getAction() {
        return action;
    }

    TransactionServiceInfo(String id, JSONObject info, ActionType action) {
        this.id = id;
        this.info = info;
        this.action = action;
    }

}
