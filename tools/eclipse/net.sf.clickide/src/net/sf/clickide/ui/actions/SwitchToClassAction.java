package net.sf.clickide.ui.actions;

import java.text.MessageFormat;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * Switches to the page class from the HTML file.
 * 
 * @author Naoki Takezoe
 * @see ClickUtils#getPageClassFromTemplate(IFile)
 */
public class SwitchToClassAction implements IEditorActionDelegate {
	
	private IEditorPart editor;
	
	public void selectionChanged(IAction action, ISelection selection) {
		// Nothing to do
	}
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

	public void run(IAction action) {
		IEditorInput input = this.editor.getEditorInput();
		if(input instanceof IFileEditorInput){
			IFile file = ((IFileEditorInput)input).getFile();
			
			if(!ClickUtils.isClickProject(file.getProject())){
				ClickUtils.openErrorDialog(MessageFormat.format(
					ClickPlugin.getString("wizard.newPage.error.notClickProject"), 
					new String[]{ file.getProject().getName() }));
				return;
			}
			
			try {
				IType type = ClickUtils.getPageClassFromTemplate(file);
				if(type!=null){
					JavaUI.openInEditor(type);
					return;
				}
			} catch(Exception ex){
				ClickPlugin.log(ex);
			}
			ClickUtils.openErrorDialog(
					ClickPlugin.getString("message.error.noPage"));
		}
	}

}
