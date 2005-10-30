/**
 * 
 */
package org.medi8.internal.core.model;

import org.eclipse.gef.commands.Command;

/**
 * This is a command which can be used to add a track to a sequence.
 * The command's undo operation will remove the track from the sequence.
 * @author Tom Tromey
 */
public class AddOrDeleteTrackCommand
  extends Command
{
  private Sequence sequence;
  private Track newTrack;

  public AddOrDeleteTrackCommand(Sequence sequence, Track newTrack)
  {
    this.sequence = sequence;
    this.newTrack = newTrack;
  }
  
  public void execute()
  {
    sequence.addTrack(newTrack);
  }
  
  public void undo()
  {
    sequence.removeTrack(newTrack);
  }
}
