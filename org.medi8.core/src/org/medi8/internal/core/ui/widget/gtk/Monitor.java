/*
 * Created on Nov 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.ui.widget.gtk;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author green
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Monitor extends Composite {

	/**
	 * @param displayArea
	 * @param i
	 */
	
	public Monitor (Composite parent) {
		super (parent, SWT.EMBEDDED);
		
		long parentHandle = embeddedHandle;
		System.out.println("parentHandle = " + Long.toHexString(parentHandle));
	}
}
