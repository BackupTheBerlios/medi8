/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.model.audio;

import org.medi8.core.file.AudioServer;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RangeControl extends Control
{ 
  private float defaultValue;
  private float value;
  
  public RangeControl (AudioEffect effect, String name, float defaultValue)
  {
    super (effect, name);
    this.defaultValue = defaultValue;
    value = defaultValue;
  }
  
  public float getValue ()
  {
    return value;
  }
  
  public float setValue (float value)
  {
    this.value = value;
    // TODO send the new value to the audio server
    // AudioServer.send("", null);
    return value;
  }
  
  public float reset ()
  {
    value = defaultValue;
    return value;
  }
}
