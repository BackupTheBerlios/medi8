/*
 * Created on Dec 28, 2004
 */
package org.medi8.internal.core.model;

import org.eclipse.gef.commands.Command;

/**
 * This command wraps a given clip in a given Track
 * in a DeadClip.
 */
public class KillCommand extends Command
{
  /**
   * Create a new KillCommand.  This will wrap the indicated
   * Clip in a DeadClip.
   * @param label
   * @param track
   * @param original
   */
  public KillCommand(String label, VideoTrack track, Clip original)
  {
    super(label);
    this.track = track;
    this.original = original;
  }
  
  public void execute ()
  {
    // If we were executed and then undone the dead clip we
    // created will still be lying around.
    if (deadClip == null)
      deadClip = new DeadClip(original);
    track.replaceClip(original, deadClip);
  }
  
  public void undo ()
  {
    track.replaceClip(deadClip, original);
  }
  
  VideoTrack track;
  Clip original;
  DeadClip deadClip;
}
