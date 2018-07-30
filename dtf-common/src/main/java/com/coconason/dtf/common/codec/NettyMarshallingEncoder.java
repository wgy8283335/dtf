package com.coconason.dtf.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import org.jboss.marshalling.Marshaller;


public class NettyMarshallingEncoder extends MarshallingEncoder
{

	/**
	 * Creates a new encoder.
	 *
	 * @param provider the {@link MarshallerProvider} to use
	 */
	public NettyMarshallingEncoder(MarshallerProvider provider)
	{
		super(provider);
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)  {
		try {
			super.encode(ctx, msg, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}