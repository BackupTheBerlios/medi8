/*
 * Created on Nov 8, 2003
 */
package org.medi8.core.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 */
public class Medi8ProjectWizard extends BasicNewProjectResourceWizard
{
  /**
   * 
   */
  public Medi8ProjectWizard()
  {
    super();
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.wizard.IWizard#performFinish()
   */
  public boolean performFinish()
  {
    boolean r = super.performFinish();
    if (! r)
		return false;
   	IProject project = getNewProject();
	if (project != null)
	{
		try {
			 IProjectDescription description = project.getDescription();
			 String[] natures = description.getNatureIds();
			 String[] newNatures = new String[natures.length + 1];
			 System.arraycopy(natures, 0, newNatures, 0, natures.length);
			 newNatures[natures.length] = "org.medi8.core.Medi8ProjectNature";
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			 IStatus status = workspace.validateNatureSet(natures);

			 // check the status and decide what to do
			 if (status.getCode() == IStatus.OK) {
				 description.setNatureIds(newNatures);
				 project.setDescription(description, null);
			 } else {
				 // FIXME: raise a user error
			 }
		} catch (CoreException e) {
			 // Something went wrong
		}
	}
	return true;
  }

}
