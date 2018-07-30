package com.coconason.dtf.common.server;

import com.coconason.dtf.common.object.Header;
import com.coconason.dtf.common.object.MessageType;
import com.coconason.dtf.common.object.NettyMessage;
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

        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_REQ)
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

    private NettyMessage buildHeartBeat()
    {
        NettyMessage heartBeat = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP);
        heartBeat.setHeader(header);
        heartBeat.setBody(null);
        return heartBeat;
    }
}
