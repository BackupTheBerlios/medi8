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

/**
 * Create a Westley XML description of a Sequence.
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
  
  /** True if the current element should be printed directly.
   * This only applies to FileClips.
   */
  private boolean printDirectly = true;

  public WestleyGenerator(PrintStream out)
  {
    this.out = out;
    this.baos = new ByteArrayOutputStream();
    this.inter = new PrintStream(baos);
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
    inter.println("    <blank length=\"" + e.getLength() + "\"/>");
  }

  public void visit(DeadClip d)
  {
    // A dead clip generates no code.
    // FIXME: it does if we're generating a westley file
    // just for the dead clip itself ...
  }

  public void visit(SelectionClip s)
  {
    printDirectly = false;
    s.visitChildren(this);
    printDirectly = true;
    String name = (String) media.get(s.getChild());
    inter.println("    <entry producer=\"" + name + "\" "
                  + "in=\"" + s.getSelectionStartTime() + "\" "
                  + "out=\"" + s.getSelectionEndTime() + "\"/>");
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
        Clip child = (Clip) it.next();
        child.visit(this);
      }
    inter.println("  </playlist>");
  }

  public void visit(AutomationTrack t)
  {
    // FIXME: implement
  }

  public void visit(Sequence s)
  {
    s.visitChildren(this);
    out.println("<westley>");
    writeMedia();
    out.println("  <tractor>");
    try {
      inter.flush();
      baos.writeTo(out);
    } catch (IOException _) {
      throw new RuntimeException(_);
    }
    out.println("  </tractor>");
    out.println("</westley>");
    out.flush();
  }
}
