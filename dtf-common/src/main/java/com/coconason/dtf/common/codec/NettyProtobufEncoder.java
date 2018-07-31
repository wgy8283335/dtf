package com.coconason.dtf.common.codec;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import com.google.protobuf.CodedInputStream;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.List;

/**
 * @Author: Jason
 * @date: 2018/7/30-10:13
 */
public class NettyProtobufEncoder extends MessageToByteEncoder<MessageLite> {
//    @Override
//    protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {
//        byte[] body = msg.toByteArray();
//        out.writeBytes(body);
//        return;
//    }

}
