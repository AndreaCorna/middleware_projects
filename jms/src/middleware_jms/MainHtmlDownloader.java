package middleware_jms;

import middleware_jms.modules.HtmlDownloader;

public class MainHtmlDownloader {

	public static void main(String[] args) throws InterruptedException {

		MonitoringToolModule htmlDownloaderMonitor = new MonitoringToolModule(HtmlDownloader.class, "URLQueue", 10);
		Thread thread = new Thread(htmlDownloaderMonitor);
		thread.start();
		thread.join();

	}

}
