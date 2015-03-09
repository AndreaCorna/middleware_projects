package middleware_jms;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import middleware_jms.modules.Module;



public class MonitoringToolModule implements Runnable {
	

	private static final int MAX_INSTANCES = 15;
	private QueueBrowser queueBrowser;
	private ArrayList<Module> modulesList;
	private Integer threshold;
	private Class<?> moduleClass;
	
    public MonitoringToolModule(Class<?> moduleClass, String queueName, int threshold) {
    	Context context;
		JMSContext jmsContext;
		this.threshold = threshold;
		this.moduleClass = moduleClass;
		modulesList = new ArrayList<>();
		
		try {
				
			modulesList.add((Module)moduleClass.newInstance());
			context = getContext();
			jmsContext = ((ConnectionFactory) context.lookup("java:comp/DefaultJMSConnectionFactory")).createContext();
			Queue queue = (Queue) context.lookup(queueName);
			queueBrowser = jmsContext.createBrowser(queue);
		
		
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
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

		while(true){
			try {
				checkLoad();
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
	

	private void checkLoad() throws JMSException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException {
		@SuppressWarnings("rawtypes")
		Enumeration messages = queueBrowser.getEnumeration();
		Integer numberOfMessages=0;
		while (messages.hasMoreElements()) {
			 messages.nextElement();
			 numberOfMessages++;	
		}
		
		System.out.println("Messages in queue  "+numberOfMessages);
		if (numberOfMessages>threshold){
			System.out.println("Incrementing object in "+moduleClass.getCanonicalName());
			if (modulesList.size()<MAX_INSTANCES){
				modulesList.add((Module) moduleClass.newInstance());
			}
			
		}
		else if(numberOfMessages ==0){
			if(modulesList.size()>1){
				System.out.println("Decrementing object in "+moduleClass.getCanonicalName());

				modulesList.remove(modulesList.size()-1);
			}
			
		}
		
		
		
	}

}
