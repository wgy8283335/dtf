package com.coconason.dtf.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.awt.*;
import java.net.InetSocketAddress;

/**
 * @Author: Jason
 * @date: 2018/7/25-10:43
 */
public class Server {
    private final int port;

    public Server(int port){
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        if(args.length != 1){
            System.err.println("Usage:"+Server.class.getSimpleName()+"<port>");
        }
        int port = Integer.parseInt(args[0]);
        new Server(port).start();
    }

    public void start() throws Exception{
        final ServerHandler serverHandler = new ServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(group).channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception{
                        ch.pipeline().addLast(serverHandler);
                    }
                });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}
