package middleware_jms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.nio.charset.Charset;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;





public class UrlLoader {
	
		
    private String filePath;
    private Context context = null;
    private Queue URLQueue;
    private JMSContext jmsContext=null;
    private JMSProducer jmsProducer;
    
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
			if(message != null){
				jmsProducer.send(URLQueue, message);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
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
			context = UrlLoader.getContext();			
			jmsContext= ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			URLQueue = (Queue) context.lookup("URLQueue");
			jmsProducer = jmsContext.createProducer();


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
		
	

}



