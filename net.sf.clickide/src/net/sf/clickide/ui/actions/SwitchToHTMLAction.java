package net.sf.clickide.ui.actions;

import java.text.MessageFormat;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ide.IDE;

/**
 * Switches to the HTML file from the page class.
 * 
 * @author Naoki Takezoe
 * @see ClickUtils#getTemplateFromPageClass(IType)
 */
public class SwitchToHTMLAction implements IEditorActionDelegate {
	
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
			
			IJavaElement element = 
				JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(input);
			ICompilationUnit unit = (ICompilationUnit)element;
			try {
				IType type = unit.getAllTypes()[0];
				IFile resource = ClickUtils.getTemplateFromPageClass(type);
				if(resource!=null){
					IDE.openEditor(ClickUtils.getActivePage(), (IFile)resource);
					return;
				}
			} catch(Exception ex){
				ClickPlugin.log(ex);
			}
			ClickUtils.openErrorDialog(
					ClickPlugin.getString("message.error.noHtml"));
		}
	}

}
