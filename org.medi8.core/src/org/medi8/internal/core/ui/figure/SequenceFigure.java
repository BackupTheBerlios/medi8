/*
 * Created on Nov 13, 2003
 */
package org.medi8.internal.core.ui.figure;

import java.util.Iterator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Canvas;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.IChangeListener;
import org.medi8.internal.core.model.Sequence;
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
		add (selectionBox);
		selectionBox.setVisible(false);
		
		setLayoutManager(new Medi8Layout(Medi8Editor.VERTICAL_GAP));
		sequence.addChangeNotifyListener(this);
		topRuler = new TimecodeRuler (TimecodeRuler.ABOVE);
		topRuler.setScale(scale);
		computeChildren();
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
	 * @param track TrackFigure holding the selection
	 * @param xLow Low X coordinate
	 * @param xHigh High X coordinate
	 */
	public void setSelection(TrackFigure track, int xLow, int xHigh, Clip clip)
	{
		Rectangle bounds = track.getBounds();
		bounds.setLocation(xLow, bounds.y - Medi8Editor.VERTICAL_GAP / 2);
		bounds.setSize(xHigh - xLow, bounds.height + Medi8Editor.VERTICAL_GAP);
		selectionBox.setBounds(bounds);
		selectionBox.setVisible(true);
		
		ISelection sel = new ClipSelection(clip, track.getTrack());
		editor.getSite().getSelectionProvider().setSelection(sel);
	}
	
	private void computeChildren()
	{
		add(topRuler);
		Iterator iter = sequence.getIterator();
		while (iter.hasNext())
		{
			VideoTrack track = (VideoTrack) iter.next();
			TrackFigure child = new TrackFigure(this, track, scale);
			add(child);
			// FIXME: at some point we'll want to unregister as well...
			adapter.addDropTargetListener(child.getDropListener(editor));
		}
	}

	private Medi8Editor editor;
	private Canvas canvas;
	private Sequence sequence;
	private Scale scale;
	private TimecodeRuler topRuler;
	
	// Some objects used for drag-and-drop.
	private DropTarget targ;
	private DelegatingDropAdapter adapter;
	
	// Information used for selection processing.
	private Figure selectionBox;
	
	// This is referenced elsewhere in the package, namely by TrackFigure.
	static Transfer fileTransfer = FileTransfer.getInstance(); 
}
