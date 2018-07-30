package com.coconason.dtf.common.codec;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.StreamCorruptedException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;


public class NettyMarshallingDecoder extends MarshallingDecoder
{

	public NettyMarshallingDecoder(UnmarshallerProvider provider, int objectMaxSize) {
		super(provider, objectMaxSize);
	}

	public NettyMarshallingDecoder(UnmarshallerProvider provider) {
		super(provider);
	}
	@Override
	public Object decode(ChannelHandlerContext ctx, ByteBuf in)  {
		try {
			return super.decode(ctx, in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
