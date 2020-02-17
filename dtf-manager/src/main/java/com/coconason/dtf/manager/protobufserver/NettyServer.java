package com.coconason.dtf.manager.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageAsyncCache;
import com.coconason.dtf.manager.cache.MessageAsyncQueueProxy;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.cache.MessageForSubmitAsyncCache;
import com.coconason.dtf.manager.cache.MessageForSubmitSyncCache;
import com.coconason.dtf.manager.cache.MessageSyncCache;
import com.coconason.dtf.manager.service.ConsumerFailingAsyncRequestRunnable;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import com.coconason.dtf.manager.threadpools.ThreadPoolForServerProxy;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty Server.
 * 
 * @Author: Jason
 */
public final class NettyServer {
    
    private static Boolean isHealthy = false;
    
    private Logger logger = LoggerFactory.getLogger(NettyServer.class);
    
    /**
     * Initialize the server.
     *
     * @param args parameters
     * @throws Exception exception
     */
    public static void main(final String[] args) throws Exception {
        MessageAsyncQueueProxy messageAsyncQueueProxy = new MessageAsyncQueueProxy();
        String classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        PropertiesReader propertiesReader = new PropertiesReader(classpath + "config.properties");
        ThreadPoolForServerProxy threadPoolForServerProxy = ThreadPoolForServerProxy.initialize();
        threadPoolForServerProxy.execute(new ConsumerFailingAsyncRequestRunnable(messageAsyncQueueProxy));
        new NettyServer().bind(messageAsyncQueueProxy, threadPoolForServerProxy, Integer.valueOf(propertiesReader.getProperty("port")));
    }
    
    /**
     * Return isHealthy.
     * 
     * @return whether is healthy.
     */
    public static Boolean isHealthy() {
        return isHealthy;
    }
    
    private void bind(final MessageAsyncQueueProxy messageAsyncQueueProxyTemp, final ThreadPoolForServerProxy threadPoolForServerProxyTemp, final Integer port) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        final MessageCacheInterface messageSyncCache = new MessageSyncCache();
        final MessageCacheInterface messageAsyncCache = new MessageAsyncCache();
        final MessageCacheInterface messageForSubmitSyncCache = new MessageForSubmitSyncCache();
        final MessageCacheInterface messageForSubmitAsyncCache = new MessageForSubmitAsyncCache();
        final MessageAsyncQueueProxy messageAsyncQueueProxy = messageAsyncQueueProxyTemp;
        final ThreadPoolForServerProxy threadPoolForServerProxy = threadPoolForServerProxyTemp;
        final ServerThreadLockCacheProxy serverThreadLockCacheProxy = new ServerThreadLockCacheProxy();
        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, work)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                
                @Override
                protected void initChannel(final SocketChannel ch) {
                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    ch.pipeline().addLast(new ProtobufEncoder());
                    ch.pipeline().addLast(new ReadTimeoutHandler(50));
                    ch.pipeline().addLast(new LoginAuthRespHandler());
                    ch.pipeline().addLast(new ServerTransactionHandler(messageSyncCache, messageAsyncCache, messageAsyncQueueProxy, 
                            threadPoolForServerProxy, messageForSubmitSyncCache, messageForSubmitAsyncCache, serverThreadLockCacheProxy));
                    ch.pipeline().addLast(new HeartBeatRespHandler());
                }
            });
        ChannelFuture f = b.bind(port).sync();
        isHealthy = true;
        logger.info("Dtf server start listening at port:18080");
        f.channel().closeFuture().sync();
        isHealthy = false;
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
    
}
