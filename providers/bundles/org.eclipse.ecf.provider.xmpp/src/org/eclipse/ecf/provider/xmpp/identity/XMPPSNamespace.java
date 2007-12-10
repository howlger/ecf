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
package org.eclipse.ecf.provider.xmpp.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.internal.provider.xmpp.Messages;

public class XMPPSNamespace extends Namespace {

	private static final long serialVersionUID = -820087396161230667L;
	
	private static final String XMPPS_PROTOCOL = "xmpps"; //$NON-NLS-1$
	
	public ID createInstance(Object[] args)
			throws IDCreateException {
		try {
			return new XMPPSID(this, (String) args[0]);
		} catch (Exception e) {
			throw new IDCreateException(Messages.XMPPSNamespace_EXCEPTION_ID_CREATE, e);
		}
	}

	public String getScheme() {
		return XMPPS_PROTOCOL;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedParameterTypesForCreateInstance()
	 */
	public Class[][] getSupportedParameterTypes() {
		return new Class[][] { { String.class } };
	}
}
