/*
 * Created on Dec 28, 2004
 */
package org.medi8.internal.core.model;

/**
 * This is like a KillCommand, but it takes a Time argument
 * instead of a Clip.  This will wrap the clip before the
 * given Time in a DeadClip.  It is intended to kill the left
 * hand side of a split, as introduced by SplitCommand.
 */
public class KillBeforeCommand extends KillCommand
{
  public KillBeforeCommand(String label, VideoTrack track, Time when)
  {
    super(label, track, null);
    this.when = when;
  }
  
  public void execute ()
  {
    if (original == null)
      original = track.findClipBefore(when);
    // System.out.println("KillBeforetrack " + track + " at " + when + " clip "+ original);
    super.execute ();
  }
  
  Time when;
}
