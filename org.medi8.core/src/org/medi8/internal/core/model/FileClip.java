/*
 * Created on Aug 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.model;

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
	
	public FileClip (Provenance location, Time length, String file,
			String subPart)
	{
		super (location, length);
		this.file = file;
		this.subPart = subPart;
	}
	
	public String getFile ()
	{
		return file;
	}
	
	public String getPart ()
	{
		return subPart;
	}
	
	public void visit (Visitor v)
	{
		v.visit(this);
	}
	
	public void visitChildren (Visitor v)
	{
		// Nothing.
	}
}
