/*
 * Created on Nov 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.ui.figure;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.graphics.Image;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.SplitCommand;
import org.medi8.internal.core.model.Time;

/**
 * This represents a conflict.  A conflict is a point in time
 * at which two or more video tracks are live.
 */
public class ConflictMarkerFigure extends MarkerFigure
{
  // The marker we display.
  Sequence.ConflictMarker marker;

  /**
   * @param time
   */
  public ConflictMarkerFigure(Sequence.ConflictMarker marker, Medi8Editor editor)
  {
    super(marker.when);
    this.marker = marker;
	setToolTip(new Label ("Conflict between tracks"));

	ConflictMouseHandler handler = new ConflictMouseHandler(editor);
	this.addMouseListener(handler);
  }

  /**
   * @param image
   * @param time
   */
  public ConflictMarkerFigure(Image image, Time time)
  {
    super(image, time);
  }
  
  class ConflictMouseHandler implements MouseListener
  {
    private Medi8Editor editor;

    public ConflictMouseHandler(Medi8Editor editor)
    {
      this.editor = editor;
    }

    public void mouseDoubleClicked(MouseEvent me)
    {
      // When the user double-clicks on a conflict marker,
      // we introduce a transition at the conflict point.
      // Operationally this means inserting a split into both
      // tracks at the conflict point, then wrapping the old
      // incoming and outgoing clips with a DeadClip.
      CompoundCommand compound = new CompoundCommand ("dual split");
      // FIXME: we only null check here because Sequence doesn't
      // properly create the ConflictMarker yet.
      if (marker.track1 != null)
        compound.add(new SplitCommand ("split", marker.track1, time));
      if (marker.track2 != null)
        compound.add(new SplitCommand ("split", marker.track2, time));
      editor.executeCommand(compound);
    }
    public void mousePressed(MouseEvent me)
    {
      // Do nothing for now.
    }
    public void mouseReleased(MouseEvent me)
    {
      // Do nothing for now.
    }
  }
}
