package com.coconason.dtf.manager.protobufserver.strategy;

import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Handle message strategy.
 *
 * @Author: Jason
 */
public interface HandleMessageStrategy {
    
    /**
     * Handle message.
     * 
     * @param serverTransactionHandler server transaction handler
     * @param ctx channel handler context
     * @param msg message
     */
    void handleMessage(ServerTransactionHandler serverTransactionHandler, ChannelHandlerContext ctx, Object msg);
}
