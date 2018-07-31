package com.coconason.dtf.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import io.netty.handler.codec.protobuf.ProtobufDecoder;


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
