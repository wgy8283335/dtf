package com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.signal;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType.APPROVESUBMIT;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType.APPROVESUBMIT_STRONG;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType.WHOLE_SUCCESS_STRONG;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType.WHOLE_FAIL_STRONG;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType.CANCEL;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType.ADD_SUCCESS_ASYNC;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType.ADD_FAIL_ASYNC;
import static com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType.COMMIT_SUCCESS_ASYNC;

public class SignalStrategyContext {
    
    private Map<ActionType,SignalStrategy> map;
    
    private SignalStrategyContext() {
        map = new HashMap<>();
        map.put(APPROVESUBMIT, new SignalStrategyForApproveSubmit());
        map.put(APPROVESUBMIT_STRONG, new SignalStrategyForApproveSubmitStrong());
        map.put(WHOLE_SUCCESS_STRONG, new SignalStrategyForWholeSuccessStrong());
        map.put(WHOLE_FAIL_STRONG, new SignalStrategyForWholeFailStrong());
        map.put(CANCEL, new SignalStrategyForCancel());
        map.put(ADD_SUCCESS_ASYNC, new SignalStrategyForCancel());
        map.put(ADD_FAIL_ASYNC, new SignalStrategyForAddFailAsynchronise());
        map.put(COMMIT_SUCCESS_ASYNC, new SignalStrategyForAddSuccessAsynchronise());
    }
    
    private static class SingleHolder {
        private static final SignalStrategyContext INSTANCE = new SignalStrategyContext();
    }
    
    public static final SignalStrategyContext getInstance() {
        return SingleHolder.INSTANCE;
    }
    
    public void processSignalAccordingToAction(Cache<String, ClientLockAndConditionInterface> threadLockCacheProxy, 
                                               MessageProto.Message.ActionType action, ClientLockAndConditionInterface lc, JSONObject map, 
                                               OperationType state, MessageProto.Message message){
        SignalStrategy signalStrategy = this.map.get(action);
        signalStrategy.processSignal(threadLockCacheProxy, action, lc, map, state, message);
    }
    
}
