/*
 * Created on Aug 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.core.file;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.EmptyClip;
import org.medi8.internal.core.model.FileClip;
import org.medi8.internal.core.model.SelectionClip;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.model.Visitable;
import org.medi8.internal.core.model.Visitor;

/**
 * @author tromey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OpenVIPNetworkVisitor extends Visitor {
	PrintStream out;
	int nameCounter = 0;
	HashMap nameMap = new HashMap();
	
	/**
	 * @param out
	 */
	public OpenVIPNetworkVisitor(PrintStream out) {
		this.out = out;
	}

	/** User code should always call this.  FixmE: make the visitor
	 * internal only.
	 * @param v
	 */
	public void generate(Visitable v)
	{
		out.println("<?xml version=\"1.0\"?>");
		out.println("<!DOCTYPE network PUBLIC \"-//OPENVIP//DTD Network Format V1.0//EN\" \"http://openvip.sourceforge.net/dtds/openvip-network.dtd\">");
		out.println("<network xmlns=\"http://openvip.sourceforge.net/network-format\" version=\"1.0\">");

		v.visit(this);

		out.println("</network>");
	}
	
	/* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.EmptyClip)
	 */
	public void visit(EmptyClip e) {
		module("NullGenerator", getId(e));
		param ("type", "video");
		param ("length", e.getLength());
		// FIXME: must emit other parameters...
		endModule();
	}

	/* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.Track)
	 */
	public void visit(VideoTrack t) {
		ArrayList localMap = new ArrayList ();
		Iterator i = t.getIterator();
		int count = 0;
		while (i.hasNext ())
		{
			Clip c = (Clip) i.next ();
			c.visit(this);
			++count;
			localMap.add(getId(c));
		}
		
		module ("Concat", getId(t));
		param ("type", "video"); // FIXME
		param ("count", "" + count);
		endModule();
		
		// FIXME: connect
	}

	/* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.Sequence)
	 */
	public void visit(Sequence s) {
		s.visitChildren(this); // anything else ...?
	}

	/* (non-Javadoc)
	 * @see org.medi8.internal.core.model.Visitor#visit(org.medi8.internal.core.model.SelectionClip)
	 */
	public void visit(SelectionClip s) {
		s.visitChildren(this);
		module("Subset", getId(s));
		param("type", "video"); // fixme
		param("pos", s.getSelectionStartTime());
		param("length", s.getLength());
		endModule();
	}

	public void visit(FileClip f)
	{
		module ("Input", getId (f));
		param ("filename", f.getFile());
		// FIXME: the sub-part ... ?
		endModule ();
	}
	
	private String getId(Object o)
	{
		if (nameMap.containsKey(o))
			return (String) nameMap.get(o);
		String result = "medi8" + nameCounter++;
		nameMap.put(o, result);
		return result;
	}
	
	private void param(String name, Object value)
	{
		out.println("    <param name=\"" + name + "\">" + value + "</param>");
	}
	
	private void module(String name, String id)
	{
		out.println("  <module class=\"" + name + "\" id=\"" + id + "\">");
	}
	
	private void endModule()
	{
		out.println("  </module>");		
	}
	
	private void connect (String moduleIn, String connectIn,
						  String moduleOut, String connectOut)
	{
		out.println("  <connect conn_in=\"" + connectIn + "\" "
				+ "conn_out=\"" + connectOut + "\" "
				+ "module_in=\"" + moduleIn + "\" "
				+ "module_out=\"" + moduleOut + "\">");
	}
}
