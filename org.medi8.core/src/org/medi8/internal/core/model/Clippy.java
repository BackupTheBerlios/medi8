/**
 * 
 */

package org.medi8.internal.core.model;

/**
 * This is a (possibly temporary) helper class that implements the model
 * parts of "clippy" -- a simpler editing mode which limits the types of
 * changes that can be done and which does not have a multi-track timeline.
 */
public class Clippy
{
  /**
   * Add a clip to the track.
   */
  public static void addClip(VideoTrack track, Clip clip)
  {
    // For now (until we handle transitions) we only add selection
    // clips to a track.
    SelectionClip sc = new SelectionClip(clip, new Time(), clip.getLength());
    track.addClip(track.getLength(), sc);
  }

  /**
   * Add a clip to the track, before another clip.
   * @param before the existing clip
   * @param track the track to which the new clip is added
   * @param clip the clip to add
   */
  public static void addClip(Clip before, VideoTrack track, Clip clip)
  {
    Time t = track.findClipTime(before);
    track.insertClip(t, clip);
  }

  /**
   * Remove a clip from the track and slide everything down to fill the gap.
   * @param track the track
   * @param clip the clip to remove
   */
  public static void removeClip(VideoTrack track, Clip clip)
  {
    track.deleteClipAndFill(clip);
  }

  public static void resizeClip(VideoTrack track, SelectionClip clip,
                                Time start, Time end)
  {
    track.resizeSelection(clip, start, end);
  }

  private Clippy()
  {
  }
}
