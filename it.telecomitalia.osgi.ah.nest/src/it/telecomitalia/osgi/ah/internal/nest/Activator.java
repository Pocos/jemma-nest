package it.telecomitalia.osgi.ah.internal.nest;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

	private static BundleContext context;
	
	public static BundleContext getContext() {
		return context;
	}

	
	public void start(BundleContext context) throws Exception {
		System.out.println("Trying to interact with NEST Cloud!!");
		Activator.context=context;
		}
	
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping bundle");
		Activator.context=null;
	}

}
