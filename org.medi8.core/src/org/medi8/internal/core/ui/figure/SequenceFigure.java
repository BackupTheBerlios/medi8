/*
 * Created on Nov 13, 2003
 */
package org.medi8.internal.core.ui.figure;

import java.util.Iterator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Canvas;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.IChangeListener;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.Track;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.ui.ClipSelection;
import org.medi8.internal.core.ui.Medi8Layout;
import org.medi8.internal.core.ui.Scale;

/**
 * A SequenceFigure is the visual representation of a Sequence.
 */
public class SequenceFigure extends Figure implements IChangeListener
{
	public SequenceFigure(Medi8Editor editor, Sequence sequence, Scale scale)
	{
		this.editor = editor;
		this.canvas = editor.getCanvas();
		this.sequence = sequence;
		this.scale = scale;
		
		targ = new DropTarget(canvas, DND.DROP_COPY | DND.DROP_DEFAULT);
		targ.setTransfer(new Transfer[] { fileTransfer });
		
		adapter = new DelegatingDropAdapter();
		targ.addDropListener(adapter);
		
		selectionBox = new SelectionFigure (this);
		selectionBox.setVisible(false);
		
		cursorLine = new Polyline();
		cursorLine.setForegroundColor(ColorConstants.red);
		cursorLine.addPoint(new Point(0, 0));
		cursorLine.addPoint(new Point(0, 0));
		cursorLine.setVisible(false);
		
		setLayoutManager(new Medi8Layout(Medi8Editor.VERTICAL_GAP, scale));
		sequence.addChangeNotifyListener(this);
		topRuler = new TimecodeRuler (TimecodeRuler.ABOVE);
		topRuler.setScale(scale);
        dropTrack = new DropTrackFigure(this, scale);
        adapter.addDropTargetListener(dropTrack.getDropListener(editor));
		computeChildren();
	}
	
	public void setSequence(Sequence s)
	{
		this.sequence = s;
		computeChildren();
	}
    
    public Sequence getSequence()
    {
      return sequence;
    }

	public boolean useLocalCoordinates()
	{
		// All children are relative to our coordinates.
		return true;
	}
	
	public void notify(Medi8Event event)
	{
		removeAll();
		computeChildren();
	}

	public void clearSelection ()
	{
		selectionBox.setVisible(false);
		ISelection sel = new ISelection() {
			public boolean isEmpty() {
				return true;
			}
		};
		editor.getSite().getSelectionProvider().setSelection(sel);
	}

	/**
	 * Set the selection to a part of a particular track
	 * @param track VideoTrackFigure holding the selection
	 * @param xLow Low X coordinate
	 * @param xHigh High X coordinate
	 */
	public void setSelection(VideoTrackFigure track, int xLow, int xHigh, Clip clip)
	{
      Rectangle bounds;
      if (track == null)
        {
          bounds = new Rectangle(getBounds());
          bounds.setLocation(xLow, bounds.y);
          bounds.setSize(xHigh - xLow, bounds.height);
        }
      else
        {
          bounds = new Rectangle(track.getBounds());
          bounds.setLocation(xLow, bounds.y - Medi8Editor.VERTICAL_GAP / 2);
          bounds.setSize(xHigh - xLow, bounds.height + Medi8Editor.VERTICAL_GAP);
        }
      selectionBox.setBounds(bounds);
      selectionBox.setVisible(true);
      cursorLine.setVisible(false);

      if (track != null && clip != null)
        {
          ISelection sel = new ClipSelection(clip, (VideoTrack) track.getTrack());
          editor.getSite().getSelectionProvider().setSelection(sel);
        }
	}
	
	/**
	 * Set the current cursor location.
	 * If called with a negative argument, the cursor is removed.
	 */
	public void setCursorLocation(VideoTrackFigure track, int x)
	{
        selectionBox.setVisible(false);
		if (x < 0)
		  {
		    cursorLine.setVisible(false);
		    cursorTrack = null;
		  }
		else if (track != null)
		  {
		    // FIXME: should use a Time, not an x coordinate.
		    Rectangle bounds = track.getBounds();
		    cursorLine.setStart(new Point(x, bounds.y - Medi8Editor.VERTICAL_GAP / 2));
		    cursorLine.setEnd(new Point(x, bounds.y + bounds.height + Medi8Editor.VERTICAL_GAP));
		    cursorLine.setVisible(true);
		    cursorTrack = (VideoTrack) track.getTrack();
		  }
		else
		  {
		    cursorLine.setStart(new Point(x, 0));
            cursorLine.setEnd(new Point(x, getBounds().height));
            cursorLine.setVisible(true);
            cursorTrack = null;
          }
		editor.notifyCursorChange();
	}
	
	public Time getCursorTime()
	{
		if (! cursorLine.isVisible())
			return null;
		return scale.unitsToDuration(cursorLine.getBounds().x);
	}
	
	public VideoTrack getCursorTrack ()
	{
		return cursorTrack;
	}
	
	private void computeChildren()
	{
	    // Add selection box first so it is lowest on the z axis. 
        add(selectionBox);
		add(topRuler);
        add(dropTrack);

        // FIXME: adapter should be cleared somehow.  Or re-created,
        // but then we must re-add dropTrack.

		Iterator iter = sequence.getIterator();
		while (iter.hasNext())
		{
			TrackFigure child = ((Track) iter.next()).getFigure (this, scale);
			add(child);
			// FIXME: at some point we'll want to unregister as well...
			TransferDropTargetListener listener = child.getDropListener(editor);
			if (listener != null)
			  adapter.addDropTargetListener(listener);
		}
		// Make sure the cursor is always on top.
		add(cursorLine);
		
		iter = sequence.getMarkerIterator();
		while (iter.hasNext())
		{
		  Sequence.ConflictMarker marker = (Sequence.ConflictMarker) iter.next();
		  add (new ConflictMarkerFigure (marker, editor));
		}
	}

	private Medi8Editor editor;
	private Canvas canvas;
	private Sequence sequence;
	private Scale scale;
	private TimecodeRuler topRuler;
    private DropTrackFigure dropTrack;
	
	// Some objects used for drag-and-drop.
	private DropTarget targ;
	private DelegatingDropAdapter adapter;
	
	// Information used for selection processing.
	private Figure selectionBox;
	
	// The cursor line and track.
	private Polyline cursorLine;
	private VideoTrack cursorTrack;

	// This is referenced elsewhere in the package, namely by VideoTrackFigure.
	static Transfer fileTransfer = FileTransfer.getInstance();
	
	public static final boolean FIGURE_DEBUG = false;
}
