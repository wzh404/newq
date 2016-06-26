import com.xeehoo.rpc.core.netty.client.NettyClient;

public class TestNettyClient {
	public static void main(String[] args){
		NettyClient.getInstance().run();
//		call();
	}
	
//	public static void call(){
//		Thread t1 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//		        	for (int i = 0; i < 10; i++){
//		        		RpcService<User, Object> rpc = new RpcService<User, Object>()
//		        		.api(UserService.class)
//		        		.thenApply(r -> func(r))
//		        		.exceptionally((t) -> ex(t));
//
//		        		UserService userService = (UserService)rpc.getService();
//		        		userService.getUser(3);
//
//			        	Thread.sleep(1000L);
//		        	}
//		        } catch (Exception e) {
//		        	e.printStackTrace();
//		        }
//
//		        System.out.println("--------------thread end--------" + Thread.currentThread().getName());
//			}
//		});
//
//		t1.start();
//	}
//
//	private static Object func(User o){
//		System.out.println(Thread.currentThread().getName() + " future______________" + o.getClass().getName());
//		return o;
//	}
//
//	private static User ex(Throwable t){
//		System.out.println(Thread.currentThread().getName() + " future______________" + t.getMessage());
//		return null;
//	}
}
