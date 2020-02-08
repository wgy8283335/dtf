package com.coconason.dtf.manager.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public final class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(LoginAuthRespHandler.class);
    
    /**
     * White list.
     */
    private String[] writeList = { "/127.0.0.1" };

    /**
     * The cache of the IP logged, in case duplicated log in.
     */
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        MessageProto.Message message = (MessageProto.Message) msg;
        if (message.getLength() != 2 && message.getAction() == ActionType.LOGIN_REQ)
        {
            logger.debug("receive client login req : " + message);
            MessageProto.Message loginResp = null;
            String nodeIndex = ctx.channel().remoteAddress().toString().split(":")[0];
            // Check whether is duplicated log in.
            if (nodeCheck.containsKey(nodeIndex))
            {
                loginResp = buildResponse("login_repeat");
            }
            else
            {
                // Check whether the ip is in the white list.
                boolean tag = false;
                for (String ip : writeList)
                {
                    if (ip.equals(nodeIndex))
                    {
                        tag = true;
                        break;
                    }
                }
                if (tag)
                {
                    nodeCheck.put(nodeIndex, true);
                    loginResp = buildResponse("login_ok");
                }
                else
                {
                    loginResp = buildResponse("login_fail");
                }
                ctx.writeAndFlush(loginResp);
                logger.debug("send login resp is : " + loginResp);
            }
        }
        else
        {
            ctx.fireChannelRead(msg);
        }

    }

    private MessageProto.Message buildResponse(String result)
    {
        MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
        builder.setAction(ActionType.LOGIN_RESP);
        builder.setInfo(result);
        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        try
        {
            ctx.fireExceptionCaught(cause);
        }
        finally
        {
            // Clear the cache when exception happens
            nodeCheck.remove("/127.0.0.1");
        }

    }
}
