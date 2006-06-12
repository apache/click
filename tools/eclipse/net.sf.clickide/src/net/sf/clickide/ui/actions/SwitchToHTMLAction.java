package net.sf.clickide.ui.actions;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
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
 */
public class SwitchToHTMLAction implements IEditorActionDelegate {
	
	private IEditorPart editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

	public void run(IAction action) {
		IEditorInput input = this.editor.getEditorInput();
		if(input instanceof IFileEditorInput){
			IFile file = ((IFileEditorInput)input).getFile();
			IJavaElement element = 
				JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(input);
			ICompilationUnit unit = (ICompilationUnit)element;
			try {
				IType type = unit.getAllTypes()[0];
				String html = ClickUtils.getHTMLfromClass(file.getProject(), type.getFullyQualifiedName());
				if(html!=null){
					String root = ClickUtils.getWebAppRootFolder(file.getProject());
					IFolder folder = file.getProject().getFolder(root);
					IResource resource = folder.findMember(html);
					if(resource!=null && resource instanceof IFile && resource.exists()){
						IDE.openEditor(ClickUtils.getActivePage(), (IFile)resource);
					}
				}
			} catch(Exception ex){
				ClickPlugin.log(ex);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
