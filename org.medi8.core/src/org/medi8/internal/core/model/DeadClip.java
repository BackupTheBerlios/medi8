/*
 * Created on Nov 9, 2004
 */
package org.medi8.internal.core.model;

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
  
  private Clip child;
}
