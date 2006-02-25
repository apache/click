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
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
		// Adds the page mapping to the click.xml
		if(page.shouldAddToClickXML()){
			IStructuredModel model = null;
			
			String newClazz = page.getClassName();
			if(page.getPackageName().length()!=0){
				newClazz = page.getPackageName() + "." + newClazz;
			}
			
			String newPath = page.getFilename();
			String parentFolder = page.getParentFolder();
			String webAppRoot = ClickUtils.getWebAppRootFolder(project);
			if(parentFolder.startsWith(webAppRoot)){
				parentFolder = parentFolder.substring(webAppRoot.length()).replaceAll("^/|/$","");
				newPath = parentFolder + "/" + newPath;
			}
			
			try {
				IFile file = ClickUtils.getClickConfigFile(project);
				IModelManager manager = StructuredModelManager.getModelManager();
				model = manager.getExistingModelForEdit(file);
				
				IDOMDocument doc = ((IDOMModel)model).getDocument();
				
				Element root  = doc.getDocumentElement();
				Element pages = null;
				NodeList list = doc.getElementsByTagName(ClickPlugin.TAG_PAGES);
				
				if(list.getLength()==0){
					pages = doc.createElement(ClickPlugin.TAG_PAGES);
					boolean inserted = false;
					NodeList children = root.getChildNodes();
					for(int i=0;i<children.getLength();i++){
						Node node = children.item(i);
						if(node instanceof Element){
							root.insertBefore(pages, node);
							inserted = true;
							break;
						}
					}
					if(!inserted){
						root.appendChild(pages);
					}
				} else {
					pages = (Element)list.item(0);
				}
				
				NodeList children = pages.getChildNodes();
				boolean found = false;
				for(int i=0;i<children.getLength();i++){
					Node node = children.item(i);
					if(node instanceof Element && ((Element)node).getNodeName().equals(ClickPlugin.TAG_PAGE)){
						Element page = (Element)node;
						String path  = page.getAttribute(ClickPlugin.ATTR_PATH);
						String clazz = page.getAttribute(ClickPlugin.ATTR_CLASSNAME);
						if(newPath.equals(path) && newClazz.equals(clazz)){
							found = true;
							break;
						}
					}
				}
				if(!found){
					Element page = doc.createElement(ClickPlugin.TAG_PAGE);
					page.setAttribute(ClickPlugin.ATTR_PATH, newPath);
					page.setAttribute(ClickPlugin.ATTR_CLASSNAME, newClazz);
					pages.appendChild(page);
				}
			} catch(Exception ex){
				ClickPlugin.log(ex);
				return false;
			} finally {
				if(model!=null){
					model.releaseFromEdit();
				}
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
