package middleware_jms;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Main {

	public static void main(String[] args) throws NamingException {
		HtmlDownloader httpDownloader = new HtmlDownloader();
		UrlLoader urlLoader = new UrlLoader("./urls.txt");
		urlLoader.loadURLInQueue();
		
	}
	
	public static Context getContext() throws NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url", "iiop://localhost:3700");
		return new InitialContext(props);
	}
	

}
