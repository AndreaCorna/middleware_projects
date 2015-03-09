package middleware_jms;


import javax.naming.NamingException;



public class Main {

	public static void main(String[] args) throws NamingException, InterruptedException {
	
		Thread thread = new Thread(new MonitoringTool());
		thread.start();
		thread.join();
		
		
	
		
	}
	
	

	
	
	

}
