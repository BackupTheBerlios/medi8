/*
 * Created on Nov 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.model.audio;

import java.util.Vector;


/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AudioBus
{
  private static AudioBus masterBus;
  private String name;
  
  /* The target of this bus' send, or null if this is the master bus.  */
  AudioBus sendBus;
  
  /* A collection of inputs.  */
  Vector inputBusVector = new Vector ();
  
  /* A collection of effects.  */
  Vector effects = new Vector ();
  
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
