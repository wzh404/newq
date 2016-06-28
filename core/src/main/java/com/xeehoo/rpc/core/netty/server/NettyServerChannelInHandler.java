package com.xeehoo.rpc.core.netty.server;

import java.lang.reflect.InvocationTargetException;


import com.xeehoo.rpc.core.rpc.ApiBean;
import com.xeehoo.rpc.core.hessian.HessianCoder;
import com.xeehoo.rpc.model.User;
import com.xeehoo.rpc.service.UserService;
import com.xeehoo.rpc.service.UserServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NettyServerChannelInHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger =  LogManager.getLogger();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		logger.info(Thread.currentThread().getName() + "_________" + msg.getClass().toString());
		ByteBuf buf = (ByteBuf) msg;
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);		
		buf.release();
		
		ApiBean api = HessianCoder.deserialization(bytes);
		UserService userService = new UserServiceImpl();
		User u = null;
		if (api.getMethod() != null){
			try {
				u = (User)api.getMethod().invoke(userService, api.getParas());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return;
			}
		}
		else{
			return;
		}
		
		byte[] datas = HessianCoder.reply(u, api.getLsn(), api.getType());
		try {
			ctx.channel().writeAndFlush(Unpooled.wrappedBuffer(datas)).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();	
	}
}
