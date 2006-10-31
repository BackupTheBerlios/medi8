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
import org.medi8.internal.core.model.events.ReplaceEvent;
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
    
    public boolean isEmpty()
    {
      return elements.isEmpty();
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

    public boolean insertClip(Time when, Clip clip)
    {
      int index = -1;
      for (int i = 0; i < times.size(); ++i)
        {
          Time t = (Time) times.get(i);
          if (t.equals(when))
            {
              index = i;
              break;
            }
        }
      // Couldn't insert here...
      if (index == -1)
        return false;
      // Move all elements down.
      Time delta = clip.getLength();
      for (int i = index; i < times.size(); ++i)
        {
          times.set(i, new Time((Time) times.get(i), delta));
          notify(new MoveEvent(this, (Clip) elements.get(i), delta));
        }
      // Should always return true.
      return addClip(when, clip);
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
				// Note that if the end of the previous clip is the same
				// as the start of this clip, then that is ok.
				if (when.compareTo(end) < 0)
					return false;
				continue;
			}
			// Requested time comes before the start of this clip.
			// So this is our insertion point.  Make sure that the
			// end of the requested clip is also before the starting
			// point of this element in the vector.  Note that if the
			// end of the new clip is the same as the start of the next
			// clip, then that is ok.
			if (new Time(when, clip.getLength()).compareTo(start) > 0)
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

    /**
     * Remove a clip from this track and move later clips earlier to
     * fill the new gap.
     * @param clip the clip to remove
     * @return true if we removed a clip, false otherwise
     */
    public boolean deleteClipAndFill(Clip clip)
    {
        int index = findClip(clip);
        if (index == -1)
            return false;
        elements.remove(index);
        Time oldTime = (Time) times.get(index);
        times.remove(index);
        notify (new DeleteEvent(this, clip));

        // Now INDEX is the index of the next element.
        if (index < times.size())
          {
            Time delta = oldTime.getDifference((Time) times.get(index));
            while (index < times.size())
              {
                times.set(index, new Time((Time) times.get(index), delta));
                notify(new MoveEvent(this, (Clip) elements.get(index), delta));
                ++index;
              }
          }
        return true;
    }

    /**
     * Resize a selection clip and move other clips either to make room
     * (if the selection grew) or to fill the newly created gap (if the
     * selection shrunk).
     * @param clip the clip to resize
     * @param newStart the new start time of the selection
     * @param newEnd the new end time of the selection
     * @return true if the clip was successfully resized, false otherwise
     */
    public boolean resizeSelection(SelectionClip clip, Time newStart, Time newEnd)
    {
      int index = findClip(clip);
      if (index == -1)
        return false;
      // FIXME: should be able to verify that the new selection makes sense.
      clip.setTimes(newStart, newEnd);
      // FIXME: should emit an event here.
      Time delta = clip.getLength();
      while (++index < times.size())
        {
          times.set(index, new Time((Time) times.get(index), delta));
          notify(new MoveEvent(this, (Clip) elements.get(index), delta));
        }
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
	
	/**
	 * Replace one clip in this track with another clip.
	 * The new clip takes the old clip's place.
	 * The new clip must not be longer than the old clip.
	 * The old clip must be an element of this track.
	 * @param oldClip
	 * @param newClip
	 */
	public void replaceClip(Clip oldClip, Clip newClip)
	{
	  int index = findClip(oldClip);
	  if (index == -1)
	    throw new IllegalArgumentException("clip not found");
	  if (oldClip.getLength().compareTo(newClip.getLength()) < 0)
	    throw new IllegalArgumentException("new clip too long");
	  elements.set(index, newClip);
	  notify(new ReplaceEvent(this, oldClip, newClip));
	}
	
	/**
	 * Insert a split at the indicated time.  This will cause
	 * the clip at this time to be replaced by two selections
	 * of the original clip.  The first selection will end at
	 * the indicated time, and the second selection will begin
	 * where the first left off.  If the indicated time is not
	 * inside a clip, this does nothing.
	 * @param when time at which to split
	 */
	public void split(Time when)
	{
	  int index = findClip(when);
	  if (index == -1)
	    return;
	  
	  Clip clip = (Clip) elements.get(index);
	  Time start = (Time) times.get(index);
	  Time selWhen = when.getDifference(start);
	  
	  SelectionClip left = new SelectionClip(clip, new Time(), selWhen);
      // Note that the 'end' argument to the constructor is the end time in
      // the clip's timeline -- meaning that we should just reuse the clip's
      // length here.
	  SelectionClip right = new SelectionClip (clip, selWhen, clip.getLength());
	  elements.set(index, left);
	  
	  times.insertElementAt(when, index + 1);
	  elements.insertElementAt(right, index + 1);
	  
	  // FIXME: really we ought to have a new kind of event.
	  notify (new AddEvent (this, when, right));
	}
	
	/**
	 * Coalesce a previously split region.  This does nothing
	 * if the indicated time is not a split point.
	 * @param when the time at which to coalesce
	 */
	public void coalesce (Time when)
	{
	  int index = findClip(when);
	  if (index <= 0)
	    return;
	  
	  // See if the clips on either side of the time
	  // look like they were the result of a split.
	  Clip left = (Clip) elements.get(index - 1);
	  Clip right = (Clip) elements.get(index);
	  if (! (left instanceof SelectionClip)
	      || ! (right instanceof SelectionClip))
	    return;
	  SelectionClip selLeft = (SelectionClip) left;
	  SelectionClip selRight = (SelectionClip) right;
	  if (selLeft.getChild() != selRight.getChild()
	      || selLeft.getSelectionEndTime() != selRight.getSelectionStartTime())
	    return;
	  elements.set(index - 1, selLeft.getChild());
	  times.remove(index);
	  elements.remove(index);
	  // FIXME: should have a new kind of event.
	  // FIXME choice of deletion is arbitrary.
	  notify (new DeleteEvent(this, right));
	}
	
	/**
	 * Find the clip that comes before the clip covering
	 * the indicated time.
	 * @param when
	 * @return
	 */
	public Clip findClipBefore (Time when)
	{
	  int index = findClip(when);
	  if (index <= 0)
	    return null;
	  return (Clip) elements.get(index - 1);
	}
	
	/**
	 * Find the clip covering the indicated time.
	 * @param when
	 * @return
	 */
	public Clip findClipAfter (Time when)
	{
	  int index = findClip (when);
	  if (index == -1)
	    return null;
	  return (Clip) elements.get(index);
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
	
	private int findClip(Time when)
	{
	  for (int i = elements.size() - 1; i >= 0; --i)
	  {
	    Time t = (Time) times.get(i);
	    // If the time we're looking for comes before this
	    // element, keep going.
        // System.out.println("when=" + when + "; t=" + t + "; == "+ when.compareTo(t));
	    if (when.compareTo(t) < 0)
	      continue;
	    Clip clip = (Clip) elements.get(i);
	    // If the time we're looking for comes before the end
	    // of this element, we've found it.
        // System.out.println("len=" + clip.getLength() + "; t=" + new Time(t, clip.getLength()));
	    if (when.compareTo(new Time (t, clip.getLength())) < 0)
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
