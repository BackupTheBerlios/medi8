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
public class BoundedRangeControl extends RangeControl
{
  private float min;
  private float max;
  /**
   * @param effect
   * @param name
   * @param defaultValue
   */
  public BoundedRangeControl(AudioEffect effect, String name, 
                            float defaultValue, float min, float max)
  {
    super(effect, name, defaultValue);
    this.min = min;
    this.max = max;
  }

  public float setValue (float value)
  {
    value = Math.max(Math.min(max, value), min);
    return super.setValue(value);
  }
}
