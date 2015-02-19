package middleware_jms;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class HtmlModifier implements MessageListener{
	
	
	private Context context = null;
    private Queue LocalImagesQueue;
    private JMSContext jmsContext = null;
    private JMSConsumer jmsConsumer;
    
    public HtmlModifier(){
    	setup();
    }
    
    @Override
	public void onMessage(Message msg){
    	if(msg != null){
    		try {
    			System.out.println("[HTMLMODIFIER] => Received "+msg.getBody(String.class));
    			
    		} catch (JMSException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
		
	}
	
	private void setup(){
		try {
			context = HtmlModifier.getContext();			
			jmsContext= ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			LocalImagesQueue = (Queue) context.lookup("LocalImagesQueue");
			jmsConsumer = jmsContext.createConsumer(LocalImagesQueue);
			jmsConsumer.setMessageListener(this);


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
