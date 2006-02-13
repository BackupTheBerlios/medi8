/*
 * Created on Aug 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.medi8.core.file.MLTClipFactory;
import org.medi8.internal.core.ui.figure.VideoTrackFigure;

/**
 * @author tromey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FileClip extends Clip implements Visitable
{
	/** The file this clip came from.  */
	private String file;
	/** The sub-part of the file that this clip comes from.	 */
	private String subPart;
    /** Frames per second, or -1 if not applicable.  */
    private int fps;
	
	public FileClip (Provenance location, Time length, String file,
	                 String subPart, int fps)
	{
		super (location, length);
		this.file = file;
		this.subPart = subPart;
        this.fps = fps;
	}
	
	public String getFile ()
	{
		return file;
	}
	
	public String getPart ()
	{
		return subPart;
	}
    
    public int getFPS ()
    {
      return fps;
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
		return MLTClipFactory.createThumbnail(file, overallWidth, 
                                              width, height);
	}
}
