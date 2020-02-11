package com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.message;

import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * Send message strategy interface.
 * 
 * @Author: Jason
 */
public interface SendMessageStrategy {
    
    /**
     * Send message according to action.
     * 
     * @param ctx channel handler context
     * @param serviceInfo base transction service info
     */
    void sendMsg(ChannelHandlerContext ctx, BaseTransactionServiceInfo serviceInfo);
}
