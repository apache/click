package net.sf.clickide.ui.actions;

import net.sf.clickide.ClickPlugin;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

public class SwitchToHTMLAction implements IEditorActionDelegate {
	
	private IEditorPart editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

	public void run(IAction action) {
		IEditorInput input = this.editor.getEditorInput();
		IJavaElement element = 
			JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(input);
		ICompilationUnit unit = (ICompilationUnit)element;
		try {
			IType type = unit.getAllTypes()[0];
			System.out.println(type.getFullyQualifiedName());
		} catch(Exception ex){
			ClickPlugin.log(ex);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
