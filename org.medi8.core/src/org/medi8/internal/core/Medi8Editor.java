/*
 * Created on Apr 3, 2003
 */
package org.medi8.internal.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.SaveAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;
import org.medi8.core.file.XMLGeneratingVisitor;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.InsertOrDeleteCommand;
import org.medi8.internal.core.model.Sequence;
import org.medi8.internal.core.model.Time;
import org.medi8.internal.core.model.VideoTrack;
import org.medi8.internal.core.ui.ClipSelection;
import org.medi8.internal.core.ui.MouseHandler;
import org.medi8.internal.core.ui.Scale;
import org.medi8.internal.core.ui.figure.SequenceFigure;

/**
 */
public class Medi8Editor extends EditorPart
  implements CommandStackListener, ISelectionProvider
{
	/**
	 * Height of a clip, in pixels.
	 */
	public static final int CLIP_HEIGHT = 48;

	/**
	 * Padding above and below the clips.
	 */
	public static final int VERTICAL_PADDING = 30;
	
	/**
	 * Padding to the left of a clip.
	 */
	public static final int HORIZONTAL_PADDING = 30; 

	public static final int VERTICAL_GAP = 6;

	public static final org.eclipse.swt.graphics.Color bgColor = ColorConstants.listBackground;

	/**
	 * Create a new clip editor.
	 */
	public Medi8Editor()
	{
		commandStack = new CommandStack();
		commandStack.addCommandStackListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(out);
			XMLGeneratingVisitor v = new XMLGeneratingVisitor (ps);
			sequence.visit(v);
			ps.flush();
			IFile file = ((IFileEditorInput)getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), 
							true, false, monitor);
			ps.close();
			commandStack.markSaveLocation();
			savePreviouslyNeeded = false;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
	 */
	public void gotoMarker(IMarker marker)
	{
		try
		{
			marker.getAttribute(IMarker.LOCATION);
			// FIXME: scroll there.
		}
		catch (CoreException ignore)
		{
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input)
		throws PartInitException
	{
		setSite(site);
		setInput(input);

		createActions(site);
		updateActions(propertyActions);
		updateActions(stackActions);

		IFile file = ((IFileEditorInput) input).getFile();
		try
		{
			InputStream is = file.getContents(false);
			setPartName(file.getName());
			// FIXME: load the file here.
			// FIXME: add a resource change listener for this file
			is.close(); 
		}
		catch (Exception e)
		{
			//This is just an example.  All exceptions caught here.
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty()
	{
		return commandStack.isDirty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		return false;  // FIXME: will allow save-as later
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		canvas = new Canvas(parent, 0);
		LightweightSystem lws = new LightweightSystem(canvas);
		lws.setContents(createFigure());
		
		FormData data = new FormData();
		data.top = new FormAttachment(0, VERTICAL_PADDING);
		data.bottom = new FormAttachment(100, -VERTICAL_PADDING);
		data.left = new FormAttachment(0, HORIZONTAL_PADDING);
		data.right = new FormAttachment(100, -HORIZONTAL_PADDING);
		canvas.setLayoutData(data);
		parent.setLayout(new FormLayout());
		
		canvas.setBackground(bgColor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		canvas.setFocus();
	}

	private IFigure createFigure()
	{
		Layer base = new Layer();

		// This is 45 minutes, in frames.
		Time end = new Time(45 * 60 * 24);  // FIXME: knows about fps.

		ScrollPane pane = new ScrollPane();
		pane.setHorizontalScrollBarVisibility(ScrollPane.ALWAYS);
		pane.setVerticalScrollBarVisibility(ScrollPane.AUTOMATIC);

		Scale scaler = new Scale();

		// Make some initial tracks.
		sequence = new Sequence();
		VideoTrack[] tracks = new VideoTrack[5];
		for (int i = 0; i < tracks.length; ++i)
		{
			tracks[i] = new VideoTrack();
			sequence.addTrack(tracks[i]);
		}
		
		sequenceFigure = new SequenceFigure(this, sequence, scaler);
		sequenceFigure.setBounds(
			new Rectangle(0, 0, 20, CLIP_HEIGHT));
		base.add(sequenceFigure);
		base.setLayoutManager(new FlowLayout());
		
		MouseHandler handler = new MouseHandler (sequenceFigure);
		sequenceFigure.addMouseListener(handler);
		sequenceFigure.addMouseMotionListener(handler);
		
		LayeredPane top = new LayeredPane();
		top.setLayoutManager(new FlowLayout());
		top.add(base, "base");

		pane.setSize(new Dimension(500, CLIP_HEIGHT)); // FIXME: arbitrary width
		pane.setContents(top);
		return pane;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
	 */
	public void commandStackChanged(EventObject event)
	{
		// If the save state changed, then we need to note that and
		// fire a change event.
		if (savePreviouslyNeeded != isDirty())
		{
			savePreviouslyNeeded = isDirty();
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
		updateActions(stackActions);
	}

	public Object getAdapter(Class type)
	{
		if (type == CommandStack.class)
			return commandStack;
		if (type == ActionRegistry.class)
			return registry;
		return super.getAdapter(type);
	}
	
	public void executeCommand(Command cmd)
	{
		commandStack.execute(cmd);
	}

	private void createActions(IEditorSite site) 
	{
		site.setSelectionProvider(this);

		registry = new ActionRegistry();
		IAction action;
	
		action = new UndoAction(this);
		registry.registerAction(action);
		stackActions.add(action.getId());
		site.getActionBars().setGlobalActionHandler(ActionFactory.UNDO.getId(), action);
	
		action = new RedoAction(this);
		registry.registerAction(action);
		stackActions.add(action.getId());
		site.getActionBars().setGlobalActionHandler(ActionFactory.REDO.getId(), action);
	
		// FIXME: we probably shouldn't bother using a DeleteAction here.
		// we should just write our own actions.
		action = new DeleteAction(this) {
			protected boolean calculateEnabled () {
				return ! getSelection ().isEmpty();
			}
			
			public void run () {
				ISelection sel = getSelection();
				if (! (sel instanceof ClipSelection))
					return;
				ClipSelection cs = (ClipSelection) sel;
				Clip c = cs.getClip();
				VideoTrack track = cs.getTrack();
				executeCommand(new InsertOrDeleteCommand("deletion", track, c));
				sequenceFigure.clearSelection();
			}
		};
		registry.registerAction(action);
		selectionActions.add(action.getId());
		site.getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), action);
	
		action = new SaveAction(this);
		registry.registerAction(action);
		propertyActions.add(action.getId());
		site.getActionBars().setGlobalActionHandler(ActionFactory.SAVE.getId(), action);
		
		// FIXME: implement run().
		//action = new RetargetAction(IWorkbenchActionConstants.BOOKMARK, "Add Bookmark...");
		//registry.registerAction(action);
		// FIXME: add to one of the update lists.
	}

	private void updateActions(List actionIds)
	{
		Iterator iter = actionIds.iterator();
		while (iter.hasNext())
		{
			IAction action = registry.getAction((String)iter.next());
			if (action instanceof UpdateAction)
				((UpdateAction)action).update();
		}
	}
	
	public Canvas getCanvas ()
	{
		return canvas;
	}

	protected void firePropertyChange(int property) 
	{
		super.firePropertyChange(property);
		updateActions(propertyActions);
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}
	
	public ISelection getSelection() {
		return currentSelection;
	}
	
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}
	
	public void setSelection(ISelection selection) {
		currentSelection = selection;
		SelectionChangedEvent ev = new SelectionChangedEvent(this, selection);
		Iterator it = selectionListeners.iterator();
		while (it.hasNext())
		{
			ISelectionChangedListener l = (ISelectionChangedListener) it.next();
			l.selectionChanged(ev);
		}
		updateActions(selectionActions);
	}

	/** The sequence we're editing.  */
	private Sequence sequence;
	
	/** The canvas we're displaying on.  */
	private Canvas canvas;

	private CommandStack commandStack;
	private boolean savePreviouslyNeeded;
	private ActionRegistry registry;
	private ArrayList stackActions = new ArrayList();
	private ArrayList selectionActions = new ArrayList();
	private ArrayList propertyActions = new ArrayList();
	
	/** Listeners. */
	private HashSet selectionListeners = new HashSet();
	/** Selection.  */
	private ISelection currentSelection;

	/** Figure representing the sequence we display. */
	SequenceFigure sequenceFigure;
}
