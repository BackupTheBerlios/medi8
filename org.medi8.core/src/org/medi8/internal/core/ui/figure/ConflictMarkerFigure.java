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
import org.eclipse.swt.graphics.Image;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.ui.MouseHandler;

/**
 * This represents a conflict.
 */
public class ConflictMarkerFigure extends MarkerFigure
{
  /**
   * @param time
   */
  public ConflictMarkerFigure(Time time)
  {
    super(time);
	setToolTip(new Label ("Conflict between tracks"));

	ConflictMouseHandler handler = new ConflictMouseHandler();
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
    public void mouseDoubleClicked(MouseEvent me)
    {
      System.out.println("insert cut at t=" + time);

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
