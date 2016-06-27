package com.xeehoo.rpc.core.netty.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.xeehoo.rpc.core.hessian.HessianCoder;
import com.xeehoo.rpc.core.rpc.ApiReply;
import com.xeehoo.rpc.model.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyChannelInHandler extends ChannelInboundHandlerAdapter{
    private final ActorSystem system;

    public NettyChannelInHandler(ActorSystem system){
        this.system = system;
    }

	@Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) {    
        ByteBuf buf = (ByteBuf) msg;
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);		
		buf.release();
		
		ApiReply reply = HessianCoder.getReply(bytes, User.class);
		if (reply != null){
            if (system != null){
                System.out.println("__________________TELL___________");
                ActorRef actorRef = system.actorFor("akka://newq-akka/user/serviceActor");
                actorRef.tell(reply, actorRef.noSender());
            }
            else{
                HessianCoder.emit(reply.getLsn(), reply.getObj());
            }
		}
    }	
}
