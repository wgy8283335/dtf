package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Abstract class of channel inbound handler adapter.
 * 
 * @Author: Jason
 */
public abstract class AbstractClientTransactionHandler extends ChannelInboundHandlerAdapter {

    /**
     * Send information of the serviceInfo object.
     * 
     * @param serviceInfo base transaction service information
     */
    abstract void sendMsg(BaseTransactionServiceInfo serviceInfo);
    
}
