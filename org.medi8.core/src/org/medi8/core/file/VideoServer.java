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
import java.net.InetSocketAddress;
import java.net.Socket;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VideoServer 
{
    /* FIXME what's a good way to identify the location of the server
     * process??  Hardcode for now - but this sucks.
     */
  private static final String server_name
    = "/home/green/medi8/workspace/medi8-tools/video/m8vplay/m8vplay";
  
  Process process = null;
    
  /* This socket we use to talk to the server.  */
  Socket socket;
  /* The input stream we use to read from the sever.  */
  DataInputStream in;
  /* The output stream we use to write to the server.  */
  OutputStream out;

  /**
   * Stop the video server.
   */
  public void stop ()
  {
      process.destroy ();
  } 
  
  /**
   * Starts a new m8vplay server process, which is destroyed with the plugin
   * stops.
   */
	public VideoServer (long parentHandle)
	{
	    try
	    {
	        process = Runtime.getRuntime().exec(new String[] {
	                    server_name, "5900" },
	                    new String[] {
	                		"SDL_WINDOWID=0x" + Long.toHexString(parentHandle),
	                		"MLT_NORMALIZE=NTSC"});
	    } catch (IOException ex) {
	        /* TODO this is pretty bogus.  We need a real error handling 
	         * infrastructure.
	         */
	        ErrorDialog.openError (
	                (Shell) null,
	                "medi8 error",
	                "Cannot run " + server_name,
	                null);
	    }
	    
      DataInputStream dis = new DataInputStream (process.getErrorStream ());

      try {
          String line;
          while (true)
          {
              line = dis.readLine ();
             
              if (line != null
                  && line.indexOf ("listening on port") != -1)
              {
                  // The server has started up enough that we can
                  // connect to the server.
                  // Set up socket
                  socket = new Socket ();
                  InetSocketAddress address 
                  	= new InetSocketAddress ("localhost", 5900);
                  socket.connect (address);
                  
                  in = new DataInputStream (socket.getInputStream ());
                  out = socket.getOutputStream ();
                  break;
              }
          }
      } catch (IOException ex) {
          // FIXME hmm.. what to do with these "impossible" errors... ??
          ex.printStackTrace ();
      }
	}
}
