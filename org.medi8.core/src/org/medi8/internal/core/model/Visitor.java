/*
 * Created on Jul 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.model;

import org.medi8.internal.core.model.audio.AudioBus;

/**
 * @author tromey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Visitor
{
  public abstract void visit (AudioBus bus);
  
	public abstract void visit (FileClip clip);
	
	public abstract void visit (EmptyClip e);
	
	public abstract void visit (DeadClip d);
	
	public abstract void visit (SelectionClip s);
	
	public abstract void visit (VideoTrack t);
	
	public abstract void visit (AutomationTrack t);
	
	public abstract void visit (Sequence s);
}
