/*
 * Created on Nov 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.medi8.internal.core.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
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
  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl (Composite parent)
  { 
    String name = System.getProperty ("medi8.workspace")
    	+ "/org.medi8.core/themes/default/knob48/knob";
    Image knob[] = new Image[128];
    for (int k = 0; k < 128; k++)
    {
      String f = name
        + ((k < 10) ? "0" : "")
        + ((k < 100) ? "0" : "")
        + k + ".jpg";
      knob[k] = new Image (Display.getDefault (), f);
    }
                                                                              
    volumeKnob = new KnobWidget (parent, 0);
    volumeKnob.defineKnob (knob, 0.0f, 100.0f, 1.0f, 50.0f);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  public void setFocus()
  {
    // TODO Auto-generated method stub
  }

}
