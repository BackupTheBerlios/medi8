/*
 * Created on 16-Nov-03
 */
package org.medi8.internal.core.model.events;

import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.VideoTrack;

/**
 */
public class MoveEvent extends Medi8Event
{
  public MoveEvent(VideoTrack source, Clip clip, Time delta)
  {
    super(source);
    this.clip = clip;
    this.delta = delta;
  }

	private Clip clip;
	private Time delta;
}
