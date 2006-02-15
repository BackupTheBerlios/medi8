/*
 * Created on Apr 3, 2003
 */
package org.medi8.internal.core.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.medi8.core.file.MLTClipFactory;
import org.medi8.internal.core.ui.figure.VideoTrackFigure;

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
        assert length.toDouble() >= 0;
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
      Figure fig = createThumbnail(width, height);
      if (fig == null)
      {
        RoundedRectangle box = new RoundedRectangle();
        box.setBounds(new Rectangle(0, 0, width, height));
        box.setBackgroundColor(ColorConstants.cyan);
        box.setCornerDimensions(new Dimension(height / 4, height / 4));
        box.setFill(true);
        fig = box;
      }
      return fig;
    }

    private Figure createThumbnail(int overallWidth, int height)
    {
        int width = (int) (height * VideoTrackFigure.ASPECT);
        return MLTClipFactory.createThumbnail(this, overallWidth, 
                                              width, height);
    }

	private Clip child;
	private Time startTime;
	private Time endTime;  // Maybe better to store length?
}
