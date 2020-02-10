package com.coconason.dtf.client.core.beans;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;

/**
 * Abstract class of base transaction service information.
 * 
 * @Author: Jason
 */
public abstract class BaseTransactionServiceInfo {

    /**
     * Current is a ThreadLocal variable,each thread has its on BaseTransactionServiceInfo variable.
     */
    private static final ThreadLocal<BaseTransactionServiceInfo> CURRENT = new ThreadLocal<>();

    /**
     * Get thread local variable from CURRENT.
     * 
     * @return base transaction service information
     */
    public static BaseTransactionServiceInfo getCurrent() {
        return CURRENT.get();
    }

    /**
     * Set thread local variable from CURRENT.
     * 
     * @param transactionServiceInfo base transaction service information
     */
    public static void setCurrent(final BaseTransactionServiceInfo transactionServiceInfo) {
        CURRENT.set(transactionServiceInfo);
    }

    /**
     * Get id.
     * 
     * @return id
     */
    public abstract String getId();

    /**
     * Get Information.
     * 
     * @return Information in JSONObject
     */
    public abstract JSONObject getInfo();
    
    /**
     * Get Action.
     * 
     * @return action type
     */
    public abstract MessageProto.Message.ActionType getAction();
}
