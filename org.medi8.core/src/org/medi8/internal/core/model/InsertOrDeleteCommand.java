/*
 * Created on Apr 2, 2003
 */
package org.medi8.internal.core.model;

import org.eclipse.gef.commands.Command;

/**
 * This class causes a Clip to be inserted into a Track at
 * an indicated Time.
 */
public class InsertOrDeleteCommand extends Command
{
	/**
	 * Indicates that the primary function is insertion.
	 */
	public static final int INSERT = 0;
	
	/**
	 * Indicates that the primary function is deletion.
	 */
	public static final int DELETE = 1;

	/**
	 * This constructor is used when inserting into a track.
	 * @param label Label for the command
	 * @param parent Track into which insertion is done
	 * @param clip Clip to insert
	 * @param start Time at which to insert clip
	 */
	public InsertOrDeleteCommand(String label, VideoTrack parent,
			Clip clip, Time start)
	{
		super(label);
		track = parent;
		startTime = start;
		child = clip;
		what = INSERT;
	}

	/**
	 * This constructor is used when deleting from a track.
	 * @param label Label for the command
	 * @param parent Track from which child is removed
	 * @param clip Clip to remove
	 */
	public InsertOrDeleteCommand(String label, VideoTrack parent, Clip clip)
	{
		// Note that a given Clip cannot appear more than once, anywhere.
		// We make a new Clip each time we need one.
		super(label);
		track = parent;
		child = clip;
		startTime = parent.findClipTime(clip);
		// This exception makes debugging simpler.
		if (startTime == null)
			throw new IllegalArgumentException("clip not found in parent track");
		what = DELETE;
	}

	private void delete()
	{
		track.deleteClip(child);
	}

	private void insert()
	{
		// FIXME: handle return result here
		track.addClip(startTime, child);
	}

	public void undo()
	{
		if (what == INSERT)
			delete();
		else
			insert();
	}

	public void execute()
	{
		if (what == INSERT)
			insert();
		else
			delete();
	}

	/**
	 * Track we are modifying.
	 */
	private final VideoTrack track;
	
	/**
	 * Where the insertion or deletion starts.
	 */
	private final Time startTime;
	
	/**
	 * The clip to insert or delete.
	 */
	private Clip child;
	
	/**
	 * What to do.
	 */
	private final int what;
}

