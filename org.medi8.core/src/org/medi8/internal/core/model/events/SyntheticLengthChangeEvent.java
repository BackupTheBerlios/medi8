/*
 * Created on Sep 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.medi8.internal.core.model.events;

import org.medi8.internal.core.model.Medi8Event;
import org.medi8.internal.core.model.VideoTrack;

/**
 * This is fired when a Track changes length "synthetically".
 */
public class SyntheticLengthChangeEvent extends Medi8Event {

	public SyntheticLengthChangeEvent(VideoTrack source) {
		super(source);
	}

}
