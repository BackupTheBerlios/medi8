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
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Sequence;
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
      // FIXME: we only null check here because Sequence doesn't
      // properly create the ConflictMarker yet.
      if (marker.track1 == null || marker.track2 == null)
        return;
      
      Command cmd = Sequence.createSimpleTransitionCommand(marker.track1,
                                                           marker.track2,
                                                           time);
      editor.executeCommand(cmd);
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
