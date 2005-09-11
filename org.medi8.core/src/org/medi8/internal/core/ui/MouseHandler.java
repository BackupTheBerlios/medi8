/*
 * Created on Aug 4, 2004
 */
package org.medi8.internal.core.ui;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.medi8.internal.core.ui.figure.SelectionFigure;
import org.medi8.internal.core.ui.figure.SequenceFigure;

/**
 * @author tromey
 */
public class MouseHandler implements MouseListener, MouseMotionListener
{
	public MouseHandler (SequenceFigure seq, Canvas canvas, MenuManager manager)
	{
		this.seq = seq;
        this.canvas = canvas;
        this.manager = manager;
	}

	public void mousePressed(final MouseEvent me) {
		if (me.button == 1)
          {
            if (box == null)
              {
                box = new SelectionFigure (seq);
                seq.add(box);
              }
            Rectangle r = new Rectangle(seq.getBounds());
            r.setLocation(me.x, 0);
            r.setSize(0, r.height);
            box.setBounds(r);
            dragX = me.x;
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
          }
	}

	public void mouseReleased(MouseEvent me) {
		if (me.button != 1)
			return;

	}

	public void mouseDoubleClicked(MouseEvent me) {
		// Nothing.
	}

	public void mouseDragged(MouseEvent me) {
		if (me.button != 1)
			return;
		Rectangle r = box.getBounds();
		int x2 = r.x + r.width;
		int nx, nw;
		if (me.x < dragX)
		{
			nx = me.x;
			nw = dragX - me.x;
		}
		else
		{
			nx = dragX;
			nw = me.x - dragX;
		}
		r.setLocation(nx, 0);
		r.setSize(nw, r.height);
		box.setBounds(r);
	}

	public void mouseEntered(MouseEvent me) {
		// Nothing.
	}

	public void mouseExited(MouseEvent me) {
		// Nothing.
	}

	public void mouseHover(MouseEvent me) {
		// Nothing.
	}

	public void mouseMoved(MouseEvent me) {
		// Nothing.
	}
	
	SequenceFigure seq;
	SelectionFigure box;
	int dragX;
    Canvas canvas;
    MenuManager manager;
}
