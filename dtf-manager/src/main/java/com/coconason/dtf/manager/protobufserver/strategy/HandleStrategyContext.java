package com.coconason.dtf.manager.protobufserver.strategy;

import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle Strategy Context.
 *
 * @Author: Jason
 */
public final class HandleStrategyContext {
    
    private Map<ActionType, HandleMessageStrategy> map;
    
    private HandleStrategyContext() {
        map = new HashMap<>();
        map.put(ActionType.ADD, new HandleMessageForAdd());
        map.put(ActionType.ADD_ASYNC, new HandleMessageForAddAsynchronous());
        map.put(ActionType.ADD_STRONG, new HandleMessageForAddStrong());
        map.put(ActionType.APPLYFORSUBMIT, new HandleMessageForApplySubmit());
        map.put(ActionType.APPLYFORSUBMIT_STRONG, new HandleMessageForApplySubmitStrong());
        map.put(ActionType.ASYNC_COMMIT, new HandleMessageForAsynchronousCommit());
        map.put(ActionType.CANCEL, new HandleMessageForCancel());
        map.put(ActionType.SUB_FAIL, new HandleMessageForSubFail());
        map.put(ActionType.SUB_FAIL_STRONG, new HandleMessageForSubFailStrong());
        map.put(ActionType.SUB_SUCCESS, new HandleMessageForSubSuccess());
        map.put(ActionType.SUB_SUCCESS_STRONG, new HandleMessageForSubSuccessStrong());
        map.put(ActionType.WHOLE_FAIL_STRONG_ACK, new HandleMessageForWholeFailStrongAck());
        map.put(ActionType.WHOLE_SUCCESS_STRONG_ACK, new HandleMessageForWholeSuccessStrongAck());
    }
    
    /**
     * Get single instance.
     *
     * @return handle message strategy context instance
     */
    public static HandleStrategyContext getInstance() {
        return SingleHolder.INSTANCE;
    }
    
    /**
     * Handle message according to action.
     * 
     * @param actionType action type
     * @param serverTransactionHandler server transaction handler
     * @param ctx channel handler context
     * @param msg message
     */
    public void handleMessageAccordingToAction(final ActionType actionType, final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        HandleMessageStrategy handleMessageStrategy = map.get(actionType);
        handleMessageStrategy.handleMessage(serverTransactionHandler, ctx, msg);
    }
    
    private static class SingleHolder {
        private static final HandleStrategyContext INSTANCE = new HandleStrategyContext();
    }
    
}
