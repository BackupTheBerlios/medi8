/*
 * Created on Dec 28, 2004
 */
package org.medi8.internal.core.model;

/**
 * This is like a KillCommand, but it takes a Time argument
 * instead of a Clip.  This will wrap the clip at or after the
 * given Time in a DeadClip.  It is intended to kill the right
 * hand side of a split, as introduced by SplitCommand.
 */
public class KillAfterCommand extends KillCommand
{
  public KillAfterCommand(String label, VideoTrack track, Time when)
  {
    super(label, track, null);
    this.when = when;
  }
  
  public void execute ()
  {
    if (original == null)
      original = track.findClipAfter(when);
    // System.out.println("KillAfter track " + track + " at " + when + " clip "+ original);
    super.execute ();
  }
  
  Time when;
}
