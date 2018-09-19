package com.coconason.dtf.demo2.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.demo2.cache.MessageAsyncCache;
import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.cache.MessageForSubmitSyncCache;
import com.coconason.dtf.demo2.cache.MessageSyncCache;
import com.coconason.dtf.demo2.service.ConsumerRunnable;
import com.coconason.dtf.demo2.threadpools.ThreadPoolForServer;
import com.coconason.dtf.demo2.utils.PropertiesReader;
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
        MessageAsyncQueue messageAsyncQueue = new MessageAsyncQueue();
        String classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        PropertiesReader propertiesReader = new PropertiesReader(classpath+"config.properties");
        ThreadPoolForServer threadPoolForServer = new ThreadPoolForServer(
                Integer.valueOf(propertiesReader.getProperty("corePoolSize")),
                Integer.valueOf(propertiesReader.getProperty("maximumPoolSize")),
                Integer.valueOf(propertiesReader.getProperty("keepAliveTime")),
                Integer.valueOf(propertiesReader.getProperty("capacity")));
        threadPoolForServer.addTask(new ConsumerRunnable(messageAsyncQueue));
        new NettyServer().bind(messageAsyncQueue,threadPoolForServer,Integer.valueOf(propertiesReader.getProperty("port")));
    }

    public void bind(MessageAsyncQueue messageAsyncQueueTemp,ThreadPoolForServer threadPoolForServerTemp,Integer port) throws Exception
    {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        final MessageSyncCache messageSyncCache = new MessageSyncCache();
        final MessageAsyncCache messageAsyncCache = new MessageAsyncCache();
        final MessageForSubmitSyncCache messageForSubmitSyncCache = new MessageForSubmitSyncCache();
        final MessageAsyncQueue messageAsyncQueue = messageAsyncQueueTemp;
        final ThreadPoolForServer threadPoolForServer = threadPoolForServerTemp;
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
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new ReadTimeoutHandler(50));
                            ch.pipeline().addLast(new LoginAuthRespHandler());
                            ch.pipeline().addLast(new ServerTransactionHandler(messageSyncCache,messageAsyncCache,messageAsyncQueue,threadPoolForServer,messageForSubmitSyncCache));
                            ch.pipeline().addLast(new HeartBeatRespHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
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