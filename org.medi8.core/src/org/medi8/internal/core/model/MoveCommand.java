/*
 * Created on Apr 22, 2003
 */
package org.medi8.internal.core.model;

import org.eclipse.gef.commands.Command;

/**
 * This is a command which moves a clip relative to
 * the track it is in.
 */
public class MoveCommand extends Command
{
	/**
	 * Create a new command to move a clip.
	 * @param label Name of this command
	 * @param track Track to modify
	 * @param clip The child clip to move
	 * @param delta A relative amount to move the clip
	 */
	public MoveCommand(String label, VideoTrack track, Clip clip, Time delta)
	{
		super(label);
		this.track = track;
		this.clip = clip;
		this.delta = delta;
	}

	public void undo()
	{
		// FIXME: handle error return here
		track.move(clip, delta);
		delta = new Time(- delta.toDouble());
	}

	public void execute()
	{
		// FIXME: handle error return here
		track.move(clip, delta);
		delta = new Time(- delta.toDouble());
	}

	private VideoTrack track;
	private Time delta;
	private Clip clip;
}
