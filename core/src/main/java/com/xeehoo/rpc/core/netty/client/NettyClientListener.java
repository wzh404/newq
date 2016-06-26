package com.xeehoo.rpc.core.netty.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



public class NettyClientListener implements ServletContextListener{
	private  final Logger LOG = LogManager.getLogger(NettyClientListener.class.getName());

	@Override
	public void contextDestroyed(ServletContextEvent e) {
		NettyClient.getInstance().shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent e) {		
		LOG.info("Start client connect...");
		NettyClient.getInstance().run();
	}

}
