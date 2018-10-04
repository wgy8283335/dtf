package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author: Jason
 * @date: 2018/10/2-14:11
 */
public abstract class AbstractClientTransactionHandler extends ChannelInboundHandlerAdapter {
    abstract void sendMsg(BaseTransactionServiceInfo serviceInfo);
}
