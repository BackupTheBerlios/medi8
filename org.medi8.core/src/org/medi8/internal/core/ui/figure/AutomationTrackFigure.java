/*
 * Created on Nov 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.ui.figure;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.AutomationTrack;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.ui.Scale;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AutomationTrackFigure extends TrackFigure
{
  private AutomationTrack track;
  
  public AutomationTrackFigure(SequenceFigure seq, AutomationTrack track, Scale scale)
	{
        super(seq, scale);

		this.track = track;
		track.addChangeNotifyListener(this);
		
		/* TODO: add mouse handler
		TrackMouseHandler handler = new TrackMouseHandler ();
		this.addMouseMotionListener(handler);
		this.addMouseListener(handler);
		*/

		// Set our height before computing children;
		// otherwise we could end up with a strangely sized
		// clip image.
		Dimension size = getSize();
		size.height = Medi8Editor.CLIP_HEIGHT;
		setSize(size);

		computeChildren();
		setBackgroundColor(ColorConstants.lightGray);
		setForegroundColor(ColorConstants.lightGray);
		setLayoutManager(new Layout());
	}
  
	private void computeChildren()
	{
	  // TODO: draw this thing
	  return;
	}
	
  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt)
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see org.medi8.internal.core.model.IChangeListener#notify(org.medi8.internal.core.model.Medi8Event)
   */
  public void notify(Medi8Event event)
  {
    // TODO Auto-generated method stub

  }
}
