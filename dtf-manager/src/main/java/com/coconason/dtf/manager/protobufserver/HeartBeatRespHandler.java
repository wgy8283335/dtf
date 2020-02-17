package com.coconason.dtf.manager.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Heart beat request handler.
 * 
 * @Author: Jason
 */
public final class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    
    private Logger logger = LoggerFactory.getLogger(HeartBeatRespHandler.class);
    
    /**
     * Receive heart beat message and send back heart beat message.
     * 
     * @param ctx channel handler context
     * @param msg message
     * @throws Exception exception
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        MessageProto.Message message = (MessageProto.Message) msg;
        if (message.getLength() != 2 && message.getAction() == ActionType.HEARTBEAT_REQ) {
            logger.debug("Receive client heart beat message");
            ctx.writeAndFlush(buildHeartBeat());
            logger.debug("Send heart beat message to client");
        } else {
            ctx.fireChannelRead(msg);
        }
    }
    
    private MessageProto.Message buildHeartBeat() {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setAction(ActionType.HEARTBEAT_RESP);
        return builder.build();
    }
    
    /**
     * Flush channel handler context.
     * 
     * @param ctx channel handler context
     */
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    /**
     * Caught exception.
     * 
     * @param ctx channel handler context
     * @param cause throwable
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        ctx.fireExceptionCaught(cause);
    }
    
}
