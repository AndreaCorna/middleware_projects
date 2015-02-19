package middleware_jms;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser implements MessageListener{
	
	private Context context = null;
    private Queue HTMLPageQueue;
    private Queue ImagesQueue;
    private JMSContext jmsContext = null;
    private JMSProducer jmsProducer;
    private JMSConsumer jmsConsumer;
    
    
    public Parser() {
		setup();
	}
    
	private void setup(){
		try {
			context = Main.getContext();			
			jmsContext= ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			ImagesQueue = (Queue) context.lookup("ImagesQueue");
			HTMLPageQueue = (Queue) context.lookup("HTMLPageQueue");
			jmsConsumer = jmsContext.createConsumer(HTMLPageQueue);
			jmsProducer = jmsContext.createProducer();
			jmsConsumer.setMessageListener(this);


		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
    
    /*
     * Method created for testing purposes download a webPage and create a file with its content
     */
    public static void main(String[] args) throws IOException {

    	
    	String webSiteUrl = "http://www.corriere.it";
		
		String data = Jsoup.connect(webSiteUrl).get().html();
		PrintWriter out = new PrintWriter("test.html");
		out.println(data);
		out.close();
		File input = new File("test.html");
		
		ImageDownloader imageDownloader = new ImageDownloader();
		Parser parser= new Parser();

		parser.parse(webSiteUrl,input);
		while(true){
			
		}
		
	}
   
  
	
	private void parse(String webSiteUrl,File htmlPage) throws IOException{
		
		Document doc = Jsoup.parse(htmlPage, "UTF-8", webSiteUrl);			
		Elements images = doc.select("img");
		Iterator<Element> iterator = images.iterator();
		while (iterator.hasNext()) {
			Element image =  iterator.next();
			String url = image.absUrl("src");
			System.out.println("[Parser] image absolute url "+url);
			jmsProducer.send(ImagesQueue, url);
			
		}
				
	}
	

	@Override
	public void onMessage(Message bucket_name) {
	//	File htmlPage = S3.get(bucket_name);
	//	parse(bucket_name, htmlPage);
		
		
	}

	
}
