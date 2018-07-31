package com.coconason.dtf.common.codec;

import com.coconason.dtf.common.object.Header;
import com.coconason.dtf.common.object.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder
{

	NettyMarshallingDecoder marshallingDecoder;

	public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
							   int initialBytesToStrip) throws IOException
	{
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);

		marshallingDecoder = MarshallingCodeCFactory.buildMarshallingDecoder();
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception
	{
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null)
		{
			System.out.println("解码器返回null");
			return null;
		}

		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setCrcCode(frame.readInt());
		header.setLength(frame.readInt());
		header.setSessionID(frame.readLong());
		header.setType(frame.readByte());
		header.setPriority(frame.readByte());

		// 得到附件个数
		int attachSize = frame.readInt();
		if (attachSize > 0)
		{
			Map<String, Object> attachment = new HashMap<>();
			for (int i = 0; i < attachSize; i++)
			{
				int keySize = frame.readInt();
				byte[] keyArray = new byte[keySize];
				frame.readBytes(keyArray);
				String key = new String(keyArray, "UTF-8");
				Object val = marshallingDecoder.decode(ctx, frame);
				attachment.put(key, val);
			}
			header.setAttachment(attachment);
		}

		if (frame.readableBytes() > 0)
		{
			message.setBody(marshallingDecoder.decode(ctx, frame));
		}
		message.setHeader(header);
		return message;

	}
}