package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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
 * @Author: Jason
 * @date: 2018/8/22-16:49
 */
@Component
public final class NettyService {

    private Logger logger = LoggerFactory.getLogger(NettyService.class);

    @Autowired
    private AbstractClientTransactionHandler clientTransactionHandler;

    @Autowired
    private ChannelInboundHandlerAdapter heartBeatReqHandler;

    @Autowired
    private ChannelInboundHandlerAdapter loginAuthReqHandler;

    @Autowired
    @Qualifier("threadPoolForClientProxy")
    private ExecutorService threadPoolForClientProxy;

    private EventLoopGroup group = new NioEventLoopGroup();

    private Boolean isHealthy = false;

    @Autowired
    private NettyServerConfiguration nettyServerConfiguration;

    public synchronized void start(){
        threadPoolForClientProxy.execute(new ConnectRunnable(nettyServerConfiguration.getHost(),nettyServerConfiguration.getPort()));
    }

    private class ConnectRunnable implements Runnable{
        private String host;
        private int port;

        public ConnectRunnable(String host, int port) {
            this.host = host;
            this.port = port;
        }
        @Override
        public void run(){
            try{
                TimeUnit.SECONDS.sleep(5);
                connect(host, port);
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }

    private void connect(String host, int port) throws Exception{
        Bootstrap b = new Bootstrap();
        b.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>()
                {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception
                    {
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new ReadTimeoutHandler(50));
                        ch.pipeline().addLast(loginAuthReqHandler);
                        ch.pipeline().addLast(clientTransactionHandler);
                        ch.pipeline().addLast(heartBeatReqHandler);
                    }
                });
        ChannelFuture f = b.connect(host, port).sync();
        isHealthy = true;
        System.out.println("connection success-----> " + host + ":" + port);
        f.channel().closeFuture().sync();
        isHealthy = false;
        threadPoolForClientProxy.execute(new ConnectRunnable(host,port));
    }

    public Boolean isHealthy() {
        return isHealthy;
    }

    public synchronized  void close(){
        if(group!=null){
            group.shutdownGracefully();
        }
    }

    public synchronized void restart() {
        close();
        start();
    }

    public void sendMsg(BaseTransactionServiceInfo serviceInfo) {
        clientTransactionHandler.sendMsg(serviceInfo);
    }
}
