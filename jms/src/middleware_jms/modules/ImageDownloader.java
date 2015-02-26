package middleware_jms.modules;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
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

import middleware_jms.messages.ImageDownloaderToHtmlModifierMessage;
import middleware_jms.messages.ParserToImageDownloaderMessage;


public class ImageDownloader implements MessageListener{
	
	private Context context = null;
    private Queue LocalImagesQueue;
    private Queue ImagesQueue;
    private JMSContext jmsContext = null;
    private JMSProducer jmsProducer;
    private JMSConsumer jmsConsumer;
    private S3Manager manager;
    private String directory = "./imageDownload";

	
	public ImageDownloader() {
		manager = new S3Manager();
		File directoryOutput = new File(directory);
    	if(!directoryOutput.exists()){
    		directoryOutput.mkdir();
    	}
		setup();
	}
	
	/**
	 * Setup of context and queue connection
	 */
	private void setup(){
		try {
			context = ImageDownloader.getContext();			
			jmsContext= ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			ImagesQueue = (Queue) context.lookup("ImagesQueue");
			LocalImagesQueue = (Queue) context.lookup("LocalImagesQueue");
			jmsConsumer = jmsContext.createConsumer(ImagesQueue);
			jmsProducer = jmsContext.createProducer();
			jmsConsumer.setMessageListener(this);


		} catch (NamingException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Dowload image from site and save it on S3
	 * @param string_url - url of image to download
	 * @param webSiteBase64 - websiteurl base64 encode
	 */
	private void downloadImage(String string_url, String webSiteBase64) {
		System.out.println("[IMAGES_DOWNLOADER"+this+"] downloading image "+ string_url);
		BufferedImage image = null;
		try {
		    URL url = new URL(string_url);
		    image = ImageIO.read(url);
		} catch (IOException e) {
		}
		
		String imageName= string_url.substring(string_url.lastIndexOf("/")+1);
		Pattern pattern = Pattern.compile("\\w+\\.(jpg|png)");
		Matcher matcher = pattern.matcher(imageName);
		if (matcher.find())
		{
		    String finalImageName= matcher.group(0);
			String extension = finalImageName.substring(finalImageName.lastIndexOf(".")+1);	
			System.out.println("[IMAGES_DOWNLOADER] Image name "+ finalImageName);
			System.out.println("[IMAGES_DOWNLOADER] extension  "+ extension);


			File outputfile = new File(directory+"/"+finalImageName);
			try {
				ImageIO.write(image, extension, outputfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			manager.uploadFile(outputfile, finalImageName,webSiteBase64);
			jmsProducer.send(LocalImagesQueue, new ImageDownloaderToHtmlModifierMessage(webSiteBase64, finalImageName));
		

		}

		

	}

	@Override
	public void onMessage(Message msg) {
		if(msg != null && msg instanceof ObjectMessage){
			try {
				System.out.println("[IMAGES_DOWNLOADER"+this+"] => Received "+msg.getBody(ParserToImageDownloaderMessage.class));
				
				ParserToImageDownloaderMessage message = msg.getBody(ParserToImageDownloaderMessage.class);
				String base64  = message.getBase64Encode();
				String imageUrl = message.getAbsoluteImageUrl();
				downloadImage(imageUrl,base64);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
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
    

}
