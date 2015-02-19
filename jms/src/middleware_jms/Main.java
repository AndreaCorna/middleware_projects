package middleware_jms;

import javax.naming.NamingException;

public class Main {

	public static void main(String[] args) throws NamingException {
		System.out.println(System.getProperty("javax.net.ssl.trustStore"));
		ImageDownloader imageDownloader = new ImageDownloader();
		Parser parser = new Parser();
		HtmlDownloader httpDownloader = new HtmlDownloader();
		UrlLoader urlLoader = new UrlLoader("./urls.txt");
		urlLoader.loadURLInQueue();
		while(true){
			
		}
		
	}
	
	
	

}
