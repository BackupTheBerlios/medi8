/*
 * Created on Apr 8, 2003
 */
package org.medi8.internal.core.ui.figure;

import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.Track;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;

/**
 * This class is a Figure that displays an image.
 * It is associated with a given Time, and displays
 * itself along the timeline accordingly.
 */
public class MarkerFigure extends ImageFigure
{
	/**
	 * Create a new MarkerFigure associated with a given time.
	 * @param time The time with which this figure is associated
	 */
	public MarkerFigure(Time time)
	{
		this.time = time;
		setImage(getDefaultImage());
	}

	/**
	 * @param image
	 */
	public MarkerFigure(Image image, Time time)
	{
		super(image);
		this.time = time;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public void setLocation(Point p)
	{
	  if (SequenceFigure.FIGURE_DEBUG)
	  {
		String label = ("Conflict between tracks"
		    + "\nTime = " + time
		    + "\nX = " + p.x);
		setToolTip(new Label(label));
	  }
	  super.setLocation(p);
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
	
	/**
	 * Return the track associated with this marker, or null
	 * if there is no associated track.
	 */
	public Track getTrack ()
	{
	  // Just a stub for now.
	  return null;
	}
	
	public Time getTime ()
	{
	  return time;
	}

	protected Time time;
	
	private Image getDefaultImage ()
	{
	  byte[] data = new byte[16 * 16 * 3];
	  for (int i = 0; i < 16; ++i)
	  {
	    for (int j = 0; j < 3 * 16; j += 3)
	    {
	      data[3 * 16 * i + j] = 78;
	      data[3 * 16 * i + j + 1] = 30;
	      data[3 * 16 * i + j + 2] = 23;
	    }
	  }
	  ImageData id = new ImageData(16, 16, 24, pd, 3, data);
	  return new Image(null, id);
	}
	
	// FIXME: temporary
	private static final PaletteData pd = new PaletteData(0xff0000, 0x00ff00, 0x0000ff);
}
