package org.medi8.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;

/**
 * Insert the type's description here.
 * @see IProjectNature
 */
public class Medi8ProjectNature implements IProjectNature {
	/**
	 * The constructor.
	 */
	public Medi8ProjectNature()
	{
	}
	
	public Medi8ProjectNature(IProject project)
	{
		setProject(project);
	}

	/**
	 * Insert the method's description here.
	 * @see IProjectNature#configure
	 */
	public void configure() throws CoreException
	{
	}

	/**
	 * Insert the method's description here.
	 * @see IProjectNature#deconfigure
	 */
	public void deconfigure() throws CoreException
	{
	}

	/**
	 * Insert the method's description here.
	 * @see IProjectNature#getProject
	 */
	public IProject getProject()
	{
		return fProject;
	}

	/**
	 * Insert the method's description here.
	 * @see IProjectNature#setProject
	 */
	public void setProject(IProject project)
	{
		fProject = project;
	}
	
	private IProject fProject;
}
