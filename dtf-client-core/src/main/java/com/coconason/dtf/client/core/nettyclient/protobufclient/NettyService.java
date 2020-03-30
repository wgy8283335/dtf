package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.coconason.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Netty service.
 * 
 * @Author: Jason
 */
@Component
public final class NettyService {
    
    /**
     * Logger for NettyService class.
     */
    private Logger logger = LoggerFactory.getLogger(NettyService.class);
    
    /**
     * Abstract client transaction handler.
     */
    @Autowired
    private AbstractClientTransactionHandler clientTransactionHandler;
    
    /**
     * Heart beat request handler.
     */
    @Autowired
    private ChannelInboundHandlerAdapter heartBeatReqHandler;
    
    /**
     * Login authorization request handler.
     */
    @Autowired
    private ChannelInboundHandlerAdapter loginAuthReqHandler;
    
    /**
     * Thread pool.
     */
    @Autowired
    @Qualifier("threadPoolForClientProxy")
    private ExecutorService threadPoolForClientProxy;
    
    /**
     * Event loop group.
     */
    private EventLoopGroup group = new NioEventLoopGroup();
    
    /**
     * Whether netty client is healthy.
     */
    private Boolean isHealthy = false;
    
    /**
     * Netty server configuration.
     */
    @Autowired
    private NettyServerConfiguration nettyServerConfiguration;
    
    /**
     * Put the task in the thread pool to be executed.
     */
    public synchronized void start() {
        threadPoolForClientProxy.execute(new ConnectRunnable(nettyServerConfiguration.getHost(), nettyServerConfiguration.getPort()));
    }
    
    private void connect(final String host, final int port) throws InterruptedException { 
        Bootstrap b = new Bootstrap();
        b.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(final SocketChannel ch) {
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new ReadTimeoutHandler(50));
                        ch.pipeline().addLast(loginAuthReqHandler);
                        ch.pipeline().addLast(heartBeatReqHandler);
                        ch.pipeline().addLast(clientTransactionHandler);
                    }
                });
        ChannelFuture f = b.connect(host, port).sync();
        isHealthy = true;
        logger.debug("connection success-----> " + host + ":" + port);
        f.channel().closeFuture().sync();
        isHealthy = false;
        threadPoolForClientProxy.execute(new ConnectRunnable(host, port));
    }
    
    /**
     * Send message of base transaction service information.
     *
     * @param serviceInfo base transaction service information
     */
    public void sendMsg(final BaseTransactionServiceInfo serviceInfo) {
        clientTransactionHandler.sendMsg(serviceInfo);
    }
    
    /**
     * Check the health of the client.
     * @return whether the client is healthy.
     */
    public Boolean isHealthy() {
        return isHealthy;
    }
    
    /**
     * Close the netty service.
     */
    public synchronized void close() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }
    
    /**
     * Restart the netty service.
     */
    public synchronized void restart() {
        close();
        start();
    }
    
    private class ConnectRunnable implements Runnable {
        
        private String host;
        
        private int port;
        
        ConnectRunnable(final String host, final int port) {
            this.host = host;
            this.port = port;
        }
        
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(5);
                connect(host, port);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }
    
}
