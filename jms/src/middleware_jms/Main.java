package middleware_jms;

import javax.naming.NamingException;

import middleware_jms.modules.HtmlDownloader;
import middleware_jms.modules.HtmlModifier;
import middleware_jms.modules.ImageDownloader;
import middleware_jms.modules.Parser;
import middleware_jms.modules.UrlLoader;


public class Main {

	public static void main(String[] args) throws NamingException {
		System.out.println(System.getProperty("javax.net.ssl.trustStore"));
		HtmlModifier htmlModifier = new HtmlModifier();
		ImageDownloader imageDownloader = new ImageDownloader();
		Parser parser = new Parser();
		HtmlDownloader httpDownloader = new HtmlDownloader();
		UrlLoader urlLoader = new UrlLoader("./urls.txt");
		urlLoader.loadURLInQueue();
		while(true){
			
		}
		
	}
	
	
	

}
