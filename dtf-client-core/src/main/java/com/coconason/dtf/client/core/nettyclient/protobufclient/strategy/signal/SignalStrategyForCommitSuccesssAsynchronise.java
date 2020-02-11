package com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.signal;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.google.common.cache.Cache;

public class SignalStrategyForCommitSuccesssAsynchronise implements SignalStrategy {

    @Override
    public void processSignal(Cache<String,ClientLockAndConditionInterface> threadLockCacheProxy, MessageProto.Message.ActionType action, ClientLockAndConditionInterface lc, JSONObject map, OperationType state, MessageProto.Message message) {
        ClientLockAndConditionInterface asyncFinallc = threadLockCacheProxy.getIfPresent(JSONObject.parseObject(message.getInfo().toString()).get("groupId").toString());
        asyncFinallc.setState(OperationType.COMMIT_SUCCESS_ASYNC);
        asyncFinallc.signal();
    }
}
