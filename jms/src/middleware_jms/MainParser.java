package middleware_jms;

import middleware_jms.modules.Parser;

public class MainParser {

	public static void main(String[] args) throws InterruptedException {
		
		MonitoringToolModule parserMonitor = new MonitoringToolModule(Parser.class, "HTMLPageQueue", 1);
		Thread thread = new Thread(parserMonitor);
		thread.start();
		thread.join();
		
	}

}
