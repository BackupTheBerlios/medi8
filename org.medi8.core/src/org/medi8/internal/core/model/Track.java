/*
 * Created on Sep 16, 2004
 */
package org.medi8.internal.core.model;

public abstract class Track implements Visitable {
	
	public abstract void addChangeNotifyListener(IChangeListener listener);
	
	/**
	 * Returns the length of this Track.
	 *
	 * @return the length of this Track
	 */
	public abstract Time getLength ();
}
