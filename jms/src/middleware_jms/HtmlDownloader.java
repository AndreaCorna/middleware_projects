package middleware_jms;


import java.io.IOException;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jsoup.Jsoup;


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
		try {
			System.out.println("[HTMLDOWNLOADER] => Received "+msg.getBody(String.class));
		//	downloadPage(msg.getBody(String.class));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setup(){
		try {
			context = Main.getContext();			
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
	
	private void downloadPage(String url){
		String html = null;
		try {
			html = Jsoup.connect(url).get().html();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("[HTMLDOWNLOADER] => send message "+ html);
		
		jmsProducer.send(HTMLPageQueue, html);
	}
	

}
