package com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.signal;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.thread.ClientLockAndCondition;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.client.core.thread.ThreadLockCacheProxy;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.google.common.cache.Cache;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SignalStrategyContextTest {
    
    @Test
    public void testProcessSignalAccordingToAction() {
        testByAction(ActionType.APPROVESUBMIT);
        testByAction(ActionType.APPROVESUBMIT_STRONG);
        testByAction(ActionType.WHOLE_SUCCESS_STRONG);
        testByAction(ActionType.WHOLE_FAIL_STRONG);
        testByAction(ActionType.CANCEL);
        testByAction(ActionType.ADD_SUCCESS_ASYNC);
        testByAction(ActionType.ADD_FAIL_ASYNC);
        testByAction(ActionType.COMMIT_SUCCESS_ASYNC);
    }
    
    private void testByAction(ActionType type) {
        ClientLockAndConditionInterface lc = new ClientLockAndCondition(new ReentrantLock(), OperationType.DEFAULT);
        Cache<String, ClientLockAndConditionInterface> threadLockCacheProxy = mock(ThreadLockCacheProxy.class);
        when(threadLockCacheProxy.getIfPresent(any())).thenReturn(lc);
        JSONObject map = new JSONObject();
        map.put("groupId", "1231");
        OperationType state = OperationType.DEFAULT;
        MessageProto.Message message = mock(MessageProto.Message.class);
        when(message.getInfo()).thenReturn(map.toJSONString());
        SignalStrategyContext.getInstance().processSignalAccordingToAction(threadLockCacheProxy, type, lc, map, state, message);
    }
    
}
