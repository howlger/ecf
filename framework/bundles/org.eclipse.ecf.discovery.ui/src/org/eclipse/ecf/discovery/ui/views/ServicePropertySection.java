package org.eclipse.ecf.discovery.ui.views;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.*;

public class ServicePropertySection extends AbstractPropertySection {

	private Text serviceName;
	private Text serviceID;
	private Text serviceIDNamespace;
	private Text servicePriority;
	private Text serviceWeight;

	private IServiceInfo serviceInfo;

	public ServicePropertySection() {
		// nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		// Service Name
		serviceName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		serviceName.setLayoutData(data);
		CLabel labelLabel = getWidgetFactory().createCLabel(composite, "Name:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(serviceName, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(serviceName, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

		// ServiceID 
		serviceID = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(serviceName, 0);
		serviceID.setLayoutData(data);
		labelLabel = getWidgetFactory().createCLabel(composite, "ID:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(serviceID, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(serviceID, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

		// ServiceID namespace
		serviceIDNamespace = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(serviceID, 0);
		serviceIDNamespace.setLayoutData(data);
		labelLabel = getWidgetFactory().createCLabel(composite, "Namespace:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(serviceIDNamespace, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(serviceIDNamespace, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

		// ServiceID priority
		servicePriority = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(serviceIDNamespace, 0);
		servicePriority.setLayoutData(data);
		labelLabel = getWidgetFactory().createCLabel(composite, "Priority:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(servicePriority, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(servicePriority, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

		// ServiceID weight
		serviceWeight = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(servicePriority, 0);
		serviceWeight.setLayoutData(data);
		labelLabel = getWidgetFactory().createCLabel(composite, "Weight:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(serviceWeight, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(serviceWeight, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	public void dispose() {
		super.dispose();
		serviceInfo = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof ViewTreeService);
		ViewTreeService parent = (ViewTreeService) input;
		if (parent.getID() != null)
			serviceInfo = parent.getServiceInfo();
		else
			serviceInfo = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	public void refresh() {
		if (serviceInfo != null) {
			serviceName.setText(serviceInfo.getServiceID().getServiceName());
			serviceName.setEditable(false);
			serviceID.setText(serviceInfo.getServiceID().getName());
			serviceID.setEditable(false);
			serviceIDNamespace.setText(serviceInfo.getServiceID().getNamespace().getName());
			serviceIDNamespace.setEditable(false);
			servicePriority.setText(serviceInfo.getPriority() + ""); //$NON-NLS-1$
			servicePriority.setEditable(false);
			serviceWeight.setText(serviceInfo.getWeight() + ""); //$NON-NLS-1$
			serviceWeight.setEditable(false);
		}
	}
}
