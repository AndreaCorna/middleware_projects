package middleware_jms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import middleware_jms.modules.HtmlDownloader;
import middleware_jms.modules.HtmlModifier;
import middleware_jms.modules.ImageDownloader;
import middleware_jms.modules.Parser;
import middleware_jms.modules.UrlLoader;

public class MonitoringTool implements Runnable{
	private static final Integer THRESHOLD = 30;
	
	QueueBrowser URLQueueBrowser;
	QueueBrowser HTMLPageQueueBrowser;
	QueueBrowser ImagesQueueBrowser;
	QueueBrowser LocalImagesQueueBrowser;
	
	UrlLoader urlLoader;
	ArrayList<HtmlDownloader> htmlDownloaders;
	ArrayList<HtmlModifier> htmlModifiers;
	ArrayList<ImageDownloader> imageDownloaders;
	ArrayList<Parser> parsers;
	ArrayList<UrlLoader> urlLoaders;
	
	HashMap<Class<?>, ArrayList<?>> hashMap;
	
	
	public MonitoringTool() {
		Context context;
		JMSContext jmsContext;
		
		
		htmlDownloaders = new ArrayList<HtmlDownloader>();
		htmlModifiers = new ArrayList<HtmlModifier>();
		imageDownloaders = new ArrayList<ImageDownloader>();
		parsers = new ArrayList<Parser>();
		urlLoaders = new ArrayList<UrlLoader>();
		
		htmlDownloaders.add(new HtmlDownloader());
		imageDownloaders.add(new ImageDownloader());
		parsers.add(new Parser());
		htmlModifiers.add(new HtmlModifier());
		urlLoader = new UrlLoader("./urls.txt");


		
		hashMap = new HashMap<Class<?>, ArrayList<?>>();
		hashMap.put(HtmlDownloader.class, htmlDownloaders);
		hashMap.put(HtmlModifier.class, htmlModifiers);
		hashMap.put(ImageDownloader.class, imageDownloaders);
		hashMap.put(Parser.class, parsers);

		
		try {
			
			context = getContext();
			jmsContext = ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			Queue URLQueue = (Queue) context.lookup("URLQueue");
			Queue HTMLPageQueue = (Queue) context.lookup("HTMLPageQueue");
			Queue ImagesQueue = (Queue) context.lookup("ImagesQueue");
			Queue LocalImagesQueue = (Queue) context.lookup("LocalImagesQueue");
			URLQueueBrowser = jmsContext.createBrowser(URLQueue);
			HTMLPageQueueBrowser = jmsContext.createBrowser(HTMLPageQueue);
			ImagesQueueBrowser = jmsContext.createBrowser(ImagesQueue);
			LocalImagesQueueBrowser = jmsContext.createBrowser(LocalImagesQueue);
		
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
			
	
	}
	
	
	private  Context getContext() throws NamingException {
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url", "iiop://localhost:3700");
		return new InitialContext(props);
	}


	@Override
	public void run() {
		
		
		try {
			urlLoader.loadURLInQueue();
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		while(true){
			try {
				checkLoad(URLQueueBrowser,HtmlDownloader.class);
				checkLoad(HTMLPageQueueBrowser,Parser.class);
				checkLoad(ImagesQueueBrowser,ImageDownloader.class);
				checkLoad(LocalImagesQueueBrowser,HtmlModifier.class);
				System.out.println("instances of HtmlDownloader "+htmlDownloaders.size());
				System.out.println("instances of Parser "+parsers.size());
				System.out.println("instances of ImageDownloader "+imageDownloaders.size());
				System.out.println("instances of HtmlModifier "+htmlModifiers.size());
				Thread.sleep(2000);

			} catch (JMSException e) {
				e.printStackTrace();
			
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			 catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
	}


	private void checkLoad(QueueBrowser queueBrowser,
			Class class1) throws JMSException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException {
		Enumeration messages = queueBrowser.getEnumeration();
		Integer numberOfMessages=0;
		while (messages.hasMoreElements()) {
			 messages.nextElement();
			 numberOfMessages++;	
		}
		
		System.out.println("Messages in queue "+class1.getCanonicalName()+" "+numberOfMessages);
		if (numberOfMessages>THRESHOLD){
			System.out.println("Incrementing object in "+class1.getCanonicalName());
			ArrayList arrayList = hashMap.get(class1);
			arrayList.add(class1.newInstance());		
			
		}
		else if(numberOfMessages ==0){
			ArrayList arrayList = hashMap.get(class1);
			if(arrayList.size()>1){
				System.out.println("Decrementing object in "+class1.getCanonicalName());

				arrayList.remove(arrayList.size()-1);
			}
			
		}
		
		
		
	}

}
