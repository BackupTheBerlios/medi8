/*
 * Created on Nov 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.model.audio;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Control
{
  protected AudioEffect effect;
  protected String name; 
  
  public Control (AudioEffect effect, String name)
  {
    this.effect = effect;
    this.name = name;
  }
}
