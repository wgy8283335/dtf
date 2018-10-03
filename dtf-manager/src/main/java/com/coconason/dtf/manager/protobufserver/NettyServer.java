package com.coconason.dtf.manager.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.*;
import com.coconason.dtf.manager.service.ConsumerFailingAsyncRequestRunnable;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import com.coconason.dtf.manager.threadpools.ThreadPoolForServer;
import com.coconason.dtf.manager.utils.PropertiesReader;
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
    private static Boolean isHealthy = false;

    public static Boolean isHealthy() {
        return isHealthy;
    }

    public static void main(String[] args) throws Exception
    {
        MessageAsyncQueueProxy messageAsyncQueueProxy = new MessageAsyncQueueProxy();
        String classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        PropertiesReader propertiesReader = new PropertiesReader(classpath+"config.properties");
        ThreadPoolForServer threadPoolForServer = new ThreadPoolForServer(
                Integer.valueOf(propertiesReader.getProperty("corePoolSize")),
                Integer.valueOf(propertiesReader.getProperty("maximumPoolSize")),
                Integer.valueOf(propertiesReader.getProperty("keepAliveTime")),
                Integer.valueOf(propertiesReader.getProperty("capacity")));
        threadPoolForServer.execute(new ConsumerFailingAsyncRequestRunnable(messageAsyncQueueProxy));
        new NettyServer().bind(messageAsyncQueueProxy,threadPoolForServer,Integer.valueOf(propertiesReader.getProperty("port")));
    }

    public void bind(MessageAsyncQueueProxy messageAsyncQueueProxyTemp, ThreadPoolForServer threadPoolForServerTemp, Integer port) throws Exception
    {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        final MessageSyncCacheProxy messageSyncCacheProxy = new MessageSyncCacheProxy();
        final MessageAsyncCacheProxy messageAsyncCacheProxy = new MessageAsyncCacheProxy();
        final MessageForSubmitSyncCacheProxy messageForSubmitSyncCacheProxy = new MessageForSubmitSyncCacheProxy();
        final MessageForSubmitAsyncCacheProxy messageForSubmitAsyncCacheProxy = new MessageForSubmitAsyncCacheProxy();
        final MessageAsyncQueueProxy messageAsyncQueueProxy = messageAsyncQueueProxyTemp;
        final ThreadPoolForServer threadPoolForServer = threadPoolForServerTemp;
        final ServerThreadLockCacheProxy serverThreadLockCacheProxy = new ServerThreadLockCacheProxy();
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
                            ch.pipeline().addLast(new ServerTransactionHandler(messageSyncCacheProxy, messageAsyncCacheProxy, messageAsyncQueueProxy,threadPoolForServer, messageForSubmitSyncCacheProxy, messageForSubmitAsyncCacheProxy, serverThreadLockCacheProxy));
                            ch.pipeline().addLast(new HeartBeatRespHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            isHealthy = true;
            System.out.println("Netty Server start ok! post is 18080");
            f.channel().closeFuture().sync();
            isHealthy = false;
        }
        finally
        {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }

}