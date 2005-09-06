package org.medi8.internal.core.ui.figure;

import java.io.File;

import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.medi8.core.file.MLTClipFactory;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.Time;

/**
 * This handles the drop part of dnd.  Users should extend this
 * class to provide a method which actually handles the final
 * part of the drop.
 * FIXME: ideally there would be a generic draw2d drop listener.
 * FIXME: we need a way to indicate whether the drop is valid at
 * a given point.
 */
abstract class TrackDropListener implements TransferDropTargetListener
{
  private final TrackFigure figure;

  // There must be a better way...
  Medi8Editor editor;

  Canvas canvas;

  public TrackDropListener(TrackFigure figure, Medi8Editor editor)
  {
    this.figure = figure;
    this.editor = editor;
    this.canvas = editor.getCanvas();
  }

  public Transfer getTransfer()
  {
    return SequenceFigure.fileTransfer;
  }

  public boolean isEnabled(DropTargetEvent event)
  {
    // Note this is the SWT Point, not the Draw2d Point.
    // Yay Draw2d!
    Point canvPoint = canvas.toControl(event.x, event.y);
    return this.figure.containsPoint(canvPoint.x, canvPoint.y);
  }

  public void dragEnter(DropTargetEvent event)
  {
    // FIXME: this is entry to the canvas, probably shouldn't do anything
    // here.
    if (event.detail == DND.DROP_DEFAULT)
      {
        if ((event.operations & DND.DROP_COPY) != 0)
          event.detail = DND.DROP_COPY;
        else
          event.detail = DND.DROP_NONE;
      }
  }

  public void dragLeave(DropTargetEvent event)
  {
    // Nothing to do -- this is exit from the Canvas, not this item.
  }

  public void dragOperationChanged(DropTargetEvent event)
  {
    if (event.detail == DND.DROP_DEFAULT)
      {
        if ((event.operations & DND.DROP_COPY) != 0)
          event.detail = DND.DROP_COPY;
        else
          event.detail = DND.DROP_NONE;
      }
  }

  public void dragOver(DropTargetEvent event)
  {
    // event.feedback = DND.FEEDBACK_SCROLL;
  }

  /**
   * Implemented by subclasses to handle the actual drop.
   * The parameters are the clip and the time at which
   * the drop should be done.
   * @param clip the clip
   * @param when the time
   */
  public abstract void handleDrop(Clip clip, Time when);

  public void drop(DropTargetEvent event)
  {
    String[] files = (String[]) event.data;
    // FIXME: for now only handle the first file.
    Clip clip1 = MLTClipFactory.createClip(new File(files[0]));
    Point canvPoint = canvas.toControl(event.x, event.y);

    // Now transform to figure's coordinates.
    org.eclipse.draw2d.geometry.Point xform
      = new org.eclipse.draw2d.geometry.Point(canvPoint.x, canvPoint.y);
    this.figure.translateToRelative(xform);
    Time when = this.figure.scale.unitsToDuration(xform.x);

    handleDrop(clip1, when);
  }

  public void dropAccept(DropTargetEvent event)
  {
    // Nothing to do here.
  }
}