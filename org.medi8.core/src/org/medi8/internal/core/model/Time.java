/*
 * Created on Apr 3, 2003
 */
package org.medi8.internal.core.model;

/**
 * This class represents a time in units of seconds.
 */
public class Time implements Comparable, Cloneable
{
	/**
	 * Create a new Time object.  Initially the time is zero.
	 */
	public Time()
	{
		value = 0;
	}

	public Time(double n)
	{
		this.value = n;
	}

	/**
	 * Create a new Time object which is the sum of
	 * two other Times.
	 */
	public Time(Time t1, Time t2)
	{
		value = t1.value + t2.value;
	}

	/**
	 * Create a new Time object which is computed as
	 * the offset from another Time.
	 * @param t1  The original Time
	 * @param offset Offset from the original Time, in seconds
	 */	
	public Time(Time t1, double offset)
	{
		value = t1.value + offset;
	}
	
	public Time getDifference(Time other)
	{
		return new Time(value - other.value);
	}
	
	/**
	 * Subtract the other time from this time.
	 */
	void subtract(Time other)
	{
		value -= other.value;
	}

	public int compareTo(Object o)
	{
		Time other = (Time) o;
		if (value < other.value)
			return -1;
		return value > other.value ? 1 : 0;
	}

	public boolean equals(Object o)
	{
		if (! (o instanceof Time))
			return false;
		return compareTo(o) == 0;
	}

	public boolean zero()
	{
		return value == 0;
	}
	
	public Object clone ()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// Can't happen.
			return null;	
		}
	}
	
	public String toString()
	{
		// FIXME: do something smart here.
		return Double.toString(value);
	}
	
	/**
	 * Return this time as an integer number of frames.
	 * Use with care.
	 */
	public double toDouble()
	{
		return value;
	}
	
	private double value;  // Maybe better `final'?
}
