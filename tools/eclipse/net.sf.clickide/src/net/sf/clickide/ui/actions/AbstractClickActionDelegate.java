package net.sf.clickide.ui.actions;

import net.sf.clickide.ClickUtils;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;

public abstract class AbstractClickActionDelegate implements IActionDelegate {

	protected IStructuredSelection selection;

	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection){
			this.selection = (IStructuredSelection)selection;
			Object obj = this.selection.getFirstElement();
			if(obj != null){
				IJavaProject project = ClickUtils.getJavaProject(obj);
				if(project != null){
					action.setEnabled(ClickUtils.isClickProject(project.getProject()));
					return;
				}
			}
		}
		action.setEnabled(false);
	}

}
