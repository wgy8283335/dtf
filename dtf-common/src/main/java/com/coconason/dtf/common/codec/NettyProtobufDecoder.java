package com.coconason.dtf.common.codec;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import java.util.List;

/**
 * @Author: Jason
 * @date: 2018/7/30-10:13
 */
public class NettyProtobufDecoder extends ByteToMessageDecoder {
//    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        ByteBuf bodyByteBuf = byteBuf.readBytes();
//        return;
//    }
}
