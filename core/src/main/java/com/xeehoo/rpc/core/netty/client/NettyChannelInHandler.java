package com.xeehoo.rpc.core.netty.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.xeehoo.rpc.core.hessian.HessianCoder;
import com.xeehoo.rpc.core.rpc.ApiReply;
import com.xeehoo.rpc.model.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NettyChannelInHandler extends ChannelInboundHandlerAdapter{
    private final Logger logger =  LogManager.getLogger();
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
		
		ApiReply reply = HessianCoder.getReply(bytes);
		if (reply != null){
            if (reply.getType() == 2){
                if (system != null) {
                    logger.info("__________________TELL___________" + reply.getLsn());
                    system.actorSelection("akka://newq-akka/user/serviceActor$" + reply.getLsn())
                          .tell(reply, ActorRef.noSender());
                }
                else{
                    logger.info("__________________TELL FAILED___________" + reply.getLsn());
                }
            }
            else{
                HessianCoder.emit(reply.getLsn(), reply.getObj());
            }
		}
    }	
}
