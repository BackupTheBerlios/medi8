/*
 * Created on Apr 7, 2003
 */
package org.medi8.internal.core.model;

/**
 * This holds information about the origin of a clip.
 * For instance it might hold the date it was made,
 * the tape it was on, and the location of the tape.
 * For now it is a placeholder.
 */
public class Provenance
{
	/**
	 * 
	 */
	public Provenance(String loc)
	{
		this.loc = loc;
	}

	public String toString()
	{
		return loc;
	}

	private String loc;
}
