/*
 * Created on Apr 7, 2003
 */
package org.medi8.internal.core.ui;

import org.medi8.internal.core.ui.figure.MarkerFigure;
import org.medi8.internal.core.ui.figure.SelectionFigure;
import org.medi8.internal.core.ui.figure.TimecodeRuler;

import java.util.Iterator;

import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A layout manager that knows how to lay out a medi8 frame.
 * This is not a generic layout manager, but instead assumes several
 * things about the contents of the figure.
 */
public class Medi8Layout extends AbstractHintLayout
{
	/**
	 * Create a new layout, with the given vertical gap between elements. 
	 */
	public Medi8Layout(int gap)
	{
		this.gap = gap;
	}

	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint)
	{
		int width = wHint;
		int height = 0;
		Iterator iter = container.getChildren().iterator();
		while (iter.hasNext())
		{
			IFigure fig = (IFigure) iter.next();
			
			// Markers and selection figures are ignored here, since they
			// don't change the size calculation.
			if (fig instanceof MarkerFigure || fig instanceof SelectionFigure)
				continue;
			
			Dimension dim = fig.getPreferredSize(wHint, -1);
			height += dim.height;
			if (! (fig instanceof TimecodeRuler))
				height += gap;
			width = Math.max(width, dim.width);
		}
		height -= gap;
		return new Dimension(width, height);
	}

	public void layout(IFigure figure)
	{
		Rectangle relativeArea = figure.getClientArea();
		Rectangle bounds = new Rectangle();
		int y = 0;
		Iterator iter = figure.getChildren().iterator();
		while (iter.hasNext())
		{
			IFigure fig = (IFigure) iter.next();
			
			// A marker must be handled specially.
			if (fig instanceof MarkerFigure)
			{
				// FIXME: for now, do nothing
				continue;
			}
			
			// Selection figures know how to lay themselves out.
			if (fig instanceof SelectionFigure)
				continue;
			
			// Don't add padding above the bottom ruler.
			if (y > 0 && fig instanceof TimecodeRuler)
				y -= gap;
			
			Dimension dim = fig.getPreferredSize();
			bounds.x = 0;
			bounds.y = y;
			bounds.width = relativeArea.width;
			bounds.height = dim.height;
			bounds.translate(relativeArea.x, relativeArea.y);
			fig.setBounds(bounds);
			
			y += bounds.height;
			// Only add gap between two track elements.
			if (! (fig instanceof TimecodeRuler))
				y += gap;
		}
	}
	
	private int gap;
}
