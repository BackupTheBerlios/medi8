/*
 * Created on Apr 8, 2003
 */
package org.medi8.internal.core.model;

import java.util.EventObject;

/**
 * Base class for all medi8-related events.
 */
public class Medi8Event extends EventObject
{
	/**
	 * Create a new medi8 event object.
	 * @param source
	 */
	protected Medi8Event(Object source)
	{
		super(source);
	}
}
