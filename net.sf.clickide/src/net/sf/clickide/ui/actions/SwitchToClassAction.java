package net.sf.clickide.ui.actions;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
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
 */
public class SwitchToClassAction implements IEditorActionDelegate {
	
	private IEditorPart editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

	public void run(IAction action) {
		IEditorInput input = this.editor.getEditorInput();
		if(input instanceof IFileEditorInput){
			IFile file = ((IFileEditorInput)input).getFile();
			String fullpath = file.getProjectRelativePath().toString();
			String root = ClickUtils.getWebAppRootFolder(file.getProject());
			if(fullpath.startsWith(root)){
				String path = fullpath.substring(root.length());
				if(path.startsWith("/")){
					path = path.substring(1);
				}
				String className = ClickUtils.getClassfromHTML(file.getProject(), path);
				if(className!=null){
					try {
						IType type = JavaCore.create(file.getProject()).findType(className);
						JavaUI.revealInEditor(JavaUI.openInEditor(type), (IJavaElement)type);
					} catch(Exception ex){
						ClickPlugin.log(ex);
					}
				}
			}
		}
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
