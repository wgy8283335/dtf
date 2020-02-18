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
