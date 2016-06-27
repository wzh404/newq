package com.xeehoo.rpc.core.netty.client;

import akka.actor.ActorSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



public class NettyClientListener implements ServletContextListener{
	private  final Logger LOG = LogManager.getLogger(NettyClientListener.class.getName());

	@Override
	public void contextDestroyed(ServletContextEvent e) {
		NettyClient.getInstance().shutdown();

        ActorSystem system = (ActorSystem) e.getServletContext().getAttribute("ActorSystem");
        e.getServletContext().removeAttribute("ActorSystem");
        system.shutdown();
        system.awaitTermination();
	}

	@Override
	public void contextInitialized(ServletContextEvent e) {
		ActorSystem system = ActorSystem.create("newq-akka");
        e.getServletContext().setAttribute("ActorSystem", system);

        LOG.info("Start client connect...");
        NettyClient.getInstance().run(system);
	}
}
