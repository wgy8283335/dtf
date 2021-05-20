package com.dtf.manager.protobufserver.strategy;

import com.dtf.manager.protobufserver.ServerTransactionHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * Handle message strategy.
 *
 * @Author: wangguangyuan
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
