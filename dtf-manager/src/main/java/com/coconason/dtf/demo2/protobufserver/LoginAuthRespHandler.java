package com.coconason.dtf.demo2.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter
{
    // 白名单，暂时简单处理，写死了
    private String[] writeList = { "/127.0.0.1" };

    // 已经登录的IP缓存，防止重复登录
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
        // 如果是握手请求消息
        if (message.getLength() != 2 && message.getAction() == ActionType.LOGIN_REQ)
        {
            System.out.println("receive client login req : " + message);
            MessageProto.Message loginResp = null;
            String nodeIndex = ctx.channel().remoteAddress().toString().split(":")[0];
            // 判断是否重复登录
            if (nodeCheck.containsKey(nodeIndex))
            {
                loginResp = buildResponse("login_repeat");
            }
            else
            {
                // 判断是否属于白名单中的IP
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
                System.out.println("send login resp is : " + loginResp);
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
            // 异常发生时，把登录缓存清除
            nodeCheck.remove("/127.0.0.1");
        }

    }
}
