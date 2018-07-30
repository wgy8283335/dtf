package com.coconason.dtf.common.client;

import com.coconason.dtf.common.codec.NettyMessageDecoder;
import com.coconason.dtf.common.codec.NettyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class NettyClient
{

	private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

	private EventLoopGroup group = new NioEventLoopGroup();

	public static void main(String[] args) throws Exception
	{
		new NettyClient().connect("127.0.0.1", 18080);
	}

	private void connect(String host, int port) throws Exception
	{
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
							// NettyMessageDecoder设置了单条消息最大值1MB,可以防止消息过大导致的内存溢出或者畸形码流，引发解码错位或内存分配失败
							ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4, -8, 0));
							ch.pipeline().addLast(new NettyMessageEncoder());
							ch.pipeline().addLast(new ReadTimeoutHandler(50));
							ch.pipeline().addLast(new LoginAuthReqHandler());
							ch.pipeline().addLast(new HeartBeatReqHandler());
						}
					});

			ChannelFuture f = b.connect(host, port).sync();
			System.out.println("连接成功-----> " + host + ":" + port);
			f.channel().closeFuture().sync();
		}
		finally
		{
			// 发起重连操作
			executorService.execute(new ConnectRunnable(host,port));
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
}
