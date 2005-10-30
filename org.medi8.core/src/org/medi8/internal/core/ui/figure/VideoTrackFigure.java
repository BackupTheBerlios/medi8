/*
 * Created on Apr 7, 2003
 */
package org.medi8.internal.core.ui.figure;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.EmptyClip;
import org.medi8.internal.core.model.InsertOrDeleteCommand;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.Provenance;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.Track;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.ui.Scale;

/**
 * This is a figure that displays a Track.
 */
public class VideoTrackFigure extends TrackFigure 
{
	/**
	 * Create a new VideoTrackFigure, given its child Track.
	 */
	public VideoTrackFigure(SequenceFigure seq, VideoTrack track, Scale scale)
	{
        super(seq, scale);

		this.track = track;
		track.addChangeNotifyListener(this);
		
		TrackMouseHandler handler = new VideoTrackMouseHandler ();
		this.addMouseMotionListener(handler);
		this.addMouseListener(handler);

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

	// This is called when something changes on the range model.
	// We're only interested in changes to the scale.
	public void propertyChange(PropertyChangeEvent evt)
	{
		// Might consider actually looking at the event.
		// But for now, regenerate the figure when the scale changes.
		removeAll();
		computeChildren();
	}
	
	public boolean useLocalCoordinates()
	{
		// All children are relative to our coordinates.
		return true;
	}
	
	public void notify(Medi8Event event)
	{
		// Remove all the children and start over.
		// FIXME: inefficient; fix later by looking at the
		// event and doing something intelligent.
		removeAll();
		computeChildren();
	}
	
	public Track getTrack ()
	{
		return track;
	}
	
	/**
	 * Called only by SequenceFigure to get the drop target listener.
	 */
	TransferDropTargetListener getDropListener(Medi8Editor editor)
	{
		return new TrackDropListener(this, editor)
        {
		  public void handleDrop(Clip clip, Time when)
          {
            Command cmd = new InsertOrDeleteCommand("insert", track, clip, when);
            editor.executeCommand(cmd);
          }
        };
	}
	
	private void computeChildren()
	{
		Iterator iter = track.getIterator();
		int height = getBounds().height;
		figureMap.clear();
		double now = 0;
		while (iter.hasNext())
		{
			Clip clip = (Clip) iter.next();
			Provenance prov = clip.getProvenance();
			
			int width = scale.durationToUnits(clip.getLength());
			String tip = clip.toUserString();
			Figure fig = clip.getFigure(width, height);
			if (tip == null && prov != null)
				tip = prov.toString();
			if (SequenceFigure.FIGURE_DEBUG)
			{
			  tip += ("\nwidth = " + width + "\nTime = " + now
			      + "\nX = " + scale.durationToUnit(now));
			}
			if (tip != null && ! "".equals(tip))
				fig.setToolTip(new Label(tip));
			add(fig);
			figureMap.put(fig, clip);
			now += clip.getLength().toDouble();
		}
	}
    
	class VideoTrackMouseHandler extends TrackMouseHandler
	{
		private int origX;

		public void mouseDoubleClicked(MouseEvent me) {
			// Nothing to do.
		}
		public void mousePressed(MouseEvent me) {
			if (me.button != 1)
              {
                sequenceFigure.handleMousePressed(me);
                return;
              }
            me.consume();
			origX = me.x;
			// Sigh: two kinds of Point.
			org.eclipse.draw2d.geometry.Point where 
				= new org.eclipse.draw2d.geometry.Point(me.x, me.y);
			translateFromParent(where);
			Clip clip = null;
			IFigure child = null;
			Iterator iter = getChildren().iterator();
			while (iter.hasNext())
			{
				IFigure c = (IFigure) iter.next ();
				if (c.containsPoint(where))
				{
					child = c;
					break;
				}
			}
			if (child != null)
			{
				clip = (Clip) figureMap.get(child);
				if (clip instanceof EmptyClip)
					clip = null;
			}
			if (clip == null)
			{
				sequenceFigure.clearSelection();
				sequenceFigure.setCursorLocation((VideoTrack) track, where.x);
			}
			else
			{
				Rectangle bound = child.getBounds();
				sequenceFigure.setSelection(VideoTrackFigure.this, bound.x, bound.x + bound.width,
						clip);
				sequenceFigure.setCursorLocation(null, -1);
			}
		}
		public void mouseReleased(MouseEvent me) {
			// Nothing to do.
		}
		public void mouseDragged(MouseEvent me) {
			int loX = Math.min(me.x, origX);
			int hiX = Math.max(me.x, origX);
			// sequenceFigure.setSelection(track, loX, hiX);
		}
		public void mouseEntered(MouseEvent me) {
			// Nothing to do.
		}
		public void mouseExited(MouseEvent me) {
			// Nothing to do.
		}
		public void mouseHover(MouseEvent me) {
			// Nothing to do.
		}
		public void mouseMoved(MouseEvent me) {
			// Nothing to do.
		}
	}
	
	/**
	 * The Track we are displaying.
	 */
	VideoTrack track;

	/**
	 * This maps figures to their underlying clips.
	 */
	HashMap figureMap = new HashMap();
	
	/**
	 * Aspect ratio.  FIXME: can't be fixed, but must be property of
	 * the underlying sequence.  This is width/height.
	 */
	public static double ASPECT = 4.0 / 3.0;
}
