package com.xeehoo.rpc.core.netty.client;

import com.xeehoo.rpc.core.hessian.HessianCoder;
import com.xeehoo.rpc.model.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyChannelInHandler extends ChannelInboundHandlerAdapter{
	
	@Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) {    
        ByteBuf buf = (ByteBuf) msg;
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);		
		buf.release();
		
		HessianCoder.getReply(bytes, User.class);
    }	
}
