/*
 * Created on Nov 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.model.audio;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AudioEffect
{
  private String name;
  protected Vector controls;
  
  public AudioEffect (String name)
  {
    this.name = name;
    controls = new Vector();
  }
  
  public Enumeration getControls ()
  {
    return controls.elements();
  }
}
