package com.dtf.client.core.beans.service;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto.Message.ActionType;

/**
 * TransactionServiceInfo is used to deliver the transaction information of client to dtf manager.
 * 
 * @author wangguangyuan
 */
public final class TransactionServiceInfo extends BaseTransactionServiceInfo {
    
    /**
     * service id.
     */
    private String id;
    
    /**
     * service information in json object.
     */
    private JSONObject info;
    
    /**
     * transaction action type.
     */
    private ActionType action;
    
    public TransactionServiceInfo(final String id, final JSONObject info, final ActionType action) {
        this.id = id;
        this.info = info;
        this.action = action;
    }
    
    /**
     * Get id.
     * 
     * @return id
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * Get Information.
     *
     * @return Information in JSONObject
     */
    @Override
    public JSONObject getInfo() {
        return info;
    }
    
    /**
     * Get Action.
     *
     * @return action type
     */
    @Override
    public ActionType getAction() {
        return action;
    }
    
}
