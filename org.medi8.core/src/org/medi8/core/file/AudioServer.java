/*
 * Created on Nov 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.core.file;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AudioServer 
{
  Process process = null;
    
  /* This socket we use to talk to the server.  */
  Socket socket;
  /* The input stream we use to read from the sever.  */
  DataInputStream in;
  /* The output stream we use to write to the server.  */
  OutputStream out;

  /* The audio server.  */
  private static AudioServer audio_server = null;
  
  /**
   * Create the audio server.  Much like the Highlander, there can be
   * only one.
   */
  public static AudioServer getAudioServer ()
  {
    if (audio_server == null)
      audio_server = new AudioServer ();
    return audio_server;
  }
  
  /**
   * Stop the audeo server.
   */
  public static void stop ()
  {
    if (audio_server != null)
      audio_server.process.destroy ();
  } 
  
  /**
   * Start the audeo server.
   */
  public static void start ()
  {
    getAudioServer ();
  } 
  
  /**
   * Starts a new m8vplay server process, which is destroyed with the plugin
   * stops.
   */
  private AudioServer ()
    {
    	String server_name = "m8aplay";
    	String workspace = System.getProperty("medi8.workspace");
    	if (workspace != null)
    	  server_name = workspace + "/medi8-tools/audio/m8aplay/m8aplay";
	
    	try
	    	{
    	  	process = Runtime.getRuntime().exec(new String[] {"/bin/sh",
    	  	                                                  "-c",
    	  	                                                  server_name});
	    	} catch (IOException ex) {
	    	  /* TODO this is pretty bogus.  We need a real error handling 
	    	   * infrastructure.
	         	*/
	        	ErrorDialog.openError ((Shell) null,
	        	                       "medi8 error",
	        	                       "Cannot run " + server_name,
	        	                       null);
	    	}

	    	DataInputStream dis = new DataInputStream (process.getErrorStream ());
    }
}

