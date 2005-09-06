package org.medi8.internal.core.ui.figure;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.InsertOrDeleteCommand;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.ui.Scale;

/**
 * This is a track figure which is used to dynamically create
 * new tracks as the user drops files and other resources onto it.
 * @author tromey
 */
public class DropTrackFigure
  extends TrackFigure
{
  public DropTrackFigure(SequenceFigure fig, Scale scale)
  {
    super(fig, scale);
    Dimension size = new Dimension(0, Medi8Editor.CLIP_HEIGHT / 4);
    setSize(size);
    setBackgroundColor(new Color(Display.getCurrent(), 0x2d, 0xba, 0xaa));
    setForegroundColor(new Color(Display.getCurrent(), 0x57, 0xcb, 0xca));
    setToolTip(new Label("Drop media here to create new track"));
  }
  
  protected void paintFigure(Graphics g)
  {
    Rectangle r = getBounds();
    g.fillGradient(r.x, r.y, r.width, r.height, true);
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
        // FIXME: we need a way to add a new track via a command
        // and then get this into the insertion command.
        VideoTrack newTrack = new VideoTrack();  // FIXME: always video?
        sequenceFigure.getSequence().addTrack(newTrack);
        Command cmd = new InsertOrDeleteCommand("insert", newTrack, clip, when);
        editor.executeCommand(cmd);
      }
    };
  }

  public void propertyChange(PropertyChangeEvent arg0)
  {
  }

  public void notify(Medi8Event event)
  {
  }
}
