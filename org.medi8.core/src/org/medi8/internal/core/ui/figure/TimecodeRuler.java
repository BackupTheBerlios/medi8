/*
 * Created on Apr 7, 2003
 */
package org.medi8.internal.core.ui.figure;

import org.medi8.internal.core.ui.Scale;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

// FIXME: should display tic marks and times
// Probably will need some heuristic to make this display prettily.


/**
 * This is a Figure that displays a timecode ruler.
 * This is like a ruler, except instead of measuring distance
 * it measures time, displaying it in timecode format.
 */
public class TimecodeRuler extends Figure implements PropertyChangeListener
{
	/**
	 * Specify a TimecodeRuler that goes above a sequence.
	 */
	public static final int ABOVE = 0;

	/**
	 * Specify a TimecodeRuler that goes below a sequence.
	 */
	public static final int BELOW = 1;

	/**
	 * Create a new TimecodeRuler.
	 */
	public TimecodeRuler(int how)
	{
		if (how != ABOVE && how != BELOW)
			throw new IllegalArgumentException();
		this.how = how;
	}

	/**
	 * Attach a Scale to this object.
	 * This is used to control the range and display.
	 * @param scale
	 */
	public void setScale(Scale scale)
	{
		if (this.scale != null)
			this.scale.removeListener(this);
		this.scale = scale;
		scale.addListener(this);
	}

	protected void paintFigure(Graphics g)
	{
		Rectangle r = getBounds();
		
		// Draw the barrier line.
		int direction = (how == BELOW) ? 1 : -1;
		int y = (how == BELOW) ? r.y : (r.bottom() - 1);
		int right = r.right() - 1;
		g.drawLine(r.x, y, right, y);
		
		// Draw a vertical line every second, and a bigger
		// one every minute.
		int j = 0;
		int height = r.height * direction;
		while (true)
		{
			int h;
			if (j % 60 == 0)
				h = height * 3 / 4;
			else
				h = height / 2;
			int x = scale.durationToUnit(j);
			if (x > r.width)
				break;
			g.drawLine(x, y, x, y + h);
			++j;
		}
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt)
	{
		invalidate();
	}

	/**
	 * The scale we are using.
	 */
	private Scale scale;
	
	/**
	 * How to draw.
	 */
	private final int how;
}
