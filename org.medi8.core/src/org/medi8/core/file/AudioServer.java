/*
 * Created on Nov 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.core.file;

import java.io.DataInputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AudioServer 
{
  Process process = null;

  /* The audio server.  */
  private static AudioServer audio_server = null;

  /* The OSC port.  */
  private static OSCPort outPort = null;
  
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
    if (outPort != null)
      outPort.close ();
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
   * Send a message to the audio server.
   */
  public static void send (String msg, Object arg)
  {
    try {
      Object margs[] = new Object[1];
      margs[0] = arg;
      OSCMessage oscmsg = new OSCMessage (msg, margs);
 	   outPort.send (oscmsg);
 	   } catch (Exception _) {
      // Do nothing.
 	   }
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

	    	DataInputStream dis = new DataInputStream (process.getInputStream());
	    	
	    	String ok = null;
	    	try {
	    	  ok = dis.readLine ();
	    	} catch (IOException x) {
	    	  // FIXME: what to do?
	    	}
	    	if ("OK".equals (ok))
	    	  {
	    	  	try {
	    	  	  // FIXME: make port number customizable.
	    	  	  outPort = new OSCPort (java.net.InetAddress.getLocalHost(), 5333);
	    	  	} catch (Exception _) {
	    	  	  // FIXME: what to do?
	    	  	}
	    	  }
    }
}

