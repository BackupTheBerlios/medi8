/*
 * Created on Nov 7, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.model;

import org.medi8.core.file.VideoServer;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Monitor {
    
    /**
     * The video server.
     */
    private VideoServer video_server;
    
    /**
     * The MonitorWidget is responsible for creating the video server.
     * There are two reasons for this: only the widget has the window handle
     * that the server needs, and the widget gets reliable dispose events,
     * enabling it to kill the server process when we're all done.
     */ 
    public Monitor (VideoServer video_server)
    {
        this.video_server = video_server;
    }
}
