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
	    String server_name = "m8vplay";
	    String workspace = System.getProperty("medi8.workspace");
	    if (workspace != null)
	        server_name = workspace + "/medi8-tools/video/m8vplay/m8vplay";
	    
	    try
	    {
	        process = Runtime.getRuntime().exec(new String[] {
	                    "/bin/sh",
	                    "-c",
	                    "SDL_VIDEODRIVER=x11 SDL_DEBUG=1 SDL_WINDOWID=0x" + Long.toHexString(parentHandle) + " MLT_NORMALIZE=NTSC " + server_name + " 100 100"});
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
              if (line != null)
                System.out.println (line);
             
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
