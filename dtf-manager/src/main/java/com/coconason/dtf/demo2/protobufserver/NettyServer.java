package com.coconason.dtf.demo2.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.demo2.cache.MessageCache;
import com.coconason.dtf.demo2.service.Consumer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:38
 */
public class NettyServer
{

    public static void main(String[] args) throws Exception
    {
        new NettyServer().bind();
        new Thread(new Consumer()).start();

    }

    public void bind() throws Exception
    {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        final MessageCache messageCache = new MessageCache();
        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>()
                    {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception
                        {
                            // NettyMessageDecoder设置了单条消息最大值1MB,可以防止消息过大导致的内存溢出或者畸形码流，引发解码错位或内存分配失败
                            //ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4, -8, 0));
                            //ch.pipeline().addLast(new NettyMessageEncoder());
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ReadTimeoutHandler(50));
                            ch.pipeline().addLast(new LoginAuthRespHandler());
                            ch.pipeline().addLast(new ServerTransactionHandler(messageCache));
                            ch.pipeline().addLast(new HeartBeatRespHandler());
                        }
                    });

            ChannelFuture f = b.bind(18080).sync();
            System.out.println("Netty Server start ok! post is 18080");
            f.channel().closeFuture().sync();
        }
        finally
        {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }

}