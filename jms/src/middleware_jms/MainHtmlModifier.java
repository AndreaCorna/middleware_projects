package middleware_jms;

import middleware_jms.modules.HtmlModifier;

public class MainHtmlModifier {

	public static void main(String[] args) throws InterruptedException {
		MonitoringToolModule htmlModifierMonitor = new MonitoringToolModule(HtmlModifier.class, "LocalImagesQueue", 1);
		Thread thread = new Thread(htmlModifierMonitor);
		thread.start();
		thread.join();
	}

}
