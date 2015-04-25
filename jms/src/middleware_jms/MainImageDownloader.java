package middleware_jms;

import middleware_jms.modules.ImageDownloader;

public class MainImageDownloader {

	public static void main(String[] args) throws InterruptedException {
		MonitoringToolModule imageDownloaderMonitor = new MonitoringToolModule(ImageDownloader.class, "ImagesQueue", 30);
		Thread thread = new Thread(imageDownloaderMonitor);
		thread.start();
		thread.join();

	}

}
