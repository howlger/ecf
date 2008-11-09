/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.jslp;

import ch.ethz.iks.slp.Advertiser;
import ch.ethz.iks.slp.Locator;
import java.util.Properties;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.discovery.service.IDiscoveryService;
import org.eclipse.ecf.provider.jslp.container.JSLPDiscoveryContainer;
import org.osgi.framework.*;

public class Activator implements BundleActivator {
	// The shared instance
	private static Activator plugin;
	public static final String PLUGIN_ID = "org.eclipse.ecf.provider.jslp"; //$NON-NLS-1$

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	// we need to keep a ref on our context
	// @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=108214
	volatile BundleContext bundleContext;

	volatile LocatorDecorator locator = new NullPatternLocator();
	volatile Advertiser advertiser = new NullPatternAdvertiser();

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	public Bundle getBundle() {
		return bundleContext.getBundle();
	}

	public LocatorDecorator getLocator() {
		Assert.isNotNull(locator);
		return locator;
	}

	public Advertiser getAdvertiser() {
		Assert.isNotNull(advertiser);
		return advertiser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		bundleContext = context;

		// initially get the locator and add a life cycle listener
		final ServiceReference lRef = context.getServiceReference(Locator.class.getName());
		if (lRef != null) {
			locator = new LocatorDecoratorImpl((Locator) context.getService(lRef));
		}
		context.addServiceListener(new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				switch (event.getType()) {
					case ServiceEvent.REGISTERED :
						Object service = bundleContext.getService(event.getServiceReference());
						locator = new LocatorDecoratorImpl((Locator) service);
						break;
					case ServiceEvent.UNREGISTERING :
						locator = new NullPatternLocator();
						break;
				}
			}
		}, "(" + Constants.OBJECTCLASS + "=" + Locator.class.getName() + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		// initially get the advertiser and add a life cycle listener
		final ServiceReference aRef = context.getServiceReference(Advertiser.class.getName());
		if (aRef != null) {
			advertiser = (Advertiser) context.getService(aRef);
		}
		context.addServiceListener(new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				switch (event.getType()) {
					case ServiceEvent.REGISTERED :
						advertiser = (Advertiser) bundleContext.getService(event.getServiceReference());
						break;
					case ServiceEvent.UNREGISTERING :
						advertiser = new NullPatternAdvertiser();
						break;
				}
			}
		}, "(" + Constants.OBJECTCLASS + "=" + Advertiser.class.getName() + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		Properties props = new Properties();
		props.put(IDiscoveryService.CONTAINER_ID, IDFactory.getDefault().createStringID("org.eclipse.ecf.provider.jslp.container.JSLPDiscoveryContainer")); //$NON-NLS-1$
		props.put(IDiscoveryService.CONTAINER_NAME, JSLPDiscoveryContainer.NAME);

		context.registerService(IDiscoveryService.class.getName(), new ServiceFactory() {
			private volatile JSLPDiscoveryContainer jdc;

			/* (non-Javadoc)
			 * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration)
			 */
			public Object getService(Bundle bundle, ServiceRegistration registration) {
				if (jdc == null) {
					try {
						jdc = new JSLPDiscoveryContainer();
						jdc.connect(null, null);
					} catch (IDCreateException e) {
						Trace.catching(Activator.PLUGIN_ID, Activator.PLUGIN_ID + "/debug/methods/tracing", this.getClass(), "getService(Bundle, ServiceRegistration)", e); //$NON-NLS-1$ //$NON-NLS-2$
					} catch (ContainerConnectException e) {
						Trace.catching(Activator.PLUGIN_ID, Activator.PLUGIN_ID + "/debug/methods/tracing", this.getClass(), "getService(Bundle, ServiceRegistration)", e); //$NON-NLS-1$ //$NON-NLS-2$
						jdc = null;
					}
				}
				return jdc;
			}

			/* (non-Javadoc)
			 * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration, java.lang.Object)
			 */
			public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
				//TODO-mkuppe we later might want to dispose jSLP when the last!!! consumer ungets the service 
			}
		}, props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		//TODO-mkuppe here we should do something like a deregisterAll(), but see ungetService(...);
		plugin = null;
		bundleContext = null;
	}
}
