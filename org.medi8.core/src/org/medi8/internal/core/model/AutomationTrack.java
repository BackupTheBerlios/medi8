/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.model;

import org.medi8.internal.core.model.audio.AudioBus;
import org.medi8.internal.core.ui.Scale;
import org.medi8.internal.core.ui.figure.SequenceFigure;
import org.medi8.internal.core.ui.figure.TrackFigure;
import org.medi8.internal.core.ui.figure.AutomationTrackFigure;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AutomationTrack extends Track
{
  // The audio bus we're tied to.
  private AudioBus bus;
  
  /** Constructor.
   * 
   */
  public AutomationTrack (AudioBus bus)
  {
    this.bus = bus;
  }
  
  // Private default constructor, so we never call it.
  private AutomationTrack ()
  {
    // This should never be used.
  }
  
  public TrackFigure getFigure (SequenceFigure seq, Scale scale)
	{
	  return new AutomationTrackFigure (seq, this, scale);
	}
	
  /* (non-Javadoc)
   * @see org.medi8.internal.core.model.Track#addChangeNotifyListener(org.medi8.internal.core.model.IChangeListener)
   */
  public void addChangeNotifyListener(IChangeListener listener)
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see org.medi8.internal.core.model.Track#getLength()
   */
  public Time getLength()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.medi8.internal.core.model.Track#setLength(Time time)
   */
  public void setLength(Time time)
  {
    // TODO Auto-generated method stub
    return;
  }

	public void visit (Visitor v)
	{
		v.visit(this);
	}
	
	/* (non-Javadoc)
   * @see org.medi8.internal.core.model.Visitable#visitChildren(org.medi8.internal.core.model.Visitor)
   */
  public void visitChildren (Visitor v)
	{
    bus.visit (v);
	}
}
