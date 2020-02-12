package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Hear beat request handler.
 * 
 * @Author: Jason
 */
@Component
final class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
    
    /**
     * Logger for HeartBeatReqHandler classs.
     */
    private Logger logger = LoggerFactory.getLogger(HeartBeatReqHandler.class);
    
    /**
     * Asynchronous thread execution result.
     */
    private volatile ScheduledFuture<?> heartBeat;
    
    /**
     * Responsible for hear beat.
     * 
     * @param ctx Channel handler context
     * @param msg received message
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        MessageProto.Message message = (MessageProto.Message) msg;
        if (message.getLength() == 2) {
            ctx.fireExceptionCaught(new Throwable("No Header"));
        }
        if (message.getAction() == MessageProto.Message.ActionType.LOGIN_RESP && heartBeat == null) {
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 30, TimeUnit.SECONDS);
        } else if (message.getAction() == MessageProto.Message.ActionType.HEARTBEAT_RESP) {
            logger.debug("Client receive server heart beat message :" + message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
    
    /**
     * Catch exception.
     * 
     * @param ctx channel handler context
     * @param cause throwable cause
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        try {
            ctx.fireExceptionCaught(cause);
        } finally {
            closeHeartBeat();
        }
    }
    
    private void closeHeartBeat() {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
    }
    
    private MessageProto.Message buildHeartBeat() {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setAction(MessageProto.Message.ActionType.HEARTBEAT_REQ);
        return builder.build();
    }
    
    private class HeartBeatTask implements Runnable {
        
        private final ChannelHandlerContext ctx;
        
        HeartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }
        
        @Override
        public void run() {
            ctx.writeAndFlush(buildHeartBeat());
            logger.debug("Client send heart beat message to server : ----> " + heartBeat);
        }
    }
    
}
