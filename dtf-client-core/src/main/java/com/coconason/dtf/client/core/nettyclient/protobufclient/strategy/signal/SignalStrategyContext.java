package com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.signal;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.google.common.cache.Cache;

import java.util.HashMap;
import java.util.Map;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;

/**
 * Signal strategy context.
 *
 * @Author: Jason
 */
public final class SignalStrategyContext {
    
    private Map<ActionType, SignalStrategy> map;
    
    private SignalStrategyContext() {
        map = new HashMap<>();
        map.put(ActionType.APPROVESUBMIT, new SignalStrategyForApproveSubmit());
        map.put(ActionType.APPROVESUBMIT_STRONG, new SignalStrategyForApproveSubmitStrong());
        map.put(ActionType.WHOLE_SUCCESS_STRONG, new SignalStrategyForWholeSuccessStrong());
        map.put(ActionType.WHOLE_FAIL_STRONG, new SignalStrategyForWholeFailStrong());
        map.put(ActionType.CANCEL, new SignalStrategyForCancel());
        map.put(ActionType.ADD_SUCCESS_ASYNC, new SignalStrategyForCancel());
        map.put(ActionType.ADD_FAIL_ASYNC, new SignalStrategyForAddFailAsynchronise());
        map.put(ActionType.COMMIT_SUCCESS_ASYNC, new SignalStrategyForAddSuccessAsynchronise());
    }
    
    /**
     * Get SignalStrategyContext instance.
     * 
     * @return signal strategy context
     */
    public static SignalStrategyContext getInstance() {
        return SingleHolder.INSTANCE;
    }
    
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
    public void processSignalAccordingToAction(final Cache<String, ClientLockAndConditionInterface> threadLockCacheProxy,
                                               final MessageProto.Message.ActionType action, final ClientLockAndConditionInterface lc, final JSONObject map,
                                               final OperationType state, final MessageProto.Message message) {
        SignalStrategy signalStrategy = this.map.get(action);
        signalStrategy.processSignal(threadLockCacheProxy, action, lc, map, state, message);
    }
    
    private static class SingleHolder {
        private static final SignalStrategyContext INSTANCE = new SignalStrategyContext();
    }
}
