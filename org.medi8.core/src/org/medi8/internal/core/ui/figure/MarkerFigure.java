/*
 * Created on Apr 8, 2003
 */
package org.medi8.internal.core.ui.figure;

import org.medi8.internal.core.model.Time;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.graphics.Image;

/**
 * This class is a Figure that displays an image.
 * It is associated with a given Time, and displays
 * itself along the timeline accordingly.
 */
public class MarkerFigure extends ImageFigure
{
	/**
	 * Create a new MarkerFigure associated with a given time.
	 * This figure has no image; one must be set with setImage.
	 * @param time The time with which this figure is associated
	 */
	public MarkerFigure(Time time)
	{
		this.time = time;
	}

	/**
	 * @param image
	 */
	public MarkerFigure(Image image, Time time)
	{
		super(image);
		this.time = time;
	}

	/**
	 * Notify this MarkerFigure of the new bounds of an associated range.
	 * This will set its visibility accordingly.
	 * @param start New starting time
	 * @param end New ending time
	 */
	public void setTimeBounds(Time start, Time end)
	{
		setVisible (! (time.compareTo(start) < 0 || time.compareTo(end) > 0));
	}

	private Time time;
}
