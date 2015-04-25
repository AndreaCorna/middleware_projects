package middleware_jms.modules;


import java.io.File;

import middleware_jms.messages.DownloadToParserMessage;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class downloads the html of a page and save it in S3
 * @author andrea
 *
 */
public class HtmlDownloader implements MessageListener,Module{
	
	private Context context = null;
    private Queue URLQueue;
    private Queue HTMLPageQueue;
    private JMSContext jmsContext = null;
    private JMSProducer jmsProducer;
    private JMSConsumer jmsConsumer;
    private S3Manager manager;
	
	public HtmlDownloader(){
		manager = new S3Manager();
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
			
			Document doc = Jsoup.parse(html);
			//Download css and insert into html
	        Elements imports = doc.select("link[rel=stylesheet]");
	        Iterator<Element> iterator = imports.iterator();
			Element head = doc.select("head").first();
			while (iterator.hasNext()) {
				Element importLink =  iterator.next();
				String link = importLink.attr("abs:href");
				System.out.println("LINK => "+link);
				String css = null;
				try{
					css = Jsoup.connect(link).get().html();

				}catch(IllegalArgumentException e){
					
				}
				if(css != null){
					head.appendElement("style").text(css);

				}
				importLink.remove();
				
			}
			name = Base64.encodeBase64String(url.getBytes());

			PrintWriter out = new PrintWriter(name+".html");
			out.print(doc.html());
			out.close();
			File temporaryFile = new File(name+".html");


			String nameFile = "index.html";
			manager.uploadFile(temporaryFile,nameFile,name);
			DownloadToParserMessage message =  new DownloadToParserMessage(name, nameFile);
			jmsProducer.send(HTMLPageQueue,  message);
			temporaryFile.delete();
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
