package com.dtf.manager.protobufserver;

import com.dtf.common.protobuf.MessageProto;
import com.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Login authorization response handler.
 * 
 * @Author: wangguangyuan
 */
public final class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    
    private Logger logger = LoggerFactory.getLogger(LoginAuthRespHandler.class);
    
    /**
     * White list.
     */
    private String[] writeList = {"/127.0.0.1"};
    
    /**
     * The cache of the IP logged, in case duplicated log in.
     */
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap();

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
     * Responsible for login.
     * 
     * @param ctx channel handler context
     * @param msg message
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        MessageProto.Message message = (MessageProto.Message) msg;
        if (!(message.getLength() != 2 && message.getAction() == ActionType.LOGIN_REQ)) {
            ctx.fireChannelRead(msg);
            return;
        }
        logger.debug("receive client login req : " + message);
        MessageProto.Message loginResp = null;
        String nodeIndex = ctx.channel().remoteAddress().toString().split(":")[0];
        // Check whether is duplicated log in.
        if (nodeCheck.containsKey(nodeIndex)) {
            loginResp = buildResponse("login_repeat");
            return;
        }
        // Check whether the ip is in the white list.
        boolean tag = false;
        for (String ip : writeList) {
            if (ip.equals(nodeIndex)) {
                tag = true;
                break;
            }
        }
        if (tag) {
            nodeCheck.put(nodeIndex, true);
            loginResp = buildResponse("login_ok");
        } else {
            loginResp = buildResponse("login_fail");
        }
        ctx.writeAndFlush(loginResp);
        logger.debug("send login resp is : " + loginResp);
    }
    
    private MessageProto.Message buildResponse(final String result) {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setAction(ActionType.LOGIN_RESP);
        builder.setInfo(result);
        return builder.build();
    }
    
    /**
     * Caught exception.
     * 
     * @param ctx channel handler context
     * @param cause throwable
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        try {
            ctx.fireExceptionCaught(cause);
        } finally {
            nodeCheck.remove("/127.0.0.1");
        }
    }
    
}
