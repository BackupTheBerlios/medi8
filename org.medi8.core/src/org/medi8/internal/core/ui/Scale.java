/*
 * Created on Apr 26, 2003
 */
package org.medi8.internal.core.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.medi8.internal.core.model.Time;

/**
 * A Scale represents the scale at which a sequence should
 * be displayed.  It keeps track of the number of pixels per display
 * unit, and when changed it notifies observers.
 */
public class Scale
{
	/**
	 * Create a new scale.
	 */
	public Scale()
	{
		// Initially, one pixel is 1/30 sec.
		this.scale = 1 / 30.0;
		this.listeners = new PropertyChangeSupport(this);
	}
	
	/**
	 * Set the number of seconds that constitute a display unit.
	 * @param scale
	 */
	public void setScale(double scale)
	{
		double save = this.scale;
		this.scale = scale;
		listeners.firePropertyChange(SCALE_PROPERTY, new Double(save), new Double(scale));
	}
	
	public void addListener(PropertyChangeListener l)
	{
		listeners.addPropertyChangeListener(SCALE_PROPERTY, l);
	}
	
	public void removeListener(PropertyChangeListener l)
	{
		listeners.removePropertyChangeListener(SCALE_PROPERTY, l);
	}

	public double getScale()
	{
		return scale;
	}

	/**
	 * Convert a duration to a number of pixels.
	 * @param duration The amount of time
	 * @return Corresponding number of pixels
	 */
	public int durationToUnits(Time duration)
	{
		return (int) (duration.toDouble() / scale);
	}
	
	public int durationToUnit(double duration)
	{
		return (int) (duration / scale);
	}
	
	/**
	 * Convert a number of pixels to a duration.
	 * @param units Number of pixels
	 * @return A Time representing the corresponding duration.
	 */
	public Time unitsToDuration(int units)
	{
		return new Time(units * scale);
	}
	
	/** Number of seconds per pixel.  */
	private double scale;
	private PropertyChangeSupport listeners;
	
	private static final String SCALE_PROPERTY = "scale";
}
