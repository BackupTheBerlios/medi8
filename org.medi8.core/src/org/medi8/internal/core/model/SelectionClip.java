/*
 * Created on Apr 3, 2003
 */
package org.medi8.internal.core.model;

import org.eclipse.draw2d.Figure;

/**
 * This is a Clip that represents a selection of
 * some other clip.
 */
public class SelectionClip extends Clip implements Visitable
{
	public SelectionClip(Clip child)
	{
		super(child.location, null);
		this.child = child;
	}
	
	public SelectionClip(Clip child, Time start, Time end)
	{
		super(child.location, null);
		this.child = child;
		setTimes(start, end);
	}
	
	public void setTimes(Time start, Time end)
	{
		startTime = start;
		endTime = end;
		length = end.getDifference(start);
	}
	
	public Time getSelectionStartTime()
	{
		return startTime;
	}
	
	public Time getSelectionEndTime()
	{
		return endTime;
	}
	
	public Clip getChild()
	{
		return child;
	}
	
	public String toString()
	{
		return "SelectionClip[" + super.toString()
					+ ",child=" + child + ",start=" + startTime
					+ ",end=" + endTime + "]";
	}
    
    public String toUserString()
    {
      String result = super.toUserString();
      if (! "".equals(result))
        result = "Selection of " + result;
      return result;
    }
	
	public void visit (Visitor v)
	{
		v.visit (this);
	}
	
	public void visitChildren (Visitor v)
	{
		child.visit (v);
	}

	public Figure getFigure(int width, int height)
	{
	  // Ideally we would display frames from the
	  // selected part of the clip.  But, currently
	  // the MLT driver we have can't do this.
	  return child.getFigure(width, height);
	}

	private Clip child;
	private Time startTime;
	private Time endTime;  // Maybe better to store length?
}
