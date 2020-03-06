package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Login authorization request handler.
 * 
 * @Author: Jason
 */
@Component
final class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    
    /**
     * Logger for LoginAuthReqHandler class.
     */
    private Logger logger = LoggerFactory.getLogger(LoginAuthReqHandler.class);
    
    /**
     * Send login request message.
     * 
     * @param ctx channel handler context
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        MessageProto.Message message = buildLoginReq();
        ctx.writeAndFlush(message);
        ctx.fireChannelActive();
    }
    
    private MessageProto.Message buildLoginReq() {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setAction(MessageProto.Message.ActionType.LOGIN_REQ);
        return builder.build();
    }
    
    /**
     * Responsible for login and authorization.
     * 
     * @param ctx channel handler context
     * @param msg message
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        MessageProto.Message message = (MessageProto.Message) msg;
        if (!(message.getLength() != 2 && message.getAction() == MessageProto.Message.ActionType.LOGIN_RESP)) {
            ctx.fireChannelRead(msg);
            return;
        }
        if (message.getLength() == 2) {
            ctx.close();
            logger.debug("fail,close connection");
            return;
        }
        String loginResult = message.getInfo().toString();
        if ("login_ok".equals(loginResult)) {
            logger.debug("Login is success :" + message);
            if(message.getAction() != MessageProto.Message.ActionType.LOGIN_RESP){
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.close();
            logger.debug("fail,close connection");
        }
    }

    /**
     * Flush message by channel handler context.
     * 
     * @param ctx channel handler context
     */
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    /**
     * Send throwable cause by channel handler context.
     * 
     * @param ctx channel handler context
     * @param cause throwable cause
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
    
}
