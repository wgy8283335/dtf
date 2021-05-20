package com.dtf.client.core.nettyclient.protobufclient;

import com.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Abstract class of channel inbound handler adapter.
 * 
 * @Author: wangguangyuan
 */
public abstract class AbstractClientTransactionHandler extends ChannelInboundHandlerAdapter {

    /**
     * Send information of the serviceInfo object.
     * 
     * @param serviceInfo base transaction service information
     */
    abstract void sendMsg(BaseTransactionServiceInfo serviceInfo);
    
}
