/*
 * Created on Aug 4, 2004
 */


package org.medi8.internal.core.ui;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;

/**
 * This is a simple mouse handler for the main part of a Medi8
 * editor.  It handles mouse actions for the background.
 * Other figures may pass mouse events to it (via the SequenceFigure);
 * for instance this is typically done for the context menu handling.
 * @author Tom Tromey
 */
public abstract class MouseHandler
  implements MouseListener, MouseMotionListener
{
  protected MouseHandler()
  {
  }
  
  /**
   * This can be overridden to change how selections are set.
   * For a cursor setting call, it will have cursor==true;
   * in this case xhi may be ignored.
   * @param xlo the low x coordinate
   * @param xhi the high x coordinatee
   * @param cursor true for cursor-setting
   */
  protected abstract void setSelection(int xlo, int xhi, boolean cursor);

  public void mousePressed(final MouseEvent me)
  {
    if (me.button == 1)
      {
        Point p = new Point(me.x, me.y);
        // FIXME ... ?
        // seq.translateFromParent(p);
        if ((me.getState() & MouseEvent.SHIFT) != 0)
          {
            // We set dragX as it allows for shift-click-then-drag.
            if (Math.abs(p.x - xlo) < Math.abs(p.x - xhi))
              {
                xlo = p.x;
                dragX = xhi;
              }
            else
              {
                xhi = p.x;
                dragX = xlo;
              }
            setSelection(xlo, xhi, false);
          }
        else
          {
            setSelection(xlo, -1, true);
            dragX = p.x;
          }
        me.consume();
      }
  }

  private void updateBox(MouseEvent me)
  {
    Point p = new Point(me.x, me.y);
    // FIXME ... ?
    // seq.translateFromParent(p);
    if (p.x < dragX)
      {
        xlo = p.x;
        xhi = dragX;
      }
    else
      {
        xlo = dragX;
        xhi = p.x;
      }
    setSelection(xlo, xhi, xlo == xhi);
  }

  public void mouseReleased(MouseEvent me)
  {
    if (me.button == 1 && dragX != -1)
      updateBox(me);
    dragX = -1;
  }

  public void mouseDoubleClicked(MouseEvent me)
  {
    // Nothing.
  }

  public void mouseDragged(MouseEvent me)
  {
    updateBox(me);
  }

  public void mouseEntered(MouseEvent me)
  {
    // Nothing.
  }

  public void mouseExited(MouseEvent me)
  {
    // Nothing.
  }

  public void mouseHover(MouseEvent me)
  {
    // Nothing.
  }

  public void mouseMoved(MouseEvent me)
  {
    // Nothing.
  }

  /** When dragging, the starting point.  */
  int dragX = -1;
  /** Bounds of the selection rectangle.  */
  int xlo, xhi;
}
