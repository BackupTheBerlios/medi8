/*
 * Created on Nov 16, 2004
 */
package org.medi8.internal.core.model;

import org.eclipse.gef.commands.Command;

/**
 * This command is applied to a track.  It inserts a split into
 * the track at the indicated time.  A split causes the clip at
 * a given moment to be broken into two new clips, each a selection
 * of the original.  The resulting track will render the same way,
 * but the split allows the two resulting clips to be addressed
 * independently.
 */
public class SplitCommand extends Command
{
  /**
   * Create a new SplitCommand
   * @param label Label for this command
   * @param track The track to split
   * @param when  The time at which to insert the split
   */
  public SplitCommand(String label, VideoTrack track, Time when)
  {
    super(label);
    this.track = track;
    this.when = when;
  }
  
  /**
   * Create the split.
   */
  public void execute ()
  {
    track.split(when);
  }
  
  /**
   * Undo the split.
   */
  public void undo ()
  {
    track.coalesce(when);
  }
  
  private VideoTrack track;
  private Time when;
}
