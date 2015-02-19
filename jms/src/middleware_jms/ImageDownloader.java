package middleware_jms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.NamingException;

public class ImageDownloader implements MessageListener{
	
	private Context context = null;
    private Queue LocalImagesQueue;
    private Queue ImagesQueue;
    private JMSContext jmsContext = null;
    private JMSProducer jmsProducer;
    private JMSConsumer jmsConsumer;
	
	public ImageDownloader() {
		setup();
	}
	
	private void setup(){
		try {
			context = Main.getContext();			
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
	
	private void downloadImage(String string_url) {
		System.out.println("[IMAGES_DOWNLOADER] downloading image "+ string_url);
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
			String extension = string_url.substring(string_url.lastIndexOf(".")+1);	
			System.out.println("[IMAGES_DOWNLOADER] Image name "+ finalImageName);
			System.out.println("[IMAGES_DOWNLOADER] extension  "+ extension);


			File outputfile = new File("./imageDownload/"+finalImageName);
			try {
				ImageIO.write(image, extension, outputfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			

		}

		
		//upload S3

	}

	@Override
	public void onMessage(Message image_url) {
		try {
			System.out.println("[IMAGES_DOWNLOADER] => Received "+image_url.getBody(String.class));
			downloadImage(image_url.getBody(String.class));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    

}
