package com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.signal;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.google.common.cache.Cache;

/**
 * Process signal according to action type.
 *
 * @Author: Jason
 */
public class SignalStrategyForCommitSuccesssAsynchronise implements SignalStrategy {
    
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
    @Override
    public void processSignal(final Cache<String, ClientLockAndConditionInterface> threadLockCacheProxy, final MessageProto.Message.ActionType action,
                              final ClientLockAndConditionInterface lc, final JSONObject map, final OperationType state, final MessageProto.Message message) {
        ClientLockAndConditionInterface asyncFinallc = threadLockCacheProxy.getIfPresent(JSONObject.parseObject(message.getInfo().toString()).get("groupId").toString());
        asyncFinallc.setState(OperationType.COMMIT_SUCCESS_ASYNC);
        asyncFinallc.signal();
    }
}
