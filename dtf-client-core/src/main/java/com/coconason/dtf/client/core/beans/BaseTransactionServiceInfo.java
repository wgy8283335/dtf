package com.coconason.dtf.client.core.beans;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;

/**
 * @Author: Jason
 * @date: 2018/10/4-12:15
 */
public abstract class BaseTransactionServiceInfo {

    private final static ThreadLocal<BaseTransactionServiceInfo> current = new ThreadLocal<>();


    public static BaseTransactionServiceInfo getCurrent() {
        return current.get();
    }

    public static void setCurrent(BaseTransactionServiceInfo transactionServiceInfo){
        current.set(transactionServiceInfo);
    }

    public abstract String getId();

    public abstract JSONObject getInfo();

    public abstract MessageProto.Message.ActionType getAction();
}
