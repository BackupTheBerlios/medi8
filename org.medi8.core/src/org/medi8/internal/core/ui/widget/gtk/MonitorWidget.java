/*
 * Created on Nov 8, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.ui.widget.gtk;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.medi8.core.file.VideoServer;

/**
 * @author green
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MonitorWidget extends Composite {
    
  VideoServer video_server; 
   
	/**
	 * @param displayArea
	 * @param i
	 */
	
	public MonitorWidget (Composite parent) {
		super (parent, SWT.EMBEDDED);
		
		// Create our video server.
		video_server = new VideoServer (embeddedHandle);
		
		parent.addControlListener (new ControlAdapter () {
		  public void controlResized (ControlEvent event) {
		    // TODO resize our embedded SDL window
		    }
		});
		
		parent.addDisposeListener (new DisposeListener () {
		    public void widgetDisposed (DisposeEvent event) 
		    {
		      // We must stop the video server in order to free up
		      // system resources.
		      video_server.stop ();
		    }
		});
	}
	
	/**
	 * Get the video server.
	 * @return the video server.
	 */
	public VideoServer getVideoServer ()
	{
	    return video_server;
	}
}
