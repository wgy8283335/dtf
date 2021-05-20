package com.dtf.client.core.nettyclient.protobufclient.strategy.signal;

import com.alibaba.fastjson.JSONObject;
import com.dtf.client.core.dbconnection.OperationType;
import com.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.dtf.common.protobuf.MessageProto;
import com.google.common.cache.Cache;

/**
 * Process signal according to action type.
 *
 * @author wangguangyuan
 */
public class SignalStrategyForWholeFailStrong implements SignalStrategy {
    
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
        JSONObject temp;
        if (null != map) {
            temp = map;
        } else {
            temp = JSONObject.parseObject(message.getInfo());
        }
        ClientLockAndConditionInterface lc2 = threadLockCacheProxy.getIfPresent(temp.get("groupId").toString());
        if (null == lc2) {
            return;
        }
        lc2.setState(OperationType.WHOLE_FAIL);
        lc2.signal();
    }
}
