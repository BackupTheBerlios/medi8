/*
 * Created on Nov 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.medi8.core.file.AudioServer;
import org.medi8.internal.core.ui.widget.KnobWidget;

/**
 * @author green
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AudioEffectsView extends ViewPart
{
  private KnobWidget volumeKnob;
  private KnobWidget balanceKnob;
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl (Composite parent)
  { 
    AudioServer audio_server = AudioServer.getAudioServer ();
    
    // Set the background colour to white.
    parent.setBackground (new Color(Display.getDefault (), 255, 255, 255));
    
    // FIXME This part is completely busted right now, but I'll get around
    // to fixing it soon enough.
    
    // Create the layout object.
    FormLayout formLayout = new FormLayout ();
    parent.setLayout (formLayout);
                                               
    String name = System.getProperty ("medi8.workspace")
    	+ "/org.medi8.core/themes/default/knob48/knob";
    Image knob[] = new Image[128];
    for (int k = 0; k < 128; k++)
    {
      String fileName = name
        + ((k < 10) ? "0" : "")
        + ((k < 100) ? "0" : "")
        + k + ".jpg";
      knob[k] = new Image (Display.getDefault (), fileName);
    }
                                  
    volumeKnob = new KnobWidget (parent, 0);
    volumeKnob.defineKnob (knob, 0.0f, 100.0f, 1.0f, 50.0f);

    FormData fb = new FormData ();
    fb.top = new FormAttachment (100, 0);
    fb.right = new FormAttachment (100, 0);
    volumeKnob.setLayoutData (fb);
    
    Image image;
    image = new Image (Display.getDefault (), 
                       System.getProperty ("medi8.workspace") 
                       + "/org.medi8.core/themes/default/knob48/vol.jpg");
    Label vl = new Label (parent, SWT.CENTER);
    vl.setImage (image);
    FormData f = new FormData ();
    f.top = new FormAttachment (volumeKnob, 0);
    f.right = new FormAttachment (100,0);
    vl.setLayoutData (f);
    
    balanceKnob = new KnobWidget (parent, 0);
    balanceKnob.defineKnob (knob, 0.0f, 100.0f, 1.0f, 50.0f);

    fb = new FormData ();
    fb.right = new FormAttachment (volumeKnob, 0);
    fb.top = new FormAttachment (100, 0);
    balanceKnob.setLayoutData (fb);
    
    image = new Image (Display.getDefault (), 
                       System.getProperty ("medi8.workspace") 
                       + "/org.medi8.core/themes/default/knob48/bal.jpg");
    Label l = new Label (parent, SWT.CENTER);
    l.setImage (image);
    f = new FormData ();
    f.top = new FormAttachment (balanceKnob, 0);
    f.right = new FormAttachment (vl, 0);
    l.setLayoutData (f);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  public void setFocus()
  {
    // TODO Auto-generated method stub
  }

}
