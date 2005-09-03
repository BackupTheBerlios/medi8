/*
 * Created on Aug 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.ui;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Rectangle;
import org.medi8.internal.core.ui.figure.SelectionFigure;
import org.medi8.internal.core.ui.figure.SequenceFigure;

/**
 * @author tromey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MouseHandler implements MouseListener, MouseMotionListener
{
	public MouseHandler (SequenceFigure seq)
	{
		this.seq = seq;
	}

	public void mousePressed(MouseEvent me) {
		if (me.button != 1)
			return;
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
}
