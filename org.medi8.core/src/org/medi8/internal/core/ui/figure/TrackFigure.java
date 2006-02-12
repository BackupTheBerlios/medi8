/*
 * Created on Nov 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


package org.medi8.internal.core.ui.figure;

import java.beans.PropertyChangeListener;
import java.util.Iterator;

import org.eclipse.draw2d.AbstractHintLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.medi8.internal.core.Medi8Editor;
import org.medi8.internal.core.model.IChangeListener;
import org.medi8.internal.core.ui.Scale;

/**
 * @author green
 */
public abstract class TrackFigure
  extends Figure
  implements PropertyChangeListener, IChangeListener
{
  /**
   * The scale we are using.
   */
  protected Scale scale;

  /**
   * The sequence figure with which we're associated.
   */
  protected SequenceFigure sequenceFigure;

  /**
   * This handles layout for the children of a VideoTrackFigure.
   */
  protected static class Layout
    extends AbstractHintLayout
  {
    /**
     * Create a new layout.
     */
    public Layout()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.draw2d.AbstractLayout#calculatePreferredSize(org.eclipse.draw2d.IFigure,
     *      int, int)
     */
    protected Dimension calculatePreferredSize(IFigure container, int wHint,
                                               int hHint)
    {
      TrackFigure trackFig = (TrackFigure) container;
      int offset = 0;
      Iterator iter = trackFig.getChildren().iterator();
      while (iter.hasNext())
        {
          IFigure fig = (IFigure) iter.next();
          Dimension dim = fig.getPreferredSize();
          offset += dim.width;
        }

      return new Dimension(offset, Medi8Editor.CLIP_HEIGHT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.draw2d.LayoutManager#layout(org.eclipse.draw2d.IFigure)
     */
    public void layout(IFigure seqArg)
    {
      TrackFigure trackFig = (TrackFigure) seqArg;
      int offset = 0;
      Rectangle bounds = new Rectangle();
      Iterator iter = trackFig.getChildren().iterator();
      while (iter.hasNext())
        {
          IFigure fig = (IFigure) iter.next();
          Dimension dim = fig.getPreferredSize();
          bounds.x = offset;
          bounds.y = 0;
          bounds.width = dim.width;
          bounds.height = Medi8Editor.CLIP_HEIGHT;
          fig.setBounds(bounds);
          // System.err.println(" fig = " + fig + "; bounds = " + bounds);
          offset += dim.width;
        }
    }
  }

  /**
   * 
   */
  public TrackFigure(SequenceFigure sf, Scale scale)
  {
    super();
    this.sequenceFigure = sf;
    this.scale = scale;
  }

  /**
   * Attach a Scale to this object. This is used to control the range and
   * display.
   * 
   * @param range
   */
  public void setScale(Scale scale)
  {
    if (this.scale != null)
      this.scale.removeListener(this);
    this.scale = scale;
    scale.addListener(this);
  }

  /**
   * Called only by SequenceFigure to get the drop target listener.
   */
  TransferDropTargetListener getDropListener(Medi8Editor editor)
  {
    return null;
  }
}
