/*
 * Created on Apr 7, 2003
 */
package org.medi8.internal.core.ui;

import java.util.Iterator;

import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.medi8.internal.core.ui.figure.MarkerFigure;
import org.medi8.internal.core.ui.figure.SelectionFigure;

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
	public Medi8Layout(int gap, Scale scale)
	{
		this.gap = gap;
		this.scale = scale;
	}

	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint)
	{
		int width = wHint;
		int height = gap;
		Iterator iter = container.getChildren().iterator();
		while (iter.hasNext())
		{
			IFigure fig = (IFigure) iter.next();
			
			// Markers and selection figures are ignored here, since they
			// don't change the size calculation.  Likewise the cursor line.
			if (fig instanceof MarkerFigure || fig instanceof SelectionFigure
					|| fig instanceof Polyline)
				continue;
			
			Dimension dim = fig.getPreferredSize(wHint, -1);
			height += dim.height + gap;
			width = Math.max(width, dim.width);
		}
		return new Dimension(width, height);
	}

	public void layout(IFigure figure)
	{
		Rectangle relativeArea = figure.getClientArea();
		Rectangle bounds = new Rectangle();
		int y = gap;
		Iterator iter = figure.getChildren().iterator();
		Polyline cursor = null;
		while (iter.hasNext())
		{
			IFigure fig = (IFigure) iter.next();
			
			// A marker must be handled specially.
			if (fig instanceof MarkerFigure)
			{
			  MarkerFigure mf = (MarkerFigure) fig;
			  Point p = new Point (scale.durationToUnits(mf.getTime()),
			                       50 /* FIXME */);
              // System.err.println("Point is: " + p + "; time = " + mf.getTime());
			  mf.setLocation(p);
			  continue;
			}
			
			// We handle the cursor specially later.
			if (fig instanceof Polyline)
			{
				cursor = (Polyline) fig;
				continue;
			}
			
			// Selection figures know how to lay themselves out.
			if (fig instanceof SelectionFigure)
				continue;
			
			Dimension dim = fig.getPreferredSize();
			bounds.x = 0;
			bounds.y = y;
			bounds.width = relativeArea.width;
			bounds.height = dim.height;
			bounds.translate(relativeArea.x, relativeArea.y);
			fig.setBounds(bounds);
            // System.err.println("fig: " + fig + "; location: " + bounds);
			
			y += bounds.height + gap;
		}
		
		if (cursor != null)
		{
			PointList points = cursor.getPoints();
			Point p2 = points.getLastPoint();
			p2.y = y;
			cursor.setEnd(p2);
		}
	}
	
	private int gap;
	private Scale scale;
}
