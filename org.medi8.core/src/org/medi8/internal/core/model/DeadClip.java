/*
 * Created on Nov 9, 2004
 */
package org.medi8.internal.core.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This class represents a dead section of a track.  It wraps some
 * other clip and declares to the conflict manager that this section
 * should not be considered in its liveness calculation.
 */
public class DeadClip extends Clip
{
  public DeadClip(Clip other)
  {
    super (other.getProvenance (), other.getLength ());
    this.child = other;
  }
  
  public Clip getChild ()
  {
    return child;
  }

  /* (non-Javadoc)
   * @see org.medi8.internal.core.model.Visitable#visit(org.medi8.internal.core.model.Visitor)
   */
  public void visit(Visitor v)
  {
    v.visit (this);
  }

  /* (non-Javadoc)
   * @see org.medi8.internal.core.model.Visitable#visitChildren(org.medi8.internal.core.model.Visitor)
   */
  public void visitChildren(Visitor v)
  {
    child.visit (v);
  }

  public Figure getFigure(int width, int height)
  {
	RectangleFigure box = new RectangleFigure();
	box.setBounds(new Rectangle(0, 0, width, height));
	box.setBackgroundColor(ColorConstants.darkGray);
	box.setFill(true);
    return box;
  }
  
  public String toString ()
  {
    return "DeadClip[" + super.toString() + "]";
  }
  
  public String toUserString()
  {
    String result = super.toUserString();
    if (! "".equals(result))
      result = "Deleted part of " + result;
    return result;
  }

  private Clip child;
}
