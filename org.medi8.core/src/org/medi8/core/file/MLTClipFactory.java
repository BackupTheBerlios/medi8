/*
 * Created on Nov 7, 2004
 */
package org.medi8.core.file;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.FileClip;
import org.medi8.internal.core.model.Provenance;
import org.medi8.internal.core.model.Time;

/**
 * This class is a factory to create FileClip objects.
 * It uses MLT to find information about the file.
 * The idea is that we should minimize our explicit MLT
 * dependencies, so most interaction with the library is
 * modularized.
 */
public class MLTClipFactory
{
	private MLTClipFactory()
	{
	}
	
  // We use this property when running in the build workspace (for
  // development only).	static String repository;
	static String repository; 
	static {
	    String workspace = System.getProperty ("medi8.workspace");
	    repository = "";
	    if (workspace != null)
	    	repository = "MLT_REPOSITORY=" + workspace + "/medi8-tools/modules";
	}
	
	public static Clip createClip(File file)
	{
		try
		{
			Process p = Runtime.getRuntime().exec(new String[] {
					"inigo",
					file.toString(),
					"-consumer",
					"info"
			},
				new String[] {
			        repository,
			        "MLT_NORMALISATION=NTSC"
			});
			
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
	
	/**
	 * This constructs a thumbnail for the given file and puts it in the
	 * indicated container.  The container's size should be set by the caller;
	 * this function might update the image incrementally.
	 * @param container ImageFigure that holds the result
	 * @param file File to read
	 * @param overallWidth Width of resulting image
	 * @param width Width of one frame of the image
	 * @param height Height of one frame of the image
	 */
	public static void createThumbnail(final ImageFigure container, String file, 
			int overallWidth, int width, int height)
	{
		byte[] bytes = new byte[overallWidth * height * 3];
		ImageData data = new ImageData(overallWidth, height, 24, pd, 3, bytes);
		byte[] tbytes = new byte[width * height * 3];
		ImageData tdata = new ImageData (width, height, 24, pd, 3, tbytes);
		Process p = null;
	
		try
		{
			p = Runtime.getRuntime().exec(new String[] {
					"inigo",
					file.toString(),
					"-consumer",
					"thumb",
					"width=" + width,
					"height=" + height,
					// TODO this is a comman delimited list of frames.
					// For now let's just grab the first.
					"frames=0"
			},
				new String[] {
			        repository,
			        "MLT_NORMALISATION=NTSC"
			});
			
			Properties prop = new Properties();
			
			DataInputStream dis = new DataInputStream (p.getInputStream ());
			String line = dis.readLine ();
			// FIXME check that this was "P6"
			line = dis.readLine ();
			// FIXME check that this was WIDTH x HEIGHT
			line = dis.readLine ();
			// FIXME check that this was 255.
			
			// Now read the image data.
			dis.readFully (tdata.data);
		}
		catch (IOException _)
		{
		  if (p != null)
		    p.destroy ();
		  return;
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
		  	  	  	data.data[l * overallWidth * 3 + c] = tdata.data[l * width * 3 + c];
		  	  	  	data.data[l * overallWidth * 3 + c + 1] = tdata.data[l * width * 3 + c + 1];
		  	  	  	data.data[l * overallWidth * 3 + c + 2] = tdata.data[l * width * 3 + c + 2];
		  	  	  }
		  	  	else
		  	  	  {
	  	  	  		data.data[l * overallWidth * 3 + c] = 30;
	  	  	  		data.data[l * overallWidth * 3 + c + 1] = 78;
	  	  	  		data.data[l * overallWidth * 3 + c + 2] = 23;
		  	  	  }
		  	  }
		  }
		
		Image img = new Image(null, data);
		container.setImage(img);
	}

	private static final PaletteData pd = new PaletteData(0xff0000, 0x00ff00, 0x0000ff);
}
