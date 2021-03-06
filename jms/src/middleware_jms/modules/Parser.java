package middleware_jms.modules;

import java.io.File;
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
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import middleware_jms.messages.DownloadToParserMessage;
import middleware_jms.messages.ParserToImageDownloaderMessage;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class parsers the html file in order to obtain information about images in page html
 * @author andrea
 *
 */
public class Parser implements MessageListener,Module{
	
	private Context context = null;
    private Queue HTMLPageQueue;
    private Queue ImagesQueue;
    private JMSContext jmsContext = null;
    private JMSProducer jmsProducer;
    private JMSConsumer jmsConsumer;
    private S3Manager manager;
    private String directory = "./parser";

    
    
    public Parser() {
		manager = new S3Manager();
		File directoryOutput = new File(directory);
    	if(!directoryOutput.exists()){
    		directoryOutput.mkdir();
    	}
		setup();
	}
    
    /**
     * Setup connection and queue for class
     */
	private void setup(){
		try {
			context = Parser.getContext();			
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
    
	/**
	 * Parse the webpage to find image that are send to be processed
	 * @param webSiteUrl - url of the site
	 * @param htmlPage - the html page
	 * @param webSiteBase64 - url encode
	 * @throws IOException
	 */
	private void parse(String webSiteUrl,File htmlPage, String webSiteBase64) throws IOException{
		
		Document doc = Jsoup.parse(htmlPage, "UTF-8", Base64.decodeBase64(webSiteBase64).toString());	
		System.out.println("[Parser] Website URI"+Base64.decodeBase64(webSiteBase64).toString());
		doc.setBaseUri(webSiteUrl);
		Elements images = doc.select("img");
		Iterator<Element> iterator = images.iterator();
		PrintWriter writer = new PrintWriter(directory+"/list_images_"+webSiteBase64+".txt","UTF-8");
		while (iterator.hasNext()) {
			Element image =  iterator.next();
			String url = image.absUrl("src");
			System.out.println("[Parser] Inside Image urls "+image.toString());
			System.out.println("[Parser] urs "+url);
			String dataUrl = image.absUrl("data-src");
			if(!url.isEmpty()){
				System.out.println("[Parser] image absolute url "+url);
				writer.println(url);
				
			}else if(!dataUrl.isEmpty()){
				System.out.println("[Parser] image absolute url "+url);
		//		writer.println(url);
			}
			//
		}
		writer.close();
		manager.uploadFile(new File(directory+"/list_images_"+webSiteBase64+".txt"), "list_image.txt", webSiteBase64);//uploading the file list_images_base64.txt on S3 with name list_image
		System.out.println("[PARSER]sending message to Image downloader");
		jmsProducer.send(ImagesQueue, new ParserToImageDownloaderMessage(webSiteBase64, "list_image.txt",webSiteUrl));
		htmlPage.delete();		
	}
	

	@Override
	public void onMessage(Message msg) {
		System.out.println("[PARSER] => Received "+msg.getClass());

		if(msg != null && msg instanceof ObjectMessage){
			try {
				DownloadToParserMessage message = msg.getBody(DownloadToParserMessage.class);
				String base64  = message.getBase64Encode();
				String name = message.getHtmlFileName();
				String urlSite = message.getUrlSite();
				String base64url = Base64.decodeBase64(base64).toString();
				
				
				
				File directoryOutput = new File(directory+"/"+base64url);
		    	if(!directoryOutput.exists()){
		    		directoryOutput.mkdir();
		    	}
				
				
				System.out.println("[PARSER] => Received "+msg.getBody(DownloadToParserMessage.class) + " "+base64+ " "+name);
				File htmlPage = manager.getFile(base64,name,directory+"/"+base64url);
				parse(urlSite, htmlPage, base64);
				htmlPage.delete();
				directoryOutput.delete();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			catch (IOException e) {
				System.out.println("[PARSER] Failed parsing page");
				e.printStackTrace();
			}
		}
		
		
	}
	
	private static Context getContext() throws NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url", "iiop://localhost:3700");
		return new InitialContext(props);
	}
	
    /*
     * Method created for testing purposes download a webPage and create a file with its content
     
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
		
	}*/

	
}
