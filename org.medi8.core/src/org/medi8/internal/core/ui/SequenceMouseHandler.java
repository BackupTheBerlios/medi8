package org.medi8.internal.core.ui;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.medi8.internal.core.ui.figure.SequenceFigure;

public class SequenceMouseHandler
  extends MouseHandler
{
  public SequenceMouseHandler(SequenceFigure seq, Canvas canvas, MenuManager manager)
  {
    this.seq = seq;
    this.canvas = canvas;
    this.manager = manager;
  }

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
      super.mousePressed(me);
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

  /** The sequence figure to which we're attached.  */
  SequenceFigure seq;

  /** The canvas to which we're attached.  */
  Canvas canvas;

  /** The menu manager holding the context menu.  */
  MenuManager manager;
}
