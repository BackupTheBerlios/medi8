/*
 * Created on Aug 11, 2004
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
 * It uses OpenVIP to find information about the file.
 * The idea is that we should minimize our explicit OpenVIP
 * dependencies, so most interaction with the library is
 * modularized.
 */
public class OpenVIPClipFactory
{
	private OpenVIPClipFactory()
	{
	}
	
	public static Clip createClip(File file)
	{
		try
		{
			Process p = Runtime.getRuntime().exec(new String[] {
					openVIPBaseDir + "getinfo",
					file.toString()
			});
			Properties prop = new Properties();
			prop.load(p.getInputStream());
			if (p.waitFor() != 0)
				return null;
			String segment = prop.getProperty("segment");
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
		Process p = null;
		try
		{
			String[] args = new String[] {
					openVIPBaseDir + "thumb",
					"" + overallWidth,
					"" + width,
					"" + height,
					file
			};
			p = Runtime.getRuntime().exec(args);
			DataInputStream is = new DataInputStream (p.getInputStream());
			is.readFully(data.data);
		}
		catch (IOException _)
		{
			if (p != null)
				p.destroy();
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
		Image img = new Image(null, data);
		container.setImage(img);
	}
	
	private static final String openVIPBaseDir = System.getProperty("medi8.home");
	private static final PaletteData pd = new PaletteData(0xff0000, 0x00ff00, 0x0000ff);
}
