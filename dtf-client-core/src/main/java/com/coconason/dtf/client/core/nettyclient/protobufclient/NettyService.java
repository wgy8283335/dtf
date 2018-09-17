package com.coconason.dtf.client.core.nettyclient.protobufclient;

import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.nettyclient.nettyserverconfig.NettyServerConfiguration;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClient;
import com.coconason.dtf.common.protobuf.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/8/22-16:49
 */
@Component
public class NettyService {

    @Autowired
    ClientTransactionHandler clientTransactionHandler;

    @Autowired
    ThreadPoolForClient threadPoolForClient;

    private EventLoopGroup group = new NioEventLoopGroup();

    @Autowired
    NettyServerConfiguration nettyServerConfiguration;

    public synchronized void start(){
        try{
            threadPoolForClient.addTask(new ConnectRunnable(nettyServerConfiguration.getHost(),nettyServerConfiguration.getPort()));
        }catch (Exception e){
            System.out.println("Exception-----> \n" + e);
        }
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
                e.printStackTrace();
            }
        }
    }

    private void connect(String host, int port) throws Exception{
        try{
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
                            ch.pipeline().addLast(new LoginAuthReqHandler());
                            ch.pipeline().addLast(clientTransactionHandler);
                            ch.pipeline().addLast(new HeartBeatReqHandler());
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            System.out.println("connection success-----> " + host + ":" + port);
            f.channel().closeFuture().sync();
        }finally{
            threadPoolForClient.addTask(new ConnectRunnable(host,port));
        }
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

    public void sendMsg(TransactionServiceInfo serviceInfo) {
        clientTransactionHandler.sendMsg(serviceInfo);
    }
}
