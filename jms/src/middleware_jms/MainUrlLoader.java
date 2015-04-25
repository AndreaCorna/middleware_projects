package middleware_jms;

import javax.naming.NamingException;

import middleware_jms.modules.UrlLoader;

public class MainUrlLoader {

	public static void main(String[] args) throws NamingException, InterruptedException {
		Thread.sleep(10000);

		UrlLoader loader = new UrlLoader("./urls.txt");
		loader.loadURLInQueue(); 


	}

}
