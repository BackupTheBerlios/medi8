/*
 * Created on Apr 8, 2003
 */
package org.medi8.internal.core.model;

import java.util.EventListener;

/**
 * This interface represents listeners that listen for changes to
 * medi8 sequences.
 */
public interface IChangeListener extends EventListener
{
	// FIXME: this is really bogus!
	public void notify(Medi8Event event);
}
