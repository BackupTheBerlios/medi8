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
public class AddEvent extends Medi8Event
{
  public AddEvent(VideoTrack source, Time when, Clip clip)
  {
    super(source);
    this.when = when;
    this.clip = clip;
  }

	private Time when;
	private Clip clip;
}
