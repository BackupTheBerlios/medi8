/*
 * Created on Nov 13, 2004
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
public class GainAndBalance extends AudioEffect
{
  public GainAndBalance ()
  {
    super("Gain and Balance");
    controls.add(new BoundedRangeControl(this, "gain", 1.0f, 0.0f, 1.0f));
    controls.add(new BoundedRangeControl(this, "balance", 0.0f, -1.0f, 1.0f));
  }
}
