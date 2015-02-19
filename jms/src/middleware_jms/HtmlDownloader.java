package middleware_jms;


import java.io.File;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jsoup.Jsoup;

/**
 * This class downloads the html of a page and save it in S3
 * @author andrea
 *
 */
public class HtmlDownloader implements MessageListener{
	
	private Context context = null;
    private Queue URLQueue;
    private Queue HTMLPageQueue;
    private JMSContext jmsContext = null;
    private JMSProducer jmsProducer;
    private JMSConsumer jmsConsumer;
	
	public HtmlDownloader(){
		setup();
	}
	
	@Override
	public void onMessage(Message msg){
		if(msg != null){
			try {
				System.out.println("[HTMLDOWNLOADER] => Received "+msg.getBody(String.class));
				downloadPage(msg.getBody(String.class));
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Setup of context and queue connection
	 */
	private void setup(){
		try {
			context = HtmlDownloader.getContext();			
			jmsContext= ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			URLQueue = (Queue) context.lookup("URLQueue");
			HTMLPageQueue = (Queue) context.lookup("HTMLPageQueue");
			jmsProducer = jmsContext.createProducer();
			jmsConsumer = jmsContext.createConsumer(URLQueue);
			jmsConsumer.setMessageListener(this);


		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Download the html file and save on S3
	 * @param url - site url to download
	 */
	private void downloadPage(String url){
		String html = null;
		String name = null;
		try {
			html = Jsoup.connect(url).get().html();
			
			PrintWriter out = new PrintWriter("index.html");
			out.print(html);
			out.close();
			File temporaryFile = new File("index.html");
			name = Base64.encodeBase64String(url.getBytes());

			S3Manager manager = new S3Manager();
			manager.uploadFile(temporaryFile,"index.html",name);
			jmsProducer.send(HTMLPageQueue, name+"/index.html");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("[HTMLDOWNLOADER] => send message "+ html);
		
	}
	
	private static Context getContext() throws NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url", "iiop://localhost:3700");
		return new InitialContext(props);
	}
	

}
