package net.sf.clickide.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.preferences.Template;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
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
	
	// keys of dialog settings
	public static final String SECTION_NEW_CLICK_PAGE = "NewClickPageWizard";
	public static final String SHOULD_CREATE_HTML = "shouldCreateHTML";
	public static final String SHOULD_CREATE_CLASS = "shouldCreateClass";
	public static final String SHOULD_ADD_TO_CLICK_XML = "shouldAddToClickXML";
	public static final String SUPERCLASS = "superclass";
	
	private IStructuredSelection selection;
	private NewClickPageWizardPage page;
	private String initialPageName = null;
	private String initialClassName = null;
	private IFile[] openFiles = null;
	
	public NewClickPageWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle(ClickPlugin.getString("wizard.newPage.title"));
		
		IDialogSettings settings = ClickPlugin.getDefault().getDialogSettings();
		if(settings.getSection(SECTION_NEW_CLICK_PAGE)==null){
			IDialogSettings section = settings.addNewSection(SECTION_NEW_CLICK_PAGE);
			section.put(SHOULD_CREATE_HTML, true);
			section.put(SHOULD_CREATE_CLASS, true);
			section.put(SHOULD_ADD_TO_CLICK_XML, true);
			section.put(SUPERCLASS, "net.sf.click.Page");
		}
		setDialogSettings(settings.getSection(SECTION_NEW_CLICK_PAGE));
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
		Object element = null;
		if(selection != null){
			element = selection.getFirstElement();
		}
		page = new NewClickPageWizardPage("page1", element, 
				this.initialClassName, this.initialPageName);
		addPage(page);
	}
	
	public boolean performFinish(){
		
		final boolean shouldCreateHTML = page.shouldCreateHTML();
		final boolean shouldCreateClass = page.shouldCreateClass();
		final boolean shouldAddToClickXML = page.shouldAddToClickXML();
		final String parentFolder = page.getParentFolder();
		final String filename = page.getFilename();
		final String sourceFolder = page.getSourceFolder();
		final String packageName = page.getPackageName();
		final String className = page.getClassName();
		final String superClass = page.getSuperClass();
		final Template template = page.getTemplate();
		final IProject project = page.getProject();
		
		IDialogSettings settings = getDialogSettings();
		settings.put(SHOULD_CREATE_HTML, shouldCreateHTML);
		settings.put(SHOULD_CREATE_CLASS, shouldCreateClass);
		settings.put(SHOULD_ADD_TO_CLICK_XML, shouldAddToClickXML);
		settings.put(SUPERCLASS, superClass);
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					openFiles = doFinish(monitor, project, shouldCreateHTML, shouldCreateClass, shouldAddToClickXML,
							parentFolder, filename, sourceFolder, packageName, className, superClass, template);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
			for(int i=0;i<openFiles.length;i++){
				IDE.openEditor(ClickUtils.getActivePage(), openFiles[i]);
			}
		} catch (PartInitException e){
			// TODO display the error message?
			ClickPlugin.log(e);
		} catch (InterruptedException e) {
			// TODO display the error message?
			ClickPlugin.log(e);
			return false;
		} catch (InvocationTargetException e) {
			// TODO display the error message?
			Throwable realException = e.getTargetException();
			ClickPlugin.log(realException);
			return false;
		}
		return true;
	}
	
	private IFile[] doFinish(IProgressMonitor monitor, IProject project,
			boolean shouldCreateHTML,boolean shouldCreateClass, boolean shouldAddToClickXML,
			String parentFolder, String filename, String sourceFolder, String packageName,
			String className, String superClass, Template template) throws Exception {
		
		List files = new ArrayList();
		int totalTask = 0;
		if(shouldCreateHTML){
			totalTask++;
		}
		if(shouldCreateClass){
			totalTask++;
		}
		if(shouldAddToClickXML){
			totalTask++;
		}
		monitor.beginTask(ClickPlugin.getString("wizard.newPage.progress"), totalTask);
		
		// Creates the HTML file
		if(shouldCreateHTML){
			try {
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
				if(!file.exists()){
					file.create(new ByteArrayInputStream(template.getHtmlTemplate().getBytes()), 
							true, new NullProgressMonitor());
				}
				files.add(file);
			} finally {
				monitor.worked(1);
			}
		}
		
		// Creates the page class
		if(shouldCreateClass){
			try {
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
				if(!unit.exists()){
					IFile file = (IFile)unit.getResource();
					file.create(getPageClassInputStream(template, packageName, className, superClass),
							true, new NullProgressMonitor());
					files.add(file);
				}
			} finally {
				monitor.worked(1);
			}
		}
		
		// Adds the page mapping to the click.xml
		if(shouldAddToClickXML){
			IStructuredModel model = null;
			
			String newPath = filename;
			String webAppRoot = ClickUtils.getWebAppRootFolder(project);
			if(parentFolder.startsWith(webAppRoot)){
				parentFolder = parentFolder.substring(webAppRoot.length()).replaceAll("^/|/$","");
				if(parentFolder.length() > 0){
					newPath = parentFolder + "/" + filename;
				}
			}
			
			try {
				IFile file = ClickUtils.getClickConfigFile(project);
				IModelManager manager = StructuredModelManager.getModelManager();
				model = manager.getModelForEdit(file);
				
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
				
				String newClazz = className;
				String pagesPackage = pages.getAttribute(ClickPlugin.ATTR_PACKAGE);
				if(packageName.length()!=0){
					if(pagesPackage==null || !packageName.equals(pagesPackage)){
						newClazz = packageName + "." + className;
					}
				}
				
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
				model.save();
			} finally {
				if(model!=null){
					model.releaseFromEdit();
				}
				monitor.worked(1);
			}
		}
		
		return (IFile[])files.toArray(new IFile[files.size()]);
	}
	
	private InputStream getPageClassInputStream(Template template,
			String packageName, String className, String superClass) throws IOException {
		
		String source = new String(template.getPageClass());
		source = source.replaceAll("\\$\\{package\\}", packageName);
		source = source.replaceAll("\\$\\{classname\\}", className);
		source = source.replaceAll("\\$\\{superclass\\}", superClass);
		
		return new ByteArrayInputStream(source.getBytes());
	}
	
}
