/*
 * Created on Aug 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.ui.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.RectangleFigure;

/**
 * This represents the user's current selection in the editor.
 * It is a transient object and not represented in the model. 
 */
public class SelectionFigure extends RectangleFigure
{
	public SelectionFigure (SequenceFigure seq)
	{
		setBackgroundColor(ColorConstants.yellow);
		setFill(true);
	}
}
