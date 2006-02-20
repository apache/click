package net.sf.clickide.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * The wizard to create new click page.
 * 
 * @author Naoki Takezoe
 */
public class NewClickPageWizard extends Wizard implements INewWizard {
	
	private IStructuredSelection selection;
	private NewClickPageWizardPage page;
	private String initialPageName = null;
	private String initialClassName = null;
	
	public NewClickPageWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle(ClickPlugin.getString("wizard.newPage.title"));
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	
	public void setInitialClassName(String className){
		this.initialClassName =  className;
	}
	
	public void setInitialPageName(String pageName){
		this.initialPageName = pageName;
	}
	
	public void addPages() {
		page = new NewClickPageWizardPage("page1", selection.getFirstElement(), 
				this.initialClassName, this.initialPageName);
		addPage(page);
	}

	public boolean performFinish() {
		IProject project = ClickUtils.getJavaProject(selection.getFirstElement()).getProject();
		
		// Creates the HTML file
		if(page.shouldCreateHTML()){
			try {
				String parentFolder = page.getParentFolder();
				String filename = page.getFilename();
				
				IFile file = null;
				if(parentFolder.equals("")){
					file = project.getFile(filename);
				} else {
					IFolder folder = project.getFolder(parentFolder);
//					if(!folder.exists()){
//						folder.create(true, true, new NullProgressMonitor());
//					}
					file = folder.getFile(filename);
				}
				if(file.exists()){
					return false;
				} else {
					file.create(getPageHTMLInputStream(), true, new NullProgressMonitor());
				}
				
				IDE.openEditor(ClickUtils.getActivePage(), file);
				
			} catch(Exception ex){
				ClickPlugin.log(ex);
				return false;
			}
		}
		// Creates the page class
		if(page.shouldCreateClass()){
			try {
				String sourceFolder = page.getSourceFolder();
				String packageName = page.getPackageName();
				String className = page.getClassName();
				
				IResource resource = project;
				if(!sourceFolder.equals("")){
					resource = project.getFolder(sourceFolder);
				}
				
				IJavaProject javaProject = JavaCore.create(project);
				IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(resource);
				IPackageFragment fragment = root.getPackageFragment(packageName);
				if(!fragment.exists()){
					root.createPackageFragment(packageName, true, new NullProgressMonitor());
					fragment = root.getPackageFragment(packageName);
				}
				ICompilationUnit unit = fragment.getCompilationUnit(className+".java");
				if(unit.exists()){
					return false;
				} else {
					IFile file = (IFile)unit.getResource();
					file.create(getPageClassInputStream(packageName, className), true, 
							new NullProgressMonitor());
					
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
				}
				
			} catch(Exception ex){
				ClickPlugin.log(ex);
				return false;
			}
		}
		return true;
	}
	
	private InputStream getPageHTMLInputStream(){
		return getClass().getResourceAsStream("pagehtml.tmpl");
	}
	
	private InputStream getPageClassInputStream(String packageName, String className) throws IOException {
		
		InputStream in = null;
		byte[] buf = null;
		
		try {
			in  = getClass().getResourceAsStream("pageclass.tmpl");
			buf = new byte[in.available()];
			in.read(buf);
		} finally {
			if(in!=null){
				in.close();
			}
		}
		
		String source = new String(buf, "Windows-31J"); // TODO charset
		source = source.replaceAll("\\$\\{package\\}", packageName);
		source = source.replaceAll("\\$\\{classname\\}", className);
		
		return new ByteArrayInputStream(source.getBytes());
	}
	
}
