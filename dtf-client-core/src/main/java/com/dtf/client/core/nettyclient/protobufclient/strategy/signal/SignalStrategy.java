package com.dtf.client.core.nettyclient.protobufclient.strategy.signal;

import com.alibaba.fastjson.JSONObject;
import com.dtf.client.core.dbconnection.OperationType;
import com.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.dtf.common.protobuf.MessageProto;
import com.google.common.cache.Cache;

/**
 * Process signal according to action type.
 *
 * @Author: wangguangyuan
 */
public interface SignalStrategy {
    
    /**
     * Process signal according to action type.
     * 
     * @param threadLockCacheProxy cache for thread lock
     * @param action action type
     * @param lc thread lock and condition
     * @param map JSONObject
     * @param state operation type
     * @param message message in proto
     */
    void processSignal(Cache<String, ClientLockAndConditionInterface> threadLockCacheProxy, MessageProto.Message.ActionType action, 
                              ClientLockAndConditionInterface lc, JSONObject map, OperationType state, MessageProto.Message message);
}
