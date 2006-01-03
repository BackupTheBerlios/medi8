

package org.medi8.internal.core.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.RowLayout;
import org.medi8.core.file.VideoServer;
import org.medi8.internal.core.ui.widget.gtk.MonitorWidget;
import org.eclipse.swt.widgets.Button;

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

    RowLayout rowLayout = new RowLayout();
    rowLayout.type = org.eclipse.swt.SWT.VERTICAL;
    rowLayout.justify = true;
    rowLayout.pack = true;
    rowLayout.wrap = true;
    rowLayout.fill = true;
    top = new Composite(parent, SWT.NONE);
    top.setLayout(rowLayout);
    createMonitorWidget1();
    createComposite();
    
    videoServer = monitorWidget.getVideoServer();
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
  composite = new Composite(top, SWT.NONE);
  composite.setLayout(new RowLayout());
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
    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
    {
      // FIXME: do we need this button?
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
  monitorWidget = new MonitorWidget(top, SWT.NONE);
}

}
