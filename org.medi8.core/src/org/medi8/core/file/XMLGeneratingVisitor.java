/*
 * Created on Jul 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.core.file;

import java.io.PrintStream;

import org.medi8.internal.core.model.EmptyClip;
import org.medi8.internal.core.model.FileClip;
import org.medi8.internal.core.model.SelectionClip;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.model.Visitor;

/**
 * @author tromey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLGeneratingVisitor extends Visitor {
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
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.Track)
	 */
	public void visit(VideoTrack t) {
		out.println("<track>");
		t.visitChildren(this);
		out.println("</track>");
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

}
