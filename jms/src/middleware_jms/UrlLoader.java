package middleware_jms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.nio.charset.Charset;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConnectionFactory;
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





public class UrlLoader {
	
	public static void main(String[] args) throws NamingException {
		UrlLoader urlLoader = new UrlLoader("./urls.txt");
		urlLoader.loadURLInQueue();
		
		
	}
	
    private String filePath;
    private Context context = null;
    private Queue URLQueue;
    private JMSContext jmsContext=null;
    private JMSProducer jmsProducer;
    private JMSConsumer jmsConsumer;
    
	public UrlLoader(String pathToFile) {
		filePath = pathToFile;
		
	}
	
	public void loadURLInQueue() throws NamingException{
		
		List<String> urlsList = getUrlFromFile();
		setup();
		
		if (jmsContext == null) {
			System.out.println("context null");
			
		}
		if (URLQueue== null) {
			System.out.println("URLQueue null");
			
		}
		
		Iterator<String> iterator = urlsList.iterator();
		while (iterator.hasNext()){
			String message =  iterator.next();
			System.out.println("send message "+ message);
		
			jmsProducer.send(URLQueue, message);
						
		} 
		
	}
	
	private List<String> getUrlFromFile(){
		Charset charset = Charset.forName("UTF-8");
		Path path = Paths.get(filePath);	
		List<String> urlsList = null;	
		try {
			 urlsList = Files.readAllLines(path,charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlsList;
		
	}
	
	private void setup(){
		
		try {
			context=UrlLoader.getContext();			
			jmsContext= ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			URLQueue = (Queue) context.lookup("URLQueue");
			jmsProducer = jmsContext.createProducer();
			jmsConsumer = jmsContext.createConsumer(URLQueue);
			jmsConsumer.setMessageListener(new Myconsumer());


		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	private static Context getContext() throws NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url", "iiop://localhost:3700");
		return new InitialContext(props);
	}
	
	
	private class Myconsumer implements MessageListener{
		
		
		
		@Override
		public void onMessage(Message msg) {
			try {
				System.out.println("Received "+msg.getBody(String.class));
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	

}



