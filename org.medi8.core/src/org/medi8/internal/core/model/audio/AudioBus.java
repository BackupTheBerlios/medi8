/*
 * Created on Nov 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.model.audio;

import java.util.Vector;

import org.medi8.internal.core.model.AutomationTrack;
import org.medi8.internal.core.model.Track;
import org.medi8.internal.core.model.Visitable;
import org.medi8.internal.core.model.Visitor;


/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AudioBus implements Visitable
{
  private static AudioBus masterBus;
  private String name;
  
  /* The target of this bus' send, or null if this is the master bus.  */
  private AudioBus sendBus;
  
  /* A collection of inputs.  */
  private Vector inputs = new Vector ();
  
  /* A collection of effects.  */
  private Vector effects = new Vector ();
  
  /* The automation track for this bus.  */
  private AutomationTrack automationTrack;
  
  public void send (Track track)
  {
    inputs.add(track);
  }
  
	public void visit (Visitor v)
	{
	  v.visit (this);
	}
	
	public void visitChildren (Visitor v)
	{
	  for (int i = 0; i < inputs.size(); i++)
	    ((Track)inputs.elementAt(i)).visit (v);
	}

  /** Return the automation track for this audio bus.
   * 
   * @return the automation track model.
   */
  public AutomationTrack getAutomationTrack ()
  {
    if (automationTrack == null)
      automationTrack = new AutomationTrack (this);
    
    return automationTrack;
  }
  
  /**
   * Get the master bus.
   */
  public static AudioBus getMasterBus ()
  {
    if (masterBus == null)
      {
      	/* Create the master bus.
      	 * Is always has volume and balance effects.
      	 */
      	masterBus = new AudioBus ("Master");
      	masterBus.effects.add (new GainAndBalance ());
      }
    return masterBus;
  }
  
  /**
   * Get the bus name.
   */
  public String getName ()
  {
    return name;
  }
  
  /**
   * Create an audio bus.
   * 
   * @param name the name of the bus.
   */
  public AudioBus (String name)
  {
    this.name = name;
  }
}
