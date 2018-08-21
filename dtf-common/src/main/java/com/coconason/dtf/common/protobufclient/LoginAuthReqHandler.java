package com.coconason.dtf.common.protobufclient;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.constant.MessageType;
import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		MessageProto.Message message = buildLoginReq();
		ctx.writeAndFlush(message);
		System.out.println("客户端发送握手请求：" + message);
		ctx.fireChannelActive();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		MessageProto.Message message = (MessageProto.Message) msg;
		// 如果是握手应答消息，需要判断是否握手成功
		if (message.getLength() != 2 && message.getType() == MessageType.LOGIN_RESP)
		{
			if (message.getLength() != 2)
			{
				String loginResult = message.getInfo().toString();
				if (loginResult.equals("login_ok"))
				{
					System.out.println("Login is success :" + message);
					// 透传给后面的Handler处理
					ctx.fireChannelRead(msg);
				}
				else
				{
					// 握手失败，关闭连接
					ctx.close();
					System.out.println("握手失败，关闭连接");
				}
			}
			else
			{
				// 握手失败，关闭连接
				ctx.close();
				System.out.println("握手失败，关闭连接");
			}
		}
		else
		{
			// 如果不是握手应答消息，直接透传
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
	{
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		ctx.fireExceptionCaught(cause);
	}

	private MessageProto.Message buildLoginReq()
	{
		MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
		builder.setType(MessageType.LOGIN_REQ);
		return builder.build();
	}

}
