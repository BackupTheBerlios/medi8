/*
 * Created on Nov 7, 2004
 */


package org.medi8.core.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author green
 */
public class VideoServer
{
  Process process = null;

  /* This socket we use to talk to the server. */
  Socket socket;

  /* The Reader we use to read from the sever. */
  BufferedReader in;

  /* The output stream we use to write to the server. */
  OutputStreamWriter out;
  
  // The unit we're using.
  String vtrUnit;

  /**
   * Stop the video server.
   */
  public void stop()
  {
    process.destroy();
  }

  /**
   * Send a command to the video server and return the response.
   * If there is an error, return null.  If there is no response,
   * return the empty string.  Multi-line responses are returned
   * as a single string whose lines are separated by single \n
   * characters.
   * @param cmd the command to send
   * @return the response
   */
  private String sendCommand(String cmd)
  {
    try
    {
      // Send the command.
      out.write(cmd);
      out.write("\r\n");
      out.flush();
      // Read the response.
      String line = in.readLine();
      int value = Integer.parseInt(line.substring(0, 3));
      // This would mean that we were responsible for an error.
      assert value % 100 != 4;
      if (value >= 300)
        return null;
      // 200 - no response.
      if (value == 200)
        return "";
      // 202 - one line response.
      if (value == 202)
        return in.readLine();
      // 201 - multi-line response.
      assert value == 201;
      StringBuffer result = new StringBuffer();
      while (true)
        {
          line = in.readLine();
          if ("".equals(line))
            break;
          result.append(line);
          result.append('\n');
        }
      return result.toString();
    }
    catch (IOException _)
    {
      // Temporary hack.
      _.printStackTrace(System.err);
      // FIXME: what?
      return null;
    }
  }

  /**
   * Starts a new m8vplay server process, which is destroyed when the plugin
   * stops.
   */
  public VideoServer(long parentHandle)
  {
    String server_name = "m8vplay";
    String workspace = System.getProperty("medi8.workspace");
    if (workspace != null)
      server_name = workspace + "/medi8-tools/video/m8vplay/m8vplay";
    String repository = "MLT_REPOSITORY=" + workspace + "/medi8-tools/modules";

    try
      {
        process = Runtime.getRuntime().exec(
                                            new String[] { server_name, "100", "100" },
                                            new String[] {"SDL_VIDEODRIVER=x11",
                                                          "SDL_DEBUG=1",
                                                          "SDL_WINDOWID=" + parentHandle,
                                                          // FIXME
                                                          //"MLT_NORMALISATION=NTSC",
                                                          repository});
      }
    catch (IOException ex)
      {
        /*
         * TODO this is pretty bogus. We need a real error handling
         * infrastructure.
         */
        ErrorDialog.openError((Shell) null, "medi8 error", "Cannot run "
                                                           + server_name, null);
      }

    try
    {
      BufferedReader dis 
        = new BufferedReader(new InputStreamReader (process.getErrorStream(),
                                                    "UTF-8"));
      String line;
      while (true)
        {
          line = dis.readLine();
          if (line != null)
            System.out.println(line);

          if (line != null && line.indexOf("listening on port") != -1)
            {
              // The server has started up enough that we can
              // connect to the server.
              // Set up socket
              socket = new Socket();
              InetSocketAddress address = new InetSocketAddress("localhost",
                                                                5900);
              socket.connect(address);

              in = new BufferedReader(new InputStreamReader (socket.getInputStream(),
                                                             "UTF-8"));
              out = new OutputStreamWriter(socket.getOutputStream());

              // Read the initial '100' response from the server.
              line = in.readLine();

              break;
            }
        }
    }
    catch (IOException ex)
    {
      // FIXME hmm.. what to do with these "impossible" errors... ??
      ex.printStackTrace();
    }

    // Set up a unit for our use.  FIXME: MLT does not respond
    // properly with the unit number, so for the time being we
    // just make an assumption.
    sendCommand("UADD sdl:" + Long.toHexString(parentHandle));
    vtrUnit = "U0";
  }

  public void play()
  {
    sendCommand("PLAY " + vtrUnit);
  }
  
  public void reverse()
  {
    sendCommand("PLAY " + vtrUnit + " -1000");
  }
  
  public void pause()
  {
    sendCommand("PAUSE " + vtrUnit);
  }
  
  public void fastForward()
  {
    sendCommand("FF " + vtrUnit);
  }
  
  public void rewind()
  {
    sendCommand("REW " + vtrUnit);
  }
  
  public void step(int nFrames)
  {
    sendCommand("STEP " + vtrUnit + " " + nFrames);
  }
  
  public void gotoFrame(int frame)
  {
    sendCommand("GOTO " + vtrUnit + " " + frame);
  }
}
