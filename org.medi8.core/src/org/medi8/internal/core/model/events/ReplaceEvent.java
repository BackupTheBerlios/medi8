/*
 * Created on Dec 28, 2004
 */
package org.medi8.internal.core.model.events;

import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.VideoTrack;

/**
 * This is sent when a clip is replaced.
 */
public class ReplaceEvent extends Medi8Event
{
  public ReplaceEvent(VideoTrack source, Clip oldClip, Clip newClip) 
  {
    super(source);
    this.oldClip = oldClip;
    this.newClip = newClip;
  }

  Clip oldClip;
  Clip newClip;
}
