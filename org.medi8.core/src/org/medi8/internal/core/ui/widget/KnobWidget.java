/* Copyright (C) 2003, 2004  Anthony Green

   This file is is part of medi8.
*/

package org.medi8.internal.core.ui.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class KnobWidget extends Composite
{
  private Label label_;

  public KnobWidget (Composite parent, int style)
  {
    super (parent, style);

    label_ = new Label (this, 0);

    addDisposeListener (new DisposeListener () {
	public void widgetDisposed (DisposeEvent e) {
	  // KnobWidget.this.widgetDisposed (e);
	}
      });

    addControlListener (new ControlAdapter () {
	public void controlResized (ControlEvent e) {
	  KnobWidget.this.controlResized (e);
	}
      });

    label_.addListener (SWT.MouseDown, new Listener () {
	public void handleEvent (Event e) {
	  tracking_ = true;
	  previousPosition_ = e.y;
	}
      });

    label_.addListener (SWT.MouseMove, new Listener () {
	public void handleEvent (Event e) {
	  if (tracking_)
	    {
	      value_ -= (e.y - previousPosition_);
	      previousPosition_ = e.y;
	      if (value_ >= max_)
		value_ = max_ - 1;
	      if (value_ < min_)
		value_ = min_;
	      label_.setImage (getCurrentImage_ ());
	    }
	}
      });

    label_.addListener (SWT.MouseUp, new Listener () {
	public void handleEvent (Event e) {
	  tracking_ = false;
	}
      });
  }

  void controlResized (ControlEvent e)
  {
    resize ();
  }

  void resize ()
  {
    Point point = computeSize (SWT.DEFAULT, SWT.DEFAULT, true);
    label_.setBounds (0, 0, point.x, point.y);
  }

  public void defineKnob (Image[] images, 
			  float min, float max, 
			  float increment, float initial)
  {
    images_ = images;
    min_ = min;
    max_ = max;
    increment_ = increment;
    value_ = initial;
    label_.setImage (getCurrentImage_ ());
  }

  private Image getCurrentImage_ ()
  {
    int index = (int) ((float) images_.length 
		       * ((value_ - min_ + 1) / (max_ - min_ + 1)));
    return images_[index];
  }

  private boolean tracking_ = false;
  private int previousPosition_;

  public Point computeSize (int wHint, int hHint, boolean changed)
  {
    int width = 0, height = 0;

    if (images_ != null)
      {
	Rectangle bounds = images_[0].getBounds();
	width = bounds.width;
	height = bounds.height;
      }
    if (wHint != SWT.DEFAULT) width = wHint;
    if (hHint != SWT.DEFAULT) height = hHint;
    return new Point (width, height);
  }

  private Image[] images_;
  float min_;
  float max_;
  float increment_;
  float value_;
}
