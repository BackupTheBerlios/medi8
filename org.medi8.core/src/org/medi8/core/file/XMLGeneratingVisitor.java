/*
 * Created on Jul 31, 2004
 */
package org.medi8.core.file;

import java.io.PrintStream;
import java.util.Iterator;

import org.medi8.internal.core.model.AutomationTrack;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.DeadClip;
import org.medi8.internal.core.model.EmptyClip;
import org.medi8.internal.core.model.FileClip;
import org.medi8.internal.core.model.SelectionClip;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.model.Visitor;
import org.medi8.internal.core.model.audio.AudioBus;

/**
 * This class generates an XML representation of a given sequence.
 * It is used for saving a sequence.  The generated format is read
 * by Medi8XMLParser; changes here must be reflected there.
 */
public class XMLGeneratingVisitor extends Visitor
{
	/** Where we send the XML.  */
	PrintStream out;
	
	public XMLGeneratingVisitor (PrintStream out)
	{
		this.out = out;
	}

	/* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.EmptyClip)
	 */
	public void visit(EmptyClip e) {
		// Nothing.
	}

	/* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.audio.AudioBus)
	 */
	public void visit(AudioBus bus) {
	  out.println("<audiobus>");
	  // FIXME: bus.visitChildren(this);
	  out.println("</audiobus>");
	}

	  /* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.VideoTrack)
	 */
	public void visit(VideoTrack vt) {
	  out.println("<videotrack>");
	  Time now = new Time();
  	Iterator i = vt.getIterator();
  	while (i.hasNext())
    	{
    		Clip c = (Clip) i.next();
    		if (! (c instanceof EmptyClip))
    		  {
    		  	out.println("<clip time=\"" + now + "\">");
    		  	c.visit(this);
    		  	out.println("</clip>");
    		  }
    		  now = new Time(now, c.getLength());
    	}
    	out.println("</videotrack>");
	}
	

/* (non-Javadoc)
 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.Track)
 */
public void visit(AutomationTrack t) {
  out.println("<automationtrack>");
  t.visitChildren(this);
  out.println("</automationtrack>");
}

/* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.Sequence)
	 */
	public void visit(Sequence s) {
		out.println("<sequence>");
		s.visitChildren(this);
		out.println("</sequence>");
	}

	/* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.Provenance)
	 */
	public void visit(FileClip f) {
		out.println("<file name=\"" + f.getFile() + "\" "
				+ "length=\"" + f.getLength() + "\" "
				+ "subpart=\"" + f.getPart() + "\"/>");
	}
	
	public void visit(SelectionClip s)
	{
		out.println("<select start=\"" + s.getSelectionStartTime()
				+ "\" end=\"" + s.getSelectionEndTime()
				+ "\">");
		s.visitChildren(this);
		out.println("</select>");
	}
	
	/* (non-Javadoc)
   * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.DeadClip)
   */
  public void visit(DeadClip d)
  {
    out.println("<dead>");
    d.visitChildren(this);
    out.println("</dead>");
  }
}
