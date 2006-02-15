

package org.medi8.internal.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.medi8.core.file.VideoServer;
import org.medi8.internal.core.ui.widget.gtk.MonitorWidget;

public class MonitorView
  extends ViewPart
{

  public static final String ID = "org.medi8.internal.core.ui.MonitorView"; // TODO Needs to be whatever is mentioned in plugin.xml

  private Composite top = null;

private Composite composite = null;

private MonitorWidget monitorWidget = null;

private Button button = null;

private Button button1 = null;

private Button button2 = null;

private Button button3 = null;

private Button button4 = null;

private VideoServer videoServer;

public MonitorView()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  public void createPartControl(Composite parent)
  {
    // TODO Auto-generated method stub

    top = new Composite(parent, SWT.NONE);
    createMonitorWidget1();
    createComposite();
    
    videoServer = monitorWidget.getVideoServer();
			top.setLayout(new GridLayout());
  }

  public void setFocus()
  {
    // TODO Auto-generated method stub

  }

/**
 * This method initializes composite	
 *
 */
private void createComposite()
{
  GridData gridData = new GridData();
  gridData.grabExcessHorizontalSpace = true;
  gridData.verticalAlignment = org.eclipse.swt.layout.GridData.END;
  composite = new Composite(top, SWT.NONE);
  composite.setLayout(new RowLayout());
  composite.setLayoutData(gridData);
  button = new Button(composite, SWT.NONE);
  button.setText("<<");
  button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
  {
    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
    {
      videoServer.rewind();
    }
  });
  button1 = new Button(composite, SWT.NONE);
  button1.setText("<");
  button1.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
  {   
  	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {    
  		videoServer.reverse();
  	}
  
  });
  button2 = new Button(composite, SWT.NONE);
  button2.setText("||");
  button2.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
  {
    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
    {
      videoServer.pause();
    }
  });
  button3 = new Button(composite, SWT.NONE);
  button3.setText(">");
  button3.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
  {
    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
    {
      videoServer.play();
    }
  });
  button4 = new Button(composite, SWT.NONE);
  button4.setText(">>");
  button4.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
  {
    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
    {
      videoServer.fastForward();
    }
  });
}

/**
 * This method initializes monitorWidget1	
 *
 */
private void createMonitorWidget1()
{
  GridData gridData1 = new org.eclipse.swt.layout.GridData();
  gridData1.grabExcessHorizontalSpace = true;
  gridData1.grabExcessVerticalSpace = true;
  monitorWidget = new MonitorWidget(top, SWT.NONE);
  monitorWidget.setLayoutData(gridData1);
}

}
