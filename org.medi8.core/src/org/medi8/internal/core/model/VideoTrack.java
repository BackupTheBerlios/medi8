/*
 * Created on Nov 13, 2003
 */
package org.medi8.internal.core.model;

import java.util.Iterator;
import java.util.Vector;

import org.medi8.internal.core.model.audio.AudioBus;
import org.medi8.internal.core.model.events.AddEvent;
import org.medi8.internal.core.model.events.DeleteEvent;
import org.medi8.internal.core.model.events.MoveEvent;
import org.medi8.internal.core.model.events.SyntheticLengthChangeEvent;
import org.medi8.internal.core.ui.Scale;
import org.medi8.internal.core.ui.figure.SequenceFigure;
import org.medi8.internal.core.ui.figure.TrackFigure;
import org.medi8.internal.core.ui.figure.VideoTrackFigure;

/**
 */
public class VideoTrack extends Track
{
	public VideoTrack()
	{
		listeners = new Vector();
		times = new Vector();
		elements = new Vector();
		
		// FIXME:  Every video track is being sent to the master audio bus
		// for the purposes of testing.   This is, in fact, pretty bogus.
		// Only audio tracks should go to the audio bus.  For now we'll just
		// assume that every clip in the video track also includes audio.
		AudioBus.getMasterBus().send(this);
	}
	
	public TrackFigure getFigure (SequenceFigure seq, Scale scale)
	{
	  return new VideoTrackFigure (seq, this, scale);
	}
	
	/**
	 * Returns the length of this Track.
	 *
	 * @return the length of this Track
	 */
	public Time getLength ()
	{
		int len = elements.size() - 1;
		if (len < 0)
			return new Time ();
		return new Time ((Time) times.get(len),
				((Clip) elements.get(len)).getLength ());
	}
	
	public void setLength (Time len)
	{
		maxTime = len;
		notify(new SyntheticLengthChangeEvent(this));
	}
	
	public boolean addClip(Time when, Clip clip)
	{
		boolean result = addClipNoEvents(when, clip);
		if (result)
		{
			notify(new AddEvent(this, when, clip));
		}
		return result;
	}
	
	private boolean addClipNoEvents(Time when, Clip clip)
	{
		int size = times.size();
		int i;
		for (i = 0; i < size; ++i)
		{
			Time start = (Time) times.get(i);
			int cmp = when.compareTo(start);
			if (cmp == 0)
			{
				// overlap
				return false;
			}
			if (cmp > 0)
			{
				// Requested time comes after start time of this clip.
				// Make sure it also comes after the end of this clip.
				// Then keep going.
				Clip c = (Clip) elements.get(i);
				Time end = new Time(start, c.getLength());
				if (when.compareTo(end) <= 0)
					return false;
				continue;
			}
			// Requested time comes before the start of this clip.
			// So this is our insertion point.  Make sure that the
			// end of the requested clip is also before the starting
			// point of this element in the vector. 
			if (new Time(when, clip.getLength()).compareTo(start) >= 0)
				return false;
			break;
		}
		times.insertElementAt(when, i);
		elements.insertElementAt(clip, i);
		return true;
	}
	
	public boolean deleteClip(Clip clip)
	{
		int index = findClip(clip);
		if (index == -1)
			return false;
		elements.remove(index);
		times.remove(index);
		notify (new DeleteEvent(this, clip));
		return true;
	}
	
	public Time findClipTime(Clip clip)
	{
		int index = findClip(clip);
		if (index == -1)
			return null;
		return (Time) times.elementAt(index);
	}
	
	public boolean move(Clip clip, Time delta)
	{
		int index = findClip(clip);
		if (index == -1)
			return false;
		// simplest to just delete and re-add
		Time start = (Time) times.get(index);
		elements.remove(index);
		times.remove(index);
		if (! addClipNoEvents(new Time(start, delta), clip))
		{
			addClipNoEvents(start, clip);
			return false;
		}
		notify(new MoveEvent(this, clip, delta));
		return true;
	}

	private int findClip(Clip clip)
	{
		for (int i = elements.size() - 1; i >=0; --i)
		{
			if (elements.get(i) == clip)
				return i;
		}
		return -1;
	}

	public void addChangeNotifyListener(IChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Remove a change listener.
	 * @param listener  The listener to remove
	 */
	public void removeChangeNotifyListener(IChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	private void notify(Medi8Event event)
	{
		for (int i = 0; i < listeners.size(); ++i)
		{
			((IChangeListener) listeners.get(i)).notify(event);
		}
	}
	
	public Iterator getIterator()  // FIXME rename to "iterator()" for 1.5
	{
		return new InternalIterator();
	}
	
	protected class InternalIterator implements Iterator
	{
		private Time prevTime = new Time(0);
		private int index = 0;

	    public boolean hasNext()
    	{
	    	// Return true if there are more regular elements, or
	    	// if the last regular element ends before the specified
	    	// maximum time.
			if (index < elements.size())
				return true;
			if (maxTime == null)
				return false;
			return maxTime.compareTo(prevTime) > 0;
		}

		public Object next()
		{
			Time nTime;
			if (index == elements.size())
			{
				nTime = maxTime;
				++index;
			}
			else
			{
				nTime = (Time) times.get(index);
				int cmp = prevTime.compareTo(nTime);
				if (cmp >= 0)
				{
					// In theory we should never have cmp>0, but we
					// don't check.  Note that we only increment
					// index if we are actually returning a real clip.
					// Otherwise we let it remain the same until next
					// time and let the time difference keep our state.
					Clip result = (Clip) elements.get(index++);
					prevTime = new Time(prevTime, result.getLength());
					return result;
				}
			}
			// cmp<0 means that the last returned time is before the
			// start of the next clip.  Or, we might be at the end
			// of the track.  In either case this means we need to return
			// some empty space.
			Time length = nTime.getDifference(prevTime);
			prevTime = nTime;
			return new EmptyClip(length);
		}

	    public void remove()
	    {
			throw new Error("can't remove from a Track iterator");
	    }
	}

	public void visit (Visitor v)
	{
		v.visit(this);
	}
	
	public void visitChildren (Visitor v)
	{
		for (int i = 0; i < times.size(); ++i)
		{
			((Clip) elements.get (i)).visit(v);
		}
	}
	
	// Note that we use package-private members here, to avoid
	// costly accessor methods.

	Vector listeners;
	Vector times;
	Vector elements;
	/**
	 * All tracks in a sequence must have the same apparent length.
	 * This is used to override our "natural" idea of our length.
	 */
	Time maxTime;
}
