package com.coconason.dtf.manager.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:33
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        MessageProto.Message message = (MessageProto.Message) msg;
        if (message.getLength() != 2 && message.getAction() == ActionType.HEARTBEAT_REQ)
        {
            System.out.println("Receive client heart beat message");
            ctx.writeAndFlush(buildHeartBeat());
            System.out.println("Send heart beat message to client");
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        ctx.fireExceptionCaught(cause);
    }

    private MessageProto.Message buildHeartBeat()
    {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setAction(ActionType.HEARTBEAT_RESP);
        return builder.build();
    }
}
