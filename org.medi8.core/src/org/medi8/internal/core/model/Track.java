/*
 * Created on Sep 16, 2004
 */
package org.medi8.internal.core.model;

import org.medi8.internal.core.ui.Scale;
import org.medi8.internal.core.ui.figure.SequenceFigure;
import org.medi8.internal.core.ui.figure.TrackFigure;

public abstract class Track implements Visitable {
	
	public abstract void addChangeNotifyListener(IChangeListener listener);
	
	/**
	 * Returns the length of this Track.
	 *
	 * @return the length of this Track
	 */
	public abstract Time getLength ();
	
	public abstract void setLength (Time t);
	
	public abstract TrackFigure getFigure (SequenceFigure seq, Scale scale);
}
