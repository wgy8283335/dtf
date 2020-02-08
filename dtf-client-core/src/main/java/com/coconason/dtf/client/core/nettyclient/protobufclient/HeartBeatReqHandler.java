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
 * @Author: Jason
 * @date: 2018/10/2-14:11
 */
@Component
final class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(HeartBeatReqHandler.class);
	
	private volatile ScheduledFuture<?> heartBeat;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		MessageProto.Message message = (MessageProto.Message) msg;
		if (message.getLength() != 2)
		{
			if (message.getAction() == MessageProto.Message.ActionType.LOGIN_RESP)
			{
				if (heartBeat == null)
				{
					heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx),
							0,
							30,
							TimeUnit.SECONDS);
				}
			}
			else if (message.getAction() == MessageProto.Message.ActionType.HEARTBEAT_RESP)
			{
				logger.debug("Client receive server heart beat message :" + message);
			}
			else
			{
				ctx.fireChannelRead(msg);
			}
		}
		else
		{
			ctx.fireExceptionCaught(new Throwable("No Header"));
		}
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
			closeHeartBeat();
		}

	}

	private void closeHeartBeat()
	{
		if (heartBeat != null)
		{
			heartBeat.cancel(true);
			heartBeat = null;
		}
	}

	private class HeartBeatTask implements Runnable
	{
		final ChannelHandlerContext ctx;

		public HeartBeatTask(final ChannelHandlerContext ctx)
		{
			this.ctx = ctx;
		}

		@Override
		public void run()
		{
			ctx.writeAndFlush(buildHeartBeat());
			logger.debug("Client send heart beat message to server : ----> " + heartBeat);
		}

	}

	private MessageProto.Message buildHeartBeat()
	{
		MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
		builder.setAction(MessageProto.Message.ActionType.HEARTBEAT_REQ);
		return builder.build();
	}

}
