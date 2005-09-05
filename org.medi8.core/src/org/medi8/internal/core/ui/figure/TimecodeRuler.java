/*
 * Created on Apr 7, 2003
 */
package org.medi8.internal.core.ui.figure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.ui.Scale;

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

        Dimension size = getSize();
        size.height = Medi8Editor.CLIP_HEIGHT;
        setSize(size);
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
		int height = r.height * direction / 2;
		while (true)
		{
            int x = scale.durationToUnit(j);
            if (x > r.width)
                break;
			int h;
			if (j % 60 == 0)
              {
				h = height * 3 / 4;
                // Currently we draw the time once per minute.
                // FIXME: this is wrong, we should have a heuristic
                // that depends on the scale.
                String text = Time.toUserString(j);
                Font font = getFont();
                Dimension size = FigureUtilities.getStringExtents(text, font);
                int textx = Math.max(0, x - size.width / 2);
                g.drawString(text, textx, r.y);
              }
			else
				h = height / 2;
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
