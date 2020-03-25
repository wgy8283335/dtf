package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.message.SendMessageStrategyContext;
import com.coconason.dtf.client.core.nettyclient.protobufclient.strategy.signal.SignalStrategyContext;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Client transaction handler.
 * 
 * @Author: Jason
 */
@Component
@ChannelHandler.Sharable
final class ClientTransactionHandler extends AbstractClientTransactionHandler {
    
    /**
     * Logger of ClientTransactionHandler class.
     */
    private Logger logger = LoggerFactory.getLogger(ClientTransactionHandler.class);

    /**
     * Cache of lock and condition.
     */
    @Autowired
    @Qualifier("threadLockCacheProxy")
    private Cache<String, ClientLockAndConditionInterface> threadLockCacheProxy;
    
    /**
     * Channel handler context used for sending message.
     */
    private ChannelHandlerContext ctx;
    
    /**
     * Initialize attribute ctx.
     * 
     * @param ctx channel handler context used for sending message
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        logger.debug("create connection-->" + this.ctx);
    }
    
    /**
     * Read message from channel.
     * 
     * @param ctx channel handler context used for sending message
     * @param msg message read by channel
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        logger.debug(msg.toString());
        MessageProto.Message message = (MessageProto.Message) msg;
        ActionType action = message.getAction();
        JSONObject map = null;
        ClientLockAndConditionInterface lc = null;
        OperationType state = null;
        if (action == ActionType.APPROVESUBMIT || action == ActionType.APPROVESUBMIT_STRONG || action == ActionType.CANCEL) {
            map = JSONObject.parseObject(message.getInfo());
            lc = threadLockCacheProxy.getIfPresent(map.get("groupId").toString());
            state = lc.getState();
        }
        SignalStrategyContext.getInstance().processSignalAccordingToAction(threadLockCacheProxy, action, lc, map, state, message);
        ctx.fireChannelRead(msg);
    }

    /**
     * Operation when read complete.
     * 
     * @param ctx channel handler context
     */
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * Catch exception.
     * 
     * @param ctx channel handler context
     * @param cause throwable
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    /**
     * Send message about base transaction service information.
     * 
     * @param serviceInfo base transaction service information
     */
    @Override
    public void sendMsg(final BaseTransactionServiceInfo serviceInfo) {
        SendMessageStrategyContext.getInstance().processSignalAccordingToAction(ctx, serviceInfo);
    }
    
}
