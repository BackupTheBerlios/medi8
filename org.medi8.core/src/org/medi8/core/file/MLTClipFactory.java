/*
 * Created on Nov 7, 2004
 */


package org.medi8.core.file;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.FileClip;
import org.medi8.internal.core.model.Provenance;
import org.medi8.internal.core.model.Time;

/**
 * This class is a factory to create FileClip objects. It uses MLT to find
 * information about the file. The idea is that we should minimize our explicit
 * MLT dependencies, so most interaction with the library is modularized.
 */
public class MLTClipFactory
{
  // Set to true if you want to use the medi8 tools to create
  // thumbnails.  FIXME: currently this does not really work :-(
  private static final boolean USE_THUMBNAILS = false;

  private MLTClipFactory()
  {
  }

  // We use this property when running in the build workspace (for
  // development only). static String repository;
  static String repository;
  static
    {
      String workspace = System.getProperty("medi8.workspace");
      repository = "";
      if (workspace != null)
        repository = "MLT_REPOSITORY=" + workspace + "/medi8-tools/modules";
    }

  public static Clip createClip(File file)
  {
    try
      {
        Process p = Runtime.getRuntime().exec(
                                              new String[] { "inigo",
                                                            file.toString(),
                                                            "-consumer", "info" },
                                              new String[] { repository,
                                                            "MLT_NORMALISATION=NTSC" });

        Properties prop = new Properties();
        prop.load(p.getInputStream());
        if (p.waitFor() != 0)
          return null;
        String segment = "FIXME";
        // FIXME: should get width/height, aspect ratio.
        double fps = Double.parseDouble(prop.getProperty("fps"));
        int length = Integer.parseInt(prop.getProperty("length"));
        Provenance where = new Provenance(file.toString());
        Time timeLen = new Time(length / fps);
        return new FileClip(where, timeLen, file.toString(), segment);
      }
    catch (IOException _)
      {
        return null;
      }
    catch (InterruptedException _2)
      {
        return null;
      }
  }
  
  // Create a gradient figure.
  private static Figure createGradient(int overallWidth, int height)
  {
    Figure result = new RectangleFigure()
    {
      public void paintFigure(Graphics g)
      {
        Rectangle r = getBounds();
        g.fillGradient(r.x, r.y, r.width, r.height, true);
      }
    };
    result.setBackgroundColor(new Color(Display.getCurrent(), 0x7a, 0xf3, 0x88));
    result.setForegroundColor(new Color(Display.getCurrent(), 0x32, 0xed, 0x48));
    result.setSize(overallWidth, height);
    return result;
  }

  /**
   * This constructs a thumbnail for the given file and puts it in the indicated
   * container. The container's size should be set by the caller; this function
   * might update the image incrementally.
   * 
   * @param container
   *          ImageFigure that holds the result
   * @param file
   *          File to read
   * @param overallWidth
   *          Width of resulting image
   * @param width
   *          Width of one frame of the image
   * @param height
   *          Height of one frame of the image
   */
  public static Figure createThumbnail(String file, int overallWidth,
                                       int width, int height)
  {
    if (! USE_THUMBNAILS)
      return createGradient(overallWidth, height);
    ImageFigure container = new ImageFigure();
    container.setSize(overallWidth, height);
    byte[] bytes = new byte[overallWidth * height * 3];
    ImageData data = new ImageData(overallWidth, height, 24, pd, 3, bytes);
    byte[] tbytes = new byte[width * height * 3];
    ImageData tdata = new ImageData(width, height, 24, pd, 3, tbytes);
    Process p = null;

    try
      {
        p = Runtime.getRuntime().exec(
                                      new String[] { "inigo", file.toString(),
                                                    "-consumer", "thumb",
                                                    "width=" + width,
                                                    "height=" + height,
                                                    // TODO this is a comma
                                                    // delimited list of frames.
                                                    // For now let's just grab
                                                    // the first.
                                                    "frames=0" },
                                      new String[] { repository,
                                                    "MLT_NORMALISATION=NTSC" });

        // Properties prop = new Properties();

        DataInputStream dis = new DataInputStream(p.getInputStream());
        String line = dis.readLine();
        // FIXME check that this was "P6"
        line = dis.readLine();
        // FIXME check that this was WIDTH x HEIGHT
        line = dis.readLine();
        // FIXME check that this was 255.

        // Now read the image data.
        dis.readFully(tdata.data);
      }
    catch (IOException _)
      {
        if (p != null)
          p.destroy();
        return createGradient(overallWidth, height);
      }
    try
      {
        int res = p.waitFor();
      }
    catch (InterruptedException _)
      {
        p.destroy();
      }

    // FIXME Fill this with something silly for now.
    for (int l = 0; l < height; l++)
      {
        for (int c = 0; c < overallWidth * 3; c += 3)
          {
            if (c < width * 3)
              {
                data.data[l * overallWidth * 3 + c] = tdata.data[l * width * 3
                                                                 + c];
                data.data[l * overallWidth * 3 + c + 1] = tdata.data[l * width
                                                                     * 3 + c
                                                                     + 1];
                data.data[l * overallWidth * 3 + c + 2] = tdata.data[l * width
                                                                     * 3 + c
                                                                     + 2];
              }
            else
              {
                data.data[l * overallWidth * 3 + c] = 0x5c;
                data.data[l * overallWidth * 3 + c + 1] = (byte) 0xf3;
                data.data[l * overallWidth * 3 + c + 2] = 0x6b;
              }
          }
      }

    Image img = new Image(null, data);
    container.setImage(img);
    
    return container;
  }

  private static final PaletteData pd = new PaletteData(0xff0000, 0x00ff00,
                                                        0x0000ff);
}
