/*
 * Created on Apr 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.medi8.internal.core.model;

/**
 * A Clip is the base class for all assets.
 * Each Clip has its own timeline starting at 0 and
 * ending at some end point.
 */
public abstract class Clip implements Visitable, Cloneable
{
	/**
	 * 
	 */
	public Clip(Provenance location, Time length)
	{
		this.location = location;
		this.length = length;
	}
	
	/**
	 * @return The ending time of this clip, or, in other words, its length
	 */
	public Time getLength()
	{
		return length;
	}
	
	/* A subclass might not be able to compute its length immediately, 
	 * so we provide a way to set it.
	 */
	protected void setLength(Time length)
	{
		this.length = length;
	}

	public Provenance getProvenance()
	{
		return location;
	}
	
	public String toString()
	{
		return "Clip[" + (location == null ? "" : ("provenance=" + location + ",")) + "length=" + length + "]";
	}
	
	public boolean isVideo ()
	{
		return video;
	}
	
	public void setVideo(boolean video)
	{
		this.video = video;
	}
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException _)
		{
			// Can't happen.
			return null;
		}
	}
	
	protected Provenance location;
	protected Time length;
	protected boolean video;
}
