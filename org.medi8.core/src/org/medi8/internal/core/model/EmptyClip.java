/*
 * Created on Apr 21, 2003
 */
package org.medi8.internal.core.model;

import java.io.PrintStream;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This represents an empty clip.
 * This is ordinarily used as a placeholder to
 * tell the display modules that there is nothing to show.
 */
public class EmptyClip extends Clip
{
	/**
	 * Create a new empty clip with the given length.
	 * @param length
	 */
	public EmptyClip(Time length)
	{
		super(null, length);
	}
	
	public String toString()
	{
		return "EmptyClip[" + super.toString() + "]";
	}

	/**
	 * Write an XML representation of this clip to the specified PrintStream.
	 * @param out  The PrintStream to write to
	 */
	public void writeXML (PrintStream out)
	{
		out.println("<empty duration=\"" + length + "\" />");
	}
	
	public void visit (Visitor v)
	{
		v.visit(this);
	}
	
	public void visitChildren (Visitor v)
	{
		// Nothing.
	}

	public Figure getFigure(int width, int height)
	{
	  RectangleFigure box = new RectangleFigure();
	  box.setBounds(new Rectangle(0, 0, width, height));
	  box.setBackgroundColor(ColorConstants.lightGray);
	  box.setFill(true);
	  return box;
	}
}
