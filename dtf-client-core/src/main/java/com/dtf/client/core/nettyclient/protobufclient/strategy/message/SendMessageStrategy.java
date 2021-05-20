package com.dtf.client.core.nettyclient.protobufclient.strategy.message;

import com.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * Send message strategy interface.
 * 
 * @author wangguangyuan
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
