/*
 * Created on 16-Nov-03
 */
package org.medi8.internal.core.model.events;

import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.Track;

/**
 */
public class NewTrackEvent extends Medi8Event
{
  public NewTrackEvent(Sequence source, Track track)
  {
    super(source);
    this.track = track;
  }

	private Track track;
}
