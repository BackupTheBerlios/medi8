/*
 * Created on Nov 3, 2003
 *
 */
package org.medi8.core;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author green
 *
 */
public class Medi8Perspective implements IPerspectiveFactory {

	/* The medi8 perspective currently consists simply of a resource 
	 * navigator view on the left of the editor area. 
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		//	Get the editor area.
		String editorArea = layout.getEditorArea();
		//	Start adding views...
		layout.addView("org.eclipse.ui.views.ResourceNavigator",
			IPageLayout.LEFT, 0.25f, editorArea);
		layout.addView("org.medi8.internal.core.ui.MonitorView",
			IPageLayout.TOP, 0.3f, editorArea);
	}

}
