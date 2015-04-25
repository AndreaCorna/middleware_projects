package middleware_jms.modules;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import middleware_jms.messages.ImageDownloaderToHtmlModifierMessage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sun.misc.BASE64Encoder;

/**
 * The class substitutes the img tags with the base64 encode in order to visualize
 * image offline.
 * @author andrea
 *
 */
public class HtmlModifier implements MessageListener,Module{
	
	
	private Context context = null;
    private Queue LocalImagesQueue;
    private JMSContext jmsContext = null;
    private JMSConsumer jmsConsumer;
    private S3Manager manager;
    private String directory = "./output";
    
    public HtmlModifier(){
    	manager = new S3Manager();
    	File directoryOutput = new File(directory);
    	if(!directoryOutput.exists()){
    		directoryOutput.mkdir();
    	}
    	setup();
    }
    
    @Override
	public void onMessage(Message msg){
    	if(msg != null && msg instanceof ObjectMessage){
    		try {
    			System.out.println("[HTMLMODIFIER] => Received "+msg.getBody(ImageDownloaderToHtmlModifierMessage.class));
    			ImageDownloaderToHtmlModifierMessage message = msg.getBody(ImageDownloaderToHtmlModifierMessage.class);
				String base64  = message.getBase64Encode();
				String imageUrl = message.getFileImageName();
				modifyPage(base64,imageUrl);
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
			context = HtmlModifier.getContext();			
			jmsContext= ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			LocalImagesQueue = (Queue) context.lookup("LocalImagesQueue");
			jmsConsumer = jmsContext.createConsumer(LocalImagesQueue);
			jmsConsumer.setMessageListener(this);


		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Substitude the img src url with the base64
	 * @param webSiteBase64 - webSite url base64 encode
	 * @param imageUrl - name image to substitute
	 */
	private void modifyPage(String webSiteBase64, String imageUrl){
		File directoryOutput = new File(directory+"/"+webSiteBase64);
    	if(!directoryOutput.exists()){
    		directoryOutput.mkdir();
    	}
		
		File htmlPage = new File(directory+"/"+webSiteBase64+"/"+"index.html");
		if(!htmlPage.exists()){
			htmlPage = manager.getFile(webSiteBase64,"index.html",directory+"/"+webSiteBase64);
		}
		
		File imageFile = manager.getFile(webSiteBase64, imageUrl,directory+"/"+webSiteBase64);
		
		String encodedStringImage = encodeImageToBase64(imageFile);
		
		try {
			Document doc = Jsoup.parse(htmlPage, "UTF-8");
			Elements images = doc.select("img[data-src|src*="+imageUrl);
			Iterator<Element> iterator = images.iterator();
			
			while (iterator.hasNext()) {
				Element image =  iterator.next();
				image.attr("src","data:image/jpg;base64,"+encodedStringImage);
			}
			PrintWriter out = new PrintWriter(directory+"/"+webSiteBase64+"/"+"index.html");
			out.print(doc.html());
			out.close();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imageFile.delete();
		
		
		
	}
	
	/**
	 * Get the string base64 of a image file
	 * @param fileImage - file to convert
	 * @return base64 string encode
	 */
	private String encodeImageToBase64(File fileImage){
	    BufferedImage bufferImage;
	    String imageString = null;

		try {
			bufferImage = ImageIO.read(fileImage);
			String type = fileImage.getName().substring(fileImage.getName().lastIndexOf(".")+1);
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferImage, type, bos);
            byte[] imageBytes = bos.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
		} catch (IOException e1) {
			System.out.println("Malformed file " +fileImage.getName());
			e1.printStackTrace();
		}
		
        return imageString;
	}
	
	private static Context getContext() throws NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url", "iiop://localhost:3700");
		return new InitialContext(props);
	}
	
	
}
