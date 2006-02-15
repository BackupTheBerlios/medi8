package org.medi8.core.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.medi8.internal.core.model.AutomationTrack;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.DeadClip;
import org.medi8.internal.core.model.EmptyClip;
import org.medi8.internal.core.model.FileClip;
import org.medi8.internal.core.model.SelectionClip;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.model.Visitor;
import org.medi8.internal.core.model.audio.AudioBus;

// FIXME: known bugs in this code:
// * It needs to emit a transition whenver there is a <blank>

/**
 * Create a Westley XML description of a Sequence.
 * The documentation for this format is at
 * http://www.dennedy.org/mlt/twiki/bin/view/MLT/Westley
 */
public class WestleyGenerator
  extends Visitor
{
  /** Where we ultimately send the XML.  */
  private PrintStream out;
  
  /** Intermediate result.  We emit Westley's linear form,
   * so we first collect the underlying media and, meanwhile,
   * emit the playlists and whatnot to this stream.
   */
  private PrintStream inter;
  private ByteArrayOutputStream baos;
  
  /** This collects the final media.  */
  private HashMap media = new HashMap();
  
  /** Next playlist ID.  */
  private int playListID = 0;
  
  /** Frames per second for the sequence, or 1 if not known.
   * We use 1 instead of the more common -1 so that we don't have
   * to conditionalize our multiplies.
   */
  private int fps;
  
  /** True if the current element should be printed directly.
   * This only applies to FileClips.
   */
  private boolean printDirectly = true;
  
  /** True if a DeadClip should render itself.  */
  private boolean renderDeadClip = false;

  public WestleyGenerator(PrintStream out)
  {
    this.out = out;
    this.baos = new ByteArrayOutputStream();
    this.inter = new PrintStream(baos);
  }
  
  private void writeBuffer()
  {
    try {
      inter.flush();
      baos.writeTo(out);
    } catch (IOException _) {
      throw new RuntimeException(_);
    }
  }
  
  /**
   * The primary interface to generating Westley for a Sequence.
   * Use this and not visit.
   * @param seq the sequence
   */
  public void generate(Sequence seq)
  {
    fps = seq.getFPS();
    if (fps == -1)
      fps = 1;
    seq.visit(this);
    out.println("<westley>");
    writeMedia();
    writeBuffer();
    out.println("  <tractor>");
    out.println("    <multitrack>");
    for (int i = 0; i < playListID; ++i)
      out.println("      <track producer=\"playlist" + i + "\"/>");
    out.println("    </multitrack>");
    out.println("  </tractor>");
    out.println("</westley>");
    out.flush();
  }

  /**
   * Generate some Westley XML describing a single clip, which is
   * contained in the given sequence.
   * This treats DeadClips specially.
   * @param clip the clip
   */
  public void generate(Clip clip)
  {
    renderDeadClip = clip instanceof DeadClip;
    inter.println("  <playlist id=\"playlist0\">");
    clip.visit(this);
    inter.println("  </playlist>");
    out.println("<westley>");
    writeMedia();
    writeBuffer();
    out.println("  <tractor>");
    out.println("    <multitrack>");
    out.println("      <track producer=\"playlist0\"/>");
    out.println("    </multitrack>");
    out.println("  </tractor>");
    if (renderDeadClip)
      out.println("  <filter mlt_service=\"greyscale\" track=\"0\"/>");
    out.println("</westley>");
    out.flush();
  }

  public void visit(AudioBus bus)
  {
    // FIXME: implement
  }

  public void visit(FileClip clip)
  {
    String name;
    if (! media.containsKey(clip))
      {
        name = "producer" + media.size();
        media.put(clip, name);
      }
    else
      name = (String) media.get(clip);
    if (printDirectly)
      inter.println("    <entry producer=\"" + name + "\"/>");
    if (fps == 0)
      {
        fps = clip.getFPS();
        if (fps == -1)
          fps = 1;
      }
  }
  
  private void writeMedia()
  {
    Iterator i = media.entrySet().iterator();
    while (i.hasNext())
      {
        Map.Entry entry = (Map.Entry) i.next();
        out.println("  <producer id=\"" + entry.getValue() +"\">");
        FileClip fc = (FileClip) entry.getKey();
        out.println("    <property name=\"resource\">" + fc.getFile()
                    + "</property>");
        out.println("  </producer>");
      }
  }

  public void visit(EmptyClip e)
  {
    inter.println("    <blank length=\"" + (fps * e.getLength().toDouble())
                  + "\"/>");
  }

  public void visit(DeadClip d)
  {
    // A dead clip is ordinarily empty, but if we need to, we render it as
    // its underlying clip; in this case generate will apply a greyscale.
    if (renderDeadClip)
      d.visitChildren(this);
    else
      inter.println("    <blank length=\"" + (fps * d.getLength().toDouble())
                    + "\"/>");
  }

  public void visit(SelectionClip s)
  {
    printDirectly = false;
    s.visitChildren(this);
    String name = (String) media.get(s.getChild());
    inter.println("    <entry producer=\"" + name + "\" "
                  + "in=\"" + (fps * s.getSelectionStartTime().toDouble())
                  + "\" "
                  + "out=\"" + (fps * s.getSelectionEndTime().toDouble())
                  + "\"/>");
  }

  public void visit(VideoTrack t)
  {
    // Don't bother if the track is empty.
    if (t.isEmpty())
      return;
    inter.println("  <playlist id=\"playlist" + playListID++
                  + "\">");
    // Iterate instead of using visitChildren, as this way we
    // will get EmptyClips.
    Iterator it = t.getIterator();
    while (it.hasNext())
      {
        printDirectly = true;
        Clip child = (Clip) it.next();
        child.visit(this);
      }
    inter.println("  </playlist>");
  }

  public void visit(AutomationTrack t)
  {
    // FIXME: implement
  }

  // This should only be called by generate.
  // FIXME: move the visitor into a private class.
  public void visit(Sequence s)
  {
    s.visitChildren(this);
  }
}
