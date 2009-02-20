/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.jmdns.container;

import java.io.IOException;
import java.net.InetAddress;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.discovery.IDiscoveryContainerAdapter;
import org.eclipse.ecf.internal.provider.jmdns.Messages;

public class ContainerInstantiator implements IContainerInstantiator {

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#createInstance(org.eclipse.ecf.core.ContainerTypeDescription, java.lang.Object[])
	 */
	public IContainer createInstance(final ContainerTypeDescription description, final Object[] args) throws ContainerCreateException {
		try {
			final AbstractContainer container = new JMDNSDiscoveryContainer(InetAddress.getLocalHost());
			return container;
		} catch (final IDCreateException e) {
			final ContainerCreateException excep = new ContainerCreateException(Messages.ContainerInstantiator_EXCEPTION_CONTAINER_CREATE);
			excep.setStackTrace(e.getStackTrace());
			throw excep;
		} catch (final IOException e) {
			final ContainerCreateException excep = new ContainerCreateException(Messages.ContainerInstantiator_EXCEPTION_GETTING_INETADDRESS);
			excep.setStackTrace(e.getStackTrace());
			throw excep;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedAdapterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
	public String[] getSupportedAdapterTypes(final ContainerTypeDescription description) {
		return new String[] {IDiscoveryContainerAdapter.class.getName()};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedParameterTypes(org.eclipse.ecf.core.ContainerTypeDescription)
	 */
	public Class[][] getSupportedParameterTypes(final ContainerTypeDescription description) {
		return new Class[0][0];
	}

	/** 
	 * @see org.eclipse.ecf.core.provider.IContainerInstantiator#getSupportedIntents(org.eclipse.ecf.core.ContainerTypeDescription)
	 * @since 2.2
	 */
	public String[] getSupportedIntents(ContainerTypeDescription description) {
		return null;
	}

}
