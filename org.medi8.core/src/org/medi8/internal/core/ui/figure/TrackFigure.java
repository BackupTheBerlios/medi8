/*
 * Created on Apr 7, 2003
 */
package org.medi8.internal.core.ui.figure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.medi8.core.file.OpenVIPClipFactory;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.EmptyClip;
import org.medi8.internal.core.model.FileClip;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.IChangeListener;
import org.medi8.internal.core.model.InsertOrDeleteCommand;
import org.medi8.internal.core.model.Provenance;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.ui.Scale;

/**
 * This is a figure that displays a Track.
 */
public class TrackFigure extends Figure 
	implements PropertyChangeListener, IChangeListener
{
	/**
	 * Create a new TrackFigure, given its child Track.
	 */
	public TrackFigure(SequenceFigure seq, VideoTrack track, Scale scale)
	{
		setScale(scale);
		this.sequenceFigure = seq;
		this.track = track;
		track.addChangeNotifyListener(this);
		
		TrackMouseHandler handler = new TrackMouseHandler ();
		this.addMouseMotionListener(handler);
		this.addMouseListener(handler);
		
		computeChildren();
		setBackgroundColor(ColorConstants.lightGray);
		setForegroundColor(ColorConstants.lightGray);
		setLayoutManager(new Layout());
	}

	/**
	 * Attach a Scale to this object.
	 * This is used to control the range and display.
	 * @param range
	 */
	public void setScale(Scale scale)
	{
		if (this.scale != null)
			this.scale.removeListener(this);
		this.scale = scale;
		scale.addListener(this);
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
	
	public VideoTrack getTrack ()
	{
		return track;
	}
	
	/**
	 * Called only by SequenceFigure to get the drop target listener.
	 */
	TransferDropTargetListener getDropListener(Medi8Editor editor)
	{
		return new TrackDropListener(editor);
	}
	
	private void computeChildren()
	{
		Iterator iter = track.getIterator();
		int height = getBounds().height;
		figureMap.clear();
		int i = 0;
		while (iter.hasNext())
		{
			Clip clip = (Clip) iter.next();
			Provenance prov = clip.getProvenance();
			
			Figure fig;
			int offset = scale.durationToUnits(clip.getLength());
			int width = (int) (offset / ASPECT);
			String tip = null;
			if (clip instanceof EmptyClip)
			{
				fig = createBox(ColorConstants.lightGray, width, height);
				// This is handy while debugging.
				tip = "Empty Clip";
			}
			else if (clip instanceof FileClip)
			{
				FileClip fc = (FileClip) clip;
				String file = fc.getFile();
				fig = createThumbnail(file, width, height);
				if (fig == null)
				{
					tip = tip + "; Fail: " + msg; 
					fig = createBox(ColorConstants.lightGray, width, height);
				}
			}
			else
			{
				fig = createBox((i % 2) == 0 ? ColorConstants.blue : ColorConstants.cyan,
						width, height);
				++i;
			}
			if (tip == null && prov != null)
				tip = prov.toString();
			if (tip != null)
				fig.setToolTip(new Label(tip));
			add(fig);
			figureMap.put(fig, clip);
		}
	}
	
	private Figure createBox(Color c, int width, int height)
	{
		RectangleFigure box = new RectangleFigure();
		box.setBounds(new Rectangle(0, 0, width, height));
		box.setBackgroundColor(c);
		box.setFill(true);
		return box;
	}
	
	private Figure createThumbnail(String file, int overallWidth, int height)
	{
		int width = (int) (height * ASPECT);
		ImageFigure result = new ImageFigure();
		result.setSize(overallWidth, height);
		OpenVIPClipFactory.createThumbnail(result, file, overallWidth,
				width, height);
		return result;
	}
	
	String msg; // FIXME: debug only, remove this.
	
	/**
	 * This handles layout for the children of a TrackFigure. 
	 */
	private static class Layout extends AbstractHintLayout
	{
		/**
		 * Create a new layout.
		 */
		public Layout()
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.draw2d.AbstractLayout#calculatePreferredSize(org.eclipse.draw2d.IFigure, int, int)
		 */
		protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint)
		{
			TrackFigure trackFig = (TrackFigure) container;
			int offset = 0;
			Iterator iter = trackFig.getChildren().iterator();
			while (iter.hasNext())
			{
				IFigure fig = (IFigure) iter.next();
				Dimension dim = fig.getPreferredSize();
				offset += dim.width;
			}
			
			return new Dimension(offset, 48); // FIXME
		}

		/* (non-Javadoc)
		 * @see org.eclipse.draw2d.LayoutManager#layout(org.eclipse.draw2d.IFigure)
		 */
		public void layout(IFigure seqArg)
		{
			TrackFigure trackFig = (TrackFigure) seqArg;
			int offset = 0;
			Rectangle relativeArea = trackFig.getClientArea();
			Rectangle bounds = new Rectangle();
			Iterator iter = trackFig.getChildren().iterator();
			while (iter.hasNext())
			{
				IFigure fig = (IFigure) iter.next();
				Dimension dim = fig.getPreferredSize();
				bounds.x = offset;
				bounds.y = 0;
				bounds.width = dim.width;
				bounds.height = 48;  // FIXME
				fig.setBounds(bounds);
				offset += dim.width;
			}
			
			int width = relativeArea.width;
			int widthTime = (int) (width / ASPECT);
		}
	}

	/**
	 * This handles the drop part of dnd. 
	 * FIXME: ideally there would be a generic draw2d drop listener.
	 */
	class TrackDropListener implements TransferDropTargetListener
	{
		// There must be a better way...
		Medi8Editor editor;
		Canvas canvas;
		
		public TrackDropListener(Medi8Editor editor)
		{
			this.editor = editor;
			this.canvas = editor.getCanvas();
		}
		
		public Transfer getTransfer() {
			return SequenceFigure.fileTransfer;
		}
		
		public boolean isEnabled(DropTargetEvent event) {
			// Note this is the SWT Point, not the Draw2d Point.
			// Yay Draw2d!
			Point canvPoint = canvas.toControl(event.x, event.y);
			return containsPoint(canvPoint.x, canvPoint.y);
		}

		public void dragEnter(DropTargetEvent event) {
			// FIXME: this is entry to the canvas, probably shouldn't do anything
			// here.
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0)
					event.detail = DND.DROP_COPY;
				else
					event.detail = DND.DROP_NONE;
			}
		}

		public void dragLeave(DropTargetEvent event) {
			// Nothing to do -- this is exit from the Canvas, not this item.
		}

		public void dragOperationChanged(DropTargetEvent event) {
			if (event.detail == DND.DROP_DEFAULT) {
				if ((event.operations & DND.DROP_COPY) != 0)
					event.detail = DND.DROP_COPY;
				else
					event.detail = DND.DROP_NONE;
			}
		}

		public void dragOver(DropTargetEvent event) {
			//event.feedback = DND.FEEDBACK_SCROLL;
		}

		public void drop(DropTargetEvent event) {
			String[] files = (String[]) event.data;
			// FIXME: for now only handle the first file.
			Clip clip1 = OpenVIPClipFactory.createClip(new File(files[0]));
			Point canvPoint = canvas.toControl(event.x, event.y);

			// Now transform to figure's coordinates.
			org.eclipse.draw2d.geometry.Point xform
				= new org.eclipse.draw2d.geometry.Point(canvPoint.x, canvPoint.y);
			translateToRelative(xform);
			Time when = scale.unitsToDuration(xform.x);
			
			Command cmd = new InsertOrDeleteCommand("insert", track, clip1, when);
			editor.executeCommand(cmd);
		}

		public void dropAccept(DropTargetEvent event) {
			// Nothing to do here.
		}
	}
	
	class TrackMouseHandler implements MouseListener, MouseMotionListener
	{
		private int origX;

		public void mouseDoubleClicked(MouseEvent me) {
			// Nothing to do.
		}
		public void mousePressed(MouseEvent me) {
			if (me.button != 1)
				return;
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
				sequenceFigure.setCursorLocation(track, where.x);
			}
			else
			{
				Rectangle bound = child.getBounds();
				sequenceFigure.setSelection(TrackFigure.this, bound.x, bound.x + bound.width,
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
	 * The scale we are using.
	 */
	Scale scale;
	
	/**
	 * The Track we are displaying.
	 */
	VideoTrack track;
	
	/**
	 * The sequence figure with which we're associated.
	 */
	SequenceFigure sequenceFigure;
	
	/**
	 * This maps figures to their underlying clips.
	 */
	HashMap figureMap = new HashMap();
	
	/**
	 * Aspect ratio.  FIXME: can't be fixed, but must be property of
	 * the underlying sequence.  This is width/height.
	 */
	private static double ASPECT = 4.0 / 3.0;
}
