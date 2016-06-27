package com.xeehoo.rpc.controller;

import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.xeehoo.rpc.core.akka.AkkaServiceActor;
import com.xeehoo.rpc.core.rpc.HessianRpcProxy;
import com.xeehoo.rpc.core.rpc.RpcService;
import com.xeehoo.rpc.core.rpc.Servicer;
import com.xeehoo.rpc.model.User;
import com.xeehoo.rpc.service.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class CompletableFutureContoller {
	private final Logger logger =  LogManager.getLogger();

//	@Autowired
//	private SomeService someService;
//
//	@RequestMapping(value = "/async", method = RequestMethod.GET)
//	public CompletableFuture<ModelAndView> indexAsync() {
//		LOG.info("Servlet Thread Id = '" + Thread.currentThread().getName()
//				+ "'.");
//
//		return someService.getMessage().thenApply(
//				msg -> new ModelAndView("message", "message", msg));
//	}

	@RequestMapping(value = "/netty", method = RequestMethod.GET)
	public CompletableFuture<ModelAndView> netty(){
		logger.info("----------------------");
//		RpcService<User, ModelAndView> rpc = new RpcService<User, ModelAndView>()
//        		.api(UserService.class)
//        		.thenApply(r -> func(r))
//        		.exceptionally((t) -> ex(t));
//
//        UserService userService = (UserService)rpc.getService();
//        userService.getUser(3);

		System.out.println(Thread.currentThread().getName() + " completed. ");
		RpcService<UserService, User> rpc = new RpcService(UserService.class);
		return rpc.applyAsync(r -> func2(r))
		          .thenApply(r -> func(r));
        

//		return rpc.getResult();
	}

	@RequestMapping(value = "/akka", method = RequestMethod.GET)
	public DeferredResult<String> akka(HttpServletRequest req, HttpServletResponse resp){
//		final AsyncContext asyncContext = req.startAsync();
//		HttpServletResponse resp = (HttpServletResponse) asyncContext.getResponse();
		final ActorSystem system = (ActorSystem) req.getServletContext()
				.getAttribute("ActorSystem");

		DeferredResult<String> deferredResult = new DeferredResult<String>();
		ActorRef actorRef = system.actorOf(Props.create(AkkaServiceActor.class, UserService.class, deferredResult, resp), "serviceActor");
		logger.info("path is " + actorRef.path().toString());

		HessianRpcProxy handler = new HessianRpcProxy(UserService.class, "akka");
		Object proxy = Proxy.newProxyInstance(UserService.class.getClassLoader(), new Class[]{UserService.class}, handler);
		((UserService) proxy).getUser(3);

		return deferredResult;
	}
	
	private  ModelAndView func(User o) {
		logger.info(Thread.currentThread().getName() + " future______________" + o.getClass().getName());
		return new ModelAndView("message", "message", o.getName());
	}

	private void func2(UserService userService){
		logger.info(Thread.currentThread().getName() + "___________" + userService);
		userService.getUser(3);
	}
	
	private  User ex(Throwable t){
		logger.info(Thread.currentThread().getName() + " future______________" + t.getMessage());
		return null;
	}
}
