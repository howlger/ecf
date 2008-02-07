package org.eclipse.ecf.internal.examples.remoteservices.client;

import java.io.NotSerializableException;
import java.util.Arrays;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.discovery.ui.handlers.AbstractRemoteServiceAccessHandler;
import org.eclipse.ecf.examples.remoteservices.common.IRemoteEnvironmentInfo;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

public class RemoteEnvironmentInfoServiceAccessHandler extends AbstractRemoteServiceAccessHandler {

	public RemoteEnvironmentInfoServiceAccessHandler() {
	}

	protected IContributionItem[] getContributionsForMatchingService() {
		final IContainer container = Activator.getDefault().getContainer();
		// not connected already...so setup contribution that allows connect
		final String ns = getConnectNamespace();
		final String id = getConnectID();
		if (container == null || ns == null || id == null)
			return EMPTY_CONTRIBUTION;
		ID connectTargetID = null;
		try {
			connectTargetID = createID(ns, id);
		} catch (final Exception e) {
			return EMPTY_CONTRIBUTION;
		}
		final ID connectedID = container.getConnectedID();
		if (connectedID != null) {
			// If we're already connected, and connected to the *wrong* remote, then disconnect
			if (!connectedID.equals(connectTargetID)) {
				container.disconnect();
				// Otherwise we're already connected to the right container
			} else
				return super.getContributionsForMatchingService();
		}
		// Now we get the contribution to make connection to correct connectTargetID
		final ID cTargetID = connectTargetID;
		final IAction action = new Action() {
			public void run() {
				try {
					// Then we connect
					connectContainer(container, cTargetID, null);
				} catch (ContainerConnectException e) {
					showException(e);
				}
			}
		};
		action.setText(NLS.bind("Connect to {0}", connectTargetID.getName()));
		return new IContributionItem[] {new ActionContributionItem(action)};
	}

	protected IContributionItem[] getContributionItemsForService(final IRemoteServiceContainerAdapter adapter) {
		final String className = getRemoteServiceClass();
		if (className == null)
			return NOT_AVAILABLE_CONTRIBUTION;
		else if (className.equals(IRemoteEnvironmentInfo.class.getName()))
			return getContributionItemsForRemoteEnvironmentService(adapter);
		else
			return NOT_AVAILABLE_CONTRIBUTION;
	}

	private IRemoteCall createGetPropertyRemoteCall() throws ClassNotFoundException, NotSerializableException {
		IRemoteEnvironmentInfo.class.getDeclaredMethods();
		final InputDialog input = new InputDialog(null, "Get property", "Enter property key", "user.name", null);
		input.setBlockOnOpen(true);
		final Object[] params = new Object[1];
		if (input.open() == Window.OK) {
			params[0] = input.getValue();
			return new IRemoteCall() {

				public String getMethod() {
					return "getProperty";
				}

				public Object[] getParameters() {
					return params;
				}

				public long getTimeout() {
					return 30000;
				}

			};
		} else
			return null;
	}

	private void showResult(final String serviceInterface, final IRemoteCall remoteCall, final Object result) {
		final Object display = (result != null && result.getClass().isArray()) ? Arrays.asList((Object[]) result) : result;
		final Object[] bindings = new Object[] {serviceInterface, remoteCall.getMethod(), Arrays.asList(remoteCall.getParameters()), display};
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(null, "Received Response", NLS.bind("Service: {0}\n\nMethod: {1}\nParameters: {2}\n\nResult: {3}", bindings));
			}
		});
	}

	private void showException(final Throwable t) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(null, "Received Exception", NLS.bind("Exception: {0}", t.getLocalizedMessage()));
			}
		});
	}

	private IContributionItem createContributionItem(final IRemoteService remoteService, final int invokeMode) {
		final IAction action = new Action() {
			public void run() {
				try {
					final IRemoteCall remoteCall = createGetPropertyRemoteCall();
					if (remoteCall != null) {
						switch (invokeMode) {
							// callSynch
							case 0 :
								showResult(IRemoteEnvironmentInfo.class.getName(), remoteCall, remoteService.callSynch(remoteCall));
								break;
							// callAsynch (listener)
							case 1 :
								remoteService.callAsynch(remoteCall, new IRemoteCallListener() {
									public void handleEvent(IRemoteCallEvent event) {
										if (event instanceof IRemoteCallCompleteEvent) {
											IRemoteCallCompleteEvent complete = (IRemoteCallCompleteEvent) event;
											if (complete.hadException()) {
												showException(complete.getException());
											} else
												showResult(IRemoteEnvironmentInfo.class.getName(), remoteCall, complete.getResponse());
										}
									}
								});
								break;
							// callAsynch (future)
							case 2 :
								showResult(IRemoteEnvironmentInfo.class.getName(), remoteCall, remoteService.callAsynch(remoteCall).get());
								break;
							// proxy
							case 3 :
								IRemoteEnvironmentInfo proxy = (IRemoteEnvironmentInfo) remoteService.getProxy();
								showResult(IRemoteEnvironmentInfo.class.getName(), remoteCall, proxy.getProperty((String) remoteCall.getParameters()[0]));
								break;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		switch (invokeMode) {
			case 0 :
				action.setText("getProperty (s)");
				break;
			case 1 :
				action.setText("getProperty (a)");
				break;
			case 2 :
				action.setText("getProperty (f)");
				break;
			case 3 :
				action.setText("getProperty (p)");
				break;
		}
		return new ActionContributionItem(action);
	}

	/**
	 * @param adapter
	 * @return
	 */
	private IContributionItem[] getContributionItemsForRemoteEnvironmentService(final IRemoteServiceContainerAdapter adapter) {
		try {
			final IRemoteServiceReference[] references = getRemoteServiceReferences(adapter);
			if (references == null)
				return NOT_AVAILABLE_CONTRIBUTION;
			final IRemoteService remoteService = adapter.getRemoteService(references[0]);
			return new IContributionItem[] {createContributionItem(remoteService, 0), createContributionItem(remoteService, 1), createContributionItem(remoteService, 2), createContributionItem(remoteService, 3)};
		} catch (final Exception e1) {
			return NOT_AVAILABLE_CONTRIBUTION;
		}

	}
}
