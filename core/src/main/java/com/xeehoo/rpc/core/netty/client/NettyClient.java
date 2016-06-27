package com.xeehoo.rpc.core.netty.client;

import java.util.ArrayList;
import java.util.List;


import akka.actor.ActorSystem;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NettyClient {
	private  final Logger LOG = LogManager.getLogger(NettyClient.class.getName());
	
	private  List<Channel> channels = new ArrayList<Channel>();
	private  final int maxConnectNumber = 5;
	private  EventLoopGroup group;
	
	private static class SingletonHolder {
        private static final NettyClient INSTANCE = new NettyClient();
    }
    private NettyClient (){}
    public static final NettyClient getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
	public void run(ActorSystem system){
		this.group = new NioEventLoopGroup(); 
        Bootstrap b = new Bootstrap(); 
        b.group(group).channel(NioSocketChannel.class); 
        b.handler(new ChannelInitializer<NioSocketChannel>() {
			@Override
			protected void initChannel(NioSocketChannel ch) throws Exception {
				ch.pipeline().addLast(new NettyChannelInHandler(system));
			}        	
        });
        b.option(ChannelOption.SO_KEEPALIVE, true);
		try {
			for (int i = 0; i < maxConnectNumber; i++){
				Channel c = b.connect("127.0.0.1",  8000).sync().channel();
				channels.add(c);
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return;
		}
		
		LOG.info("---------netty client completed-----------" + this);
	}
	
	public  Channel getChannel(){
		return channels.get((int)Thread.currentThread().getId() % maxConnectNumber);
	}
	
	public void shutdown(){
		group.shutdownGracefully();
	}
}
