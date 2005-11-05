/*
 * Created on Aug 4, 2004
 */


package org.medi8.internal.core.ui;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.medi8.internal.core.ui.figure.SequenceFigure;

/**
 * This is a simple mouse handler for the main part of a Medi8
 * editor.  It handles mouse actions for the background.
 * Other figures may pass mouse events to it (via the SequenceFigure);
 * for instance this is typically done for the context menu handling.
 * @author Tom Tromey
 */
public class MouseHandler
  implements MouseListener, MouseMotionListener
{
  public MouseHandler(SequenceFigure seq, Canvas canvas, MenuManager manager)
  {
    this.seq = seq;
    this.canvas = canvas;
    this.manager = manager;
  }
  
  /**
   * This can be overridden to change how selections are set.
   * For a cursor setting call, it will have cursor==true;
   * in this case xhi may be ignored.
   * @param xlo the low x coordinate
   * @param xhi the high x coordinatee
   * @param cursor true for cursor-setting
   */
  protected void setSelection(int xlo, int xhi, boolean cursor)
  {
    if (cursor)
      seq.setCursorLocation(null, xlo);
    else
      seq.setSelection(null, xlo, xhi, null);
  }

  public void mousePressed(final MouseEvent me)
  {
    if (me.button == 1)
      {
        Point p = new Point(me.x, me.y);
        seq.translateFromParent(p);
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
    else if (me.button == 3)
      {
        Display dpy = canvas.getShell().getDisplay();
        dpy.asyncExec(new Runnable()
        {
          public void run()
          {
            if (canvas.getShell().isDisposed())
              return;
            Menu menu = manager.createContextMenu(canvas);
            menu.setLocation(canvas.toDisplay(me.x, me.y));
            menu.setEnabled(true);
            menu.setVisible(true);
          }
        });
        me.consume();
      }
  }

  private void updateBox(MouseEvent me)
  {
    Point p = new Point(me.x, me.y);
    seq.translateFromParent(p);
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

  /** The sequence figure to which we're attached.  */
  SequenceFigure seq;

  /** When dragging, the starting point.  */
  int dragX = -1;
  /** Bounds of the selection rectangle.  */
  int xlo, xhi;

  /** The canvas to which we're attached.  */
  Canvas canvas;

  /** The menu manager holding the context menu.  */
  MenuManager manager;
}
