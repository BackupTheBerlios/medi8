/*
 * Created on 16-Nov-03
 */
package org.medi8.internal.core.model.events;

import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.VideoTrack;

/**
 */
public class DeleteEvent extends Medi8Event
{
  public DeleteEvent(VideoTrack source, Clip clip)
  {
    super(source);
    this.clip = clip;
  }

	private Clip clip;
}
