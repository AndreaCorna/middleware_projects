package middleware_jms;

import middleware_jms.modules.HtmlDownloader;
import middleware_jms.modules.HtmlModifier;
import middleware_jms.modules.ImageDownloader;
import middleware_jms.modules.Parser;

public class MainDistributed {

	public static void main(String[] args) throws InterruptedException {
		MonitoringToolModule htmlDownloaderMonitor = new MonitoringToolModule(HtmlDownloader.class, "URLQueue", 1);
		Thread thread = new Thread(htmlDownloaderMonitor);
		thread.start();
		thread.join();
		
		MonitoringToolModule parserMonitor = new MonitoringToolModule(Parser.class, "HTMLPageQueue", 1);
		Thread thread3 = new Thread(parserMonitor);
		thread3.start();
		thread3.join();
		
		MonitoringToolModule imageDownloaderMonitor = new MonitoringToolModule(ImageDownloader.class, "ImagesQueue", 1);
		Thread thread2 = new Thread(imageDownloaderMonitor);
		thread2.start();
		thread2.join();
		
		MonitoringToolModule htmlModifierMonitor = new MonitoringToolModule(HtmlModifier.class, "LocalImagesQueue", 1);
		Thread thread1 = new Thread(htmlModifierMonitor);
		thread1.start();
		thread1.join();
		
			

	}

}
