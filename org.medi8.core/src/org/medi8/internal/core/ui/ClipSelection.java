/*
 * Created on Oct 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.ui;

import org.eclipse.jface.viewers.ISelection;
import org.medi8.internal.core.model.Clip;
import org.medi8.internal.core.model.VideoTrack;

/**
 * This class represents a selection containing a single clip.
 */
public class ClipSelection implements ISelection {
	private Clip clip;
	private VideoTrack track;

	public ClipSelection (Clip clip, VideoTrack track) {
		this.clip = clip;
		this.track = track;
	}
	
	public Clip getClip () {
		return clip;
	}
	
	public VideoTrack getTrack () {
		return track;
	}
	
	public boolean isEmpty() {
		return false;
	}
}
