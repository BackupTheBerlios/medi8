/*
 * Created on Nov 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.medi8.internal.core.model.Monitor;
import org.medi8.internal.core.ui.widget.gtk.MonitorWidget;

/**
 * @author green
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MonitorView extends ViewPart {

	private MonitorWidget displayArea;
	
	// The monitor model.
	private Monitor monitor;

	/**
	 * 
	 */
	public MonitorView() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
	  MonitorWidget mw = new MonitorWidget (parent);
		displayArea = mw;
		monitor = new Monitor (mw.getVideoServer());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {

	}

}
