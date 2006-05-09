package net.sf.clickide.ui.wizard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.preferences.Template;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.search.JavaSearchScope;
import org.eclipse.jdt.internal.ui.dialogs.PackageSelectionDialog;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * 
 * 
 * @author Naoki Takezoe
 */
public class NewClickPageWizardPage extends WizardPage {
	
	private List templates = Template.loadFromPreference();
	private Combo template;
	private Text project;
	private Button browseProject;
	private Button createPageHTML;
	private Text parentFolder;
	private Button browseParent;
	private Text pageName;
	private Button createPageClass;
	private Text sourceFolder;
	private Text packageName;
	private Text superClass;
	private Text className;
	private Button browseSource;
	private Button browsePackage;
	private Button browseSuperClass;
	private Button addToClickXML;
	
	private Object selection;
	private String initialClassName;
	private String initialPageName;
	
	public NewClickPageWizardPage(String pageName, Object selection, 
			String initialClassName, String initialPageName) {
		super(pageName);
		
		this.selection = selection;
		this.initialClassName = initialClassName;
		this.initialPageName = initialPageName;
		
		setTitle(ClickPlugin.getString("wizard.newPage.title"));
		setDescription(ClickPlugin.getString("wizard.newPage.description"));
	}
	
	public void createControl(Composite parent) {
		IDialogSettings settings = 
			ClickPlugin.getDefault().getDialogSettings().getSection(
					NewClickPageWizard.SECTION_NEW_CLICK_PAGE);
		
		String initClassName = this.initialClassName;
		String initPackage = "";
		if(this.initialClassName != null){
			int index = this.initialClassName.lastIndexOf('.');
			if(index >= 0){
				initPackage   = this.initialClassName.substring(0, index);
				initClassName = this.initialClassName.substring(index + 1);
			}
		}
		
		Composite composite = new Composite(parent, SWT.NULL);
		
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, false));
		
		Composite projectPanel = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		projectPanel.setLayout(layout);
		projectPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ClickUtils.createLabel(projectPanel, ClickPlugin.getString("wizard.newPage.project"));
		
		project = new Text(projectPanel, SWT.BORDER);
		project.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if(selection!=null){
			IJavaProject initProject = ClickUtils.getJavaProject(selection);
			try {
				if(initProject!=null && initProject.getProject().hasNature(JavaCore.NATURE_ID)){
					project.setText(initProject.getElementName());
				}
			} catch(Exception ex){}
		}
		project.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				validate();
			}
		});
		browseProject = new Button(projectPanel, SWT.PUSH);
		browseProject.setText(ClickPlugin.getString("action.browse"));
		browseProject.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				selectProject();
				validate();
			}
		});
		ClickUtils.createLabel(projectPanel, ClickPlugin.getString("preferences.template") + ":");
		template = new Combo(projectPanel, SWT.READ_ONLY);
		for(int i=0;i<templates.size();i++){
			template.add(((Template)templates.get(i)).getName());
			if(i==0){
				template.setText(((Template)templates.get(i)).getName());
			}
		}
		
		Group htmlGroup = new Group(composite, SWT.NULL);
		htmlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		htmlGroup.setLayout(new GridLayout(3, false));
		htmlGroup.setText(ClickPlugin.getString("wizard.newPage.templateGroup"));
		
		createPageHTML = new Button(htmlGroup, SWT.CHECK);
		createPageHTML.setText(ClickPlugin.getString("wizard.newPage.templateGroup.checkbox"));
		createPageHTML.setLayoutData(createGridData(3));
		createPageHTML.setSelection(settings.getBoolean(NewClickPageWizard.SHOULD_CREATE_HTML));
		createPageHTML.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				updateHTMLGroup();
				validate();
			}
		});
		
		String initFolder = "";
		String initPageName = this.initialPageName;
		if(initPageName!=null){
			int index = initPageName.indexOf('/');
			if(index >= 0){
				initFolder   = initPageName.substring(0, index);
				initPageName = initPageName.substring(index + 1);
				if(!initFolder.startsWith("/")){
					initFolder = "/" + initFolder;
				}
			}
		}
		
		ClickUtils.createLabel(htmlGroup, ClickPlugin.getString("wizard.newPage.templateGroup.parentFolder"));
		parentFolder = new Text(htmlGroup, SWT.BORDER);
		parentFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if(selection instanceof IFolder){
			parentFolder.setText(((IFolder)selection).getProjectRelativePath().toString() + initFolder);
		} else if(selection!=null){
			IJavaProject project = ClickUtils.getJavaProject(selection);
			if(project!=null){
				parentFolder.setText(ClickUtils.getWebAppRootFolder(project.getProject()) + initFolder);
			}
		}
		parentFolder.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				validate();
			}
		});
		
		browseParent = new Button(htmlGroup, SWT.PUSH);
		browseParent.setText(ClickPlugin.getString("action.browse"));
		browseParent.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				selectFolder();
			}
		});
		
		ClickUtils.createLabel(htmlGroup, ClickPlugin.getString("wizard.newPage.templateGroup.filename"));
		pageName = new Text(htmlGroup, SWT.BORDER);
		pageName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if(initPageName!=null){
			pageName.setText(initPageName);
		} else {
			pageName.setText("newfile.htm");
		}
		pageName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				validate();
			}
		});
		
		Group classGroup = new Group(composite, SWT.NULL);
		classGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		classGroup.setLayout(new GridLayout(3, false));
		classGroup.setText(ClickPlugin.getString("wizard.newPage.pageClassGroup"));
		
		createPageClass = new Button(classGroup, SWT.CHECK);
		createPageClass.setText(ClickPlugin.getString("wizard.newPage.pageClassGroup.checkbox"));
		createPageClass.setLayoutData(createGridData(3));
		createPageClass.setSelection(settings.getBoolean(NewClickPageWizard.SHOULD_CREATE_CLASS));
		createPageClass.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				updateClassGroup();
				validate();
			}
		});
		
		ClickUtils.createLabel(classGroup, ClickPlugin.getString("wizard.newPage.pageClassGroup.sourceFolder"));
		sourceFolder = new Text(classGroup, SWT.BORDER);
		sourceFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		IPackageFragmentRoot root = ClickUtils.getSourceFolder(selection);
		if(root!=null){
			sourceFolder.setText(root.getElementName());
		} else if(selection!=null){
			try {
				IJavaProject project = ClickUtils.getJavaProject(selection);
				if(project!=null){
					IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
					if(roots.length >= 1){
						sourceFolder.setText(roots[0].getElementName());
					}
				}
			} catch(Exception ex){
				ClickPlugin.log(ex);
			}
		}
		sourceFolder.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				validate();
			}
		});
		
		browseSource = new Button(classGroup, SWT.PUSH);
		browseSource.setText(ClickPlugin.getString("action.browse"));
		browseSource.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				selectSourceFolder();
			}
		});
		
		ClickUtils.createLabel(classGroup, ClickPlugin.getString("wizard.newPage.pageClassGroup.package"));
		packageName = new Text(classGroup, SWT.BORDER);
		packageName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if(selection instanceof IPackageFragment){
			packageName.setText(((IPackageFragment)selection).getElementName());
		} else if(initPackage!=null && initPackage.length()!=0){
			packageName.setText(initPackage);
		} else if(getProject()!=null){
			String pagesPackage = ClickUtils.getPagePackageName(getProject());
			if(pagesPackage != null){
				packageName.setText(pagesPackage);
			}
		}
		packageName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				validate();
			}
		});
		
		browsePackage = new Button(classGroup, SWT.PUSH);
		browsePackage.setText(ClickPlugin.getString("action.browse"));
		browsePackage.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				IRunnableContext context= new BusyIndicatorRunnableContext();
				int style = PackageSelectionDialog.F_REMOVE_DUPLICATES | 
				            PackageSelectionDialog.F_SHOW_PARENTS | 
				            PackageSelectionDialog.F_HIDE_DEFAULT_PACKAGE;
				
				JavaSearchScope scope = new JavaSearchScope();
				try {
					IJavaProject project = JavaCore.create(getProject());
					scope.add((JavaProject)project, JavaSearchScope.SOURCES, new HashSet(2, 1));
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
				
				PackageSelectionDialog dialog = new PackageSelectionDialog(getShell(), context, style, scope);
				dialog.setMultipleSelection(false);
				if(dialog.open()==PackageSelectionDialog.OK){
					Object[] result = dialog.getResult();
					if(result.length >= 1){
						IPackageFragment fragment = (IPackageFragment)result[0];
						packageName.setText(fragment.getElementName());
					}
				}
			}
		});
		
		ClickUtils.createLabel(classGroup, ClickPlugin.getString("wizard.newPage.pageClassGroup.classname"));
		className = new Text(classGroup, SWT.BORDER);
		className.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if(initClassName!=null){
			className.setText(initClassName);
		}
		className.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				validate();
			}
		});
		
		ClickUtils.createLabel(classGroup, "");
		
		ClickUtils.createLabel(classGroup, ClickPlugin.getString("wizard.newPage.pageClassGroup.superclass"));
		superClass = new Text(classGroup, SWT.BORDER);
		superClass.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		superClass.setText(settings.get(NewClickPageWizard.SUPERCLASS));
		superClass.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				validate();
			}
		});
		browseSuperClass = new Button(classGroup, SWT.PUSH);
		browseSuperClass.setText(ClickPlugin.getString("action.browse"));
		browseSuperClass.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				Shell shell = getShell();
				try {
					IJavaProject project = JavaCore.create(getProject());
					
					SelectionDialog dialog = JavaUI.createTypeDialog(
							shell, new ProgressMonitorDialog(shell),
							SearchEngine.createJavaSearchScope(new IJavaElement[]{project}),
							IJavaElementSearchConstants.CONSIDER_CLASSES,false);
					
					if(dialog.open()==SelectionDialog.OK){
						Object[] result = dialog.getResult();
						superClass.setText(((IType)result[0]).getFullyQualifiedName());
					}
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
			}
		});
		
		ClickUtils.createLabel(composite, "");
		
		addToClickXML = new Button(composite, SWT.CHECK);
		addToClickXML.setText(ClickPlugin.getString("wizard.newPage.addMapping"));
		addToClickXML.setSelection(settings.getBoolean(NewClickPageWizard.SHOULD_ADD_TO_CLICK_XML));
		
		updateHTMLGroup();
		updateClassGroup();
		validate();
		setControl(composite);
	}
	
	private void updateHTMLGroup(){
		parentFolder.setEnabled(createPageHTML.getSelection());
		browseParent.setEnabled(createPageHTML.getSelection());
		pageName.setEnabled(createPageHTML.getSelection());
	}
	
	private void updateClassGroup(){
		sourceFolder.setEnabled(createPageClass.getSelection());
		browseSource.setEnabled(createPageClass.getSelection());
		packageName.setEnabled(createPageClass.getSelection());
		browsePackage.setEnabled(createPageClass.getSelection());
		className.setEnabled(createPageClass.getSelection());
		superClass.setEnabled(createPageClass.getSelection());
		browseSuperClass.setEnabled(createPageClass.getSelection());
	}
	
	private void validate(){
		if(getProject()==null){
			setMessage(ClickPlugin.getString("wizard.newPage.error.selectProject"), ERROR);
			setPageComplete(false);
			browsePackage.setEnabled(false);
			browseParent.setEnabled(false);
			browseSource.setEnabled(false);
			return;
		} else {
			browsePackage.setEnabled(createPageClass.getSelection());
			browseParent.setEnabled(createPageClass.getSelection());
			browseSource.setEnabled(createPageHTML.getSelection());
		}
		
		if(createPageHTML.getSelection() || createPageClass.getSelection()){
			if(template.getText().length()==0){
				setMessage(ClickPlugin.getString("wizard.newPage.error.noTemplate"), ERROR);
				setPageComplete(false);
				return;
			}
		}
		
		// for the HTML file part
		if(createPageHTML.getSelection()){
			if(!existsFolder(parentFolder.getText())){
				setMessage(MessageFormat.format(
						ClickPlugin.getString("wizard.newPage.error.folderDoesNotExist"),
						new String[]{ parentFolder.getText() }),
						ERROR);
				setPageComplete(false);
				return;
			} else if(pageName.getText().equals("")){
				setMessage(ClickPlugin.getString("wizard.newPage.error.pageIsEmpty"), ERROR);
				setPageComplete(false);
				return;
			} else if(existsFile(parentFolder.getText(), pageName.getText())){
				setMessage(ClickPlugin.getString("wizard.newPage.error.fileAlreadyExists"), ERROR);
				setPageComplete(false);
				return;
			}
		}
		
		// for the page class part
		if(createPageClass.getSelection()){
			if(!existsFolder(sourceFolder.getText())){
				setMessage(MessageFormat.format(
						ClickPlugin.getString("wizard.newPage.error.folderDoesNotExist"),
						new String[]{ sourceFolder.getText() }),
						ERROR);
				setPageComplete(false);
				return;
			} else if(!isValidPackageName(packageName.getText())){
				setMessage(MessageFormat.format(
						ClickPlugin.getString("wizard.newPage.error.packageIsInvalid1"), 
						new Object[]{ packageName.getText() }), ERROR);
				setPageComplete(false);
				return;
			} else if(packageName.getText().endsWith(".")){
				setMessage(ClickPlugin.getString("wizard.newPage.error.packageIsInvalid2"), 
						ERROR);
				setPageComplete(false);
				return;
			} else if(className.getText().equals("")){
				setMessage(ClickPlugin.getString("wizard.newPage.error.typeIsEmpty"), ERROR);
				setPageComplete(false);
				return;
			} else if(!isValidTypeName(className.getText())){
				setMessage(MessageFormat.format(
						ClickPlugin.getString("wizard.newPage.error.typeIsInvalid"), 
						new Object[]{ className.getText() }), ERROR);
				setPageComplete(false);
				return;
			} else if(existsClass(sourceFolder.getText(), packageName.getText(), className.getText())){
				setMessage(ClickPlugin.getString("wizard.newPage.error.typeAlreadyExists"), ERROR);
				setPageComplete(false);
				return;
			} else if(packageName.getText().equals("")){
				setMessage(ClickPlugin.getString("wizard.newPage.error.defaultPackage"), WARNING);
				setPageComplete(true);
				return;
			}
		}
		
		// all valid
		setMessage(null);
		setPageComplete(true);
	}
	
	private boolean isValidPackageName(String packageName){
		for(int i=0;i<packageName.length();i++){
			char c = packageName.charAt(i);
			if(i==0){
				if(!Character.isJavaIdentifierStart(c)){
					return false;
				}
			} else {
				if(c=='.'){
					continue;
				} else if(!Character.isJavaIdentifierPart(c)){
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isValidTypeName(String className){
		for(int i=0;i<className.length();i++){
			char c = className.charAt(i);
			if(i==0){
				if(!Character.isJavaIdentifierStart(c)){
					return false;
				}
			} else {
				if(!Character.isJavaIdentifierPart(c)){
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean existsFolder(String folder){
		if(folder.equals("")){
			return true; // TODO ?
		}
		
		IProject project = getProject();
		return project.getFolder(folder).exists();
	}
	
	private boolean existsFile(String parentFolder, String fileName){
		IProject project = getProject();
		IFile file = null;
		if(parentFolder.equals("")){
			file = project.getFile(fileName);
		} else {
			file = project.getFolder(parentFolder).getFile(fileName);
		}
		return file.exists();
	}
	
	private boolean existsClass(String sourceFolder, String packageName, String className){
		try {
			IProject project = getProject();
			IResource resource = project.getProject();
			if(!sourceFolder.equals("")){
				resource = project.getFolder(sourceFolder);
			}
			
			IJavaProject javaProject = JavaCore.create(project);
			IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(resource);
			IPackageFragment fragment = root.getPackageFragment(packageName);
			if(!fragment.exists()){
				return false;
			}
			
			ICompilationUnit unit = fragment.getCompilationUnit(className + ".java");
			return unit.exists();
			
		} catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	private GridData createGridData(int colspan){
		GridData gd = new GridData();
		gd.horizontalSpan = colspan;
		return gd;
	}
	
	private void selectProject(){
		IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
		
		// required validator
		ISelectionStatusValidator validator = new ISelectionStatusValidator(){
			private IStatus fgErrorStatus= new StatusInfo(IStatus.ERROR, ""); //$NON-NLS-1$
			private IStatus fgOKStatus= new StatusInfo();
			
			public IStatus validate(Object[] selection){
				if(selection==null || selection.length != 1){
					return fgErrorStatus;
				}
				return fgOKStatus;
			}
		};
		
		// select only IJavaProject
		ViewerFilter filter = new ViewerFilter(){
		    public boolean select(Viewer viewer, Object parentElement, Object element){
		    	try {
			    	if(element instanceof IProject){
			    		if(((IProject)element).hasNature(JavaCore.NATURE_ID)){
			    			return true;
			    		}
			    	}
		    	} catch(Exception ex){}
		    	return false;
		    }
		};
		
		FolderSelectionDialog dialog = new FolderSelectionDialog(
				getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		
		dialog.setTitle(ClickPlugin.getString("wizard.newPage.dialog.selectProject"));
		
		dialog.setInput(wsroot);
		dialog.setValidator(validator);
		dialog.addFilter(filter);
		dialog.setInitialSelection(getProject());
		if (dialog.open() == FolderSelectionDialog.OK) {
			project.setText(((IProject)dialog.getFirstResult()).getName());
		}
	}
	
	private void selectFolder() {
		try {
			IProject currProject = getProject();
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			IResource init = null;
			if(parentFolder.getText().length()!=0){
				init = currProject.getFolder(parentFolder.getText());
				if(!init.exists()){
					init = null;
				}
			}
			Class[] acceptedClasses = new Class[] { IProject.class, IFolder.class };
			ISelectionStatusValidator validator = new TypedElementSelectionValidator(acceptedClasses, false);
			IProject[] allProjects = wsroot.getProjects();
			ArrayList rejectedElements = new ArrayList(allProjects.length);
			for (int i = 0; i < allProjects.length; i++) {
				if (!allProjects[i].equals(currProject)) {
					rejectedElements.add(allProjects[i]);
				}
			}
			ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
			
			FolderSelectionDialog dialog = new FolderSelectionDialog(
					getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			
			dialog.setTitle(ClickPlugin.getString("wizard.newPage.dialog.selectFolder"));
			//dialog.setMessage(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.WebRoot"));
			
			dialog.setInput(wsroot);
			dialog.setValidator(validator);
			dialog.addFilter(filter);
			dialog.setInitialSelection(init);
			if (dialog.open() == FolderSelectionDialog.OK) {
				parentFolder.setText(((IFolder)dialog.getFirstResult()).getProjectRelativePath().toString());
			}
			
		} catch (Throwable t) {
			ClickPlugin.log(t);
		}
	}
	
	private void selectSourceFolder() {
		try {
			Class[] acceptedClasses = new Class[] { IJavaModel.class, IJavaProject.class, IPackageFragmentRoot.class };
			ISelectionStatusValidator validator = new TypedElementSelectionValidator(acceptedClasses, false);
			
			IPackageFragmentRoot init = null;
			IJavaProject project = JavaCore.create(getProject());
			
			IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
			ArrayList rejectedElements = new ArrayList();
			for (int i = 0; i < roots.length; i++) {
				if (roots[i] instanceof JarPackageFragmentRoot) {
					rejectedElements.add(roots[i]);
				} else if(roots[i] instanceof IPackageFragmentRoot){
					if(((IPackageFragmentRoot)roots[i]).isArchive() || ((IPackageFragmentRoot)roots[i]).isExternal()){
						rejectedElements.add(roots[i]);
					} else {
						if(roots[i].getResource().getProjectRelativePath().toString().equals(sourceFolder.getText())){
							init = roots[i];
						}
					}
				}
			}
			IJavaModel model = (IJavaModel)project.getParent();
			IJavaProject[] projects = model.getJavaProjects();
			for(int i=0;i<projects.length;i++){
				if(!projects[i].equals(project)){
					rejectedElements.add(projects[i]);
				}
			}
			
			ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
			
			FolderSelectionDialog dialog = new FolderSelectionDialog(
					getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			
			dialog.setTitle(ClickPlugin.getString("wizard.newPage.dialog.selectSourceFolder"));
			//dialog.setMessage(HTMLPlugin.getResourceString("HTMLProjectPropertyPage.WebRoot"));
			
			dialog.setInput(model);
			dialog.setValidator(validator);
			dialog.addFilter(filter);
			dialog.setInitialSelection(init);
			if (dialog.open() == FolderSelectionDialog.OK) {
				sourceFolder.setText(((IPackageFragmentRoot)dialog.getFirstResult()).getElementName());
			}
			
		} catch (Throwable t) {
			ClickPlugin.log(t);
		}
	}
	
	/**
	 * Returns the wizard should create a HTML file or not.
	 * @return
	 */
	public boolean shouldCreateHTML(){
		return createPageHTML.getSelection();
	}
	
	/**
	 * Returns the project relative path of the parent folder of the HTML file.
	 * @return the project relative path of the parent folder
	 */
	public String getParentFolder(){
		return parentFolder.getText();
	}
	
	/**
	 * Returns the HTML filename.
	 * @return the HTML filename
	 */
	public String getFilename(){
		return pageName.getText();
	}
	
	public boolean shouldCreateClass(){
		return createPageClass.getSelection();
	}
	
	/**
	 * Returns the project relative path of the source folder of the page class.
	 * @return the project relative path of the source folder
	 */
	public String getSourceFolder(){
		return sourceFolder.getText();
	}
	
	/**
	 * Returns the package name of the page class.
	 * @return the package name
	 */
	public String getPackageName(){
		return packageName.getText();
	}
	
	/**
	 * Returns the class name of the page class.
	 * @return the class name
	 */
	public String getClassName(){
		return className.getText();
	}
	
	/**
	 * Returns the wizard should add the page mapping to click.xml or not.
	 * @return
	 */
	public boolean shouldAddToClickXML(){
		return addToClickXML.getSelection();
	}
	
	public String getSuperClass(){
		return superClass.getText();
	}
	
	public Template getTemplate(){
		return (Template)templates.get(template.getSelectionIndex());
	}
	
	private IProject getProject(){
		try {
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = wsroot.getProject(this.project.getText());
			if(project.hasNature(JavaCore.NATURE_ID)){
				return project;
			}
		} catch(Exception ex){
		}
		return null;
	}

}
