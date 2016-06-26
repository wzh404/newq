package com.xeehoo.rpc.core.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NettyServer {
	private final Logger logger =  LogManager.getLogger();
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1); 
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);  
    
	public void run(){
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
		 .channel(NioServerSocketChannel.class)
		 .childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel sc) throws Exception {
				sc.pipeline().addLast("tcp", new NettyServerChannelInHandler());
			}			 
		 });
		logger.info("start netty server.");
		try {
			ChannelFuture f = b.bind(8000).sync();
			f.channel().closeFuture().sync(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args){
		(new NettyServer()).run();
	}
}
