package net.sf.clickide;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.j2ee.internal.deployables.J2EEFlexProjDeployable;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.JSPType;
import org.eclipse.jst.j2ee.webapplication.Servlet;
import org.eclipse.jst.j2ee.webapplication.ServletMapping;
import org.eclipse.jst.j2ee.webapplication.ServletType;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.jst.j2ee.webapplication.WebapplicationFactory;
import org.eclipse.jst.j2ee.webapplication.WelcomeFileList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Provides utility methods for ClickIDE.
 * 
 * @author Naoki Takezoe
 */
public class ClickUtils {
	
	private static final String CLICK_SERVLET_NAME = "click-servlet";
	private static final String CLICK_SERVLET_CLASS = "net.sf.click.ClickServlet";
	
//	public static Resource getClickAppType(IProject project){
//		IFile file = project.getFile("WebContent/WEB-INF/click.xml");
//		if(!file.exists()){
//			return null;
//		}
//		
//		try {
//			URI uri = URI.createPlatformResourceURI(file.getFullPath().toString());
//			ClickResourceFactoryImpl factory = new ClickResourceFactoryImpl();
//			Resource resource = factory.createResource(uri);
//			resource.load(file.getContents(), new HashMap());
//			
//			return resource;
//			
//		} catch(Exception ex){
//			ex.printStackTrace();
//		}
//		
//		return null;
//	}
//	
//	public static ClickAppType getClickApp(Resource resource){
//		DocumentRoot root = (DocumentRoot)resource.getContents().get(0);
//		return root.getClickApp();
//	}
	
	/**
	 * Creates GridData.
	 * 
	 * @param colspan the horizontal span
	 * @param style the style constants
	 * @return the created GridData
	 */
	public static GridData createGridData(int colspan, int style){
		GridData gd = new GridData(style);
		gd.horizontalSpan = colspan;
		return gd;
	}
	
	/**
	 * Creates Label.
	 * 
	 * @param parent the parent composite
	 * @param text the text which will be displayed on the Label
	 * @return created Label
	 */
	public static Label createLabel(Composite parent, String text){
		Label label = new Label(parent, SWT.NULL);
		label.setText(text);
		return label;
	}
	
	public static String escapeXML(String value){
		value = value.replaceAll("&", "&amp;");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll(">", "&gt;");
		value = value.replaceAll("\"", "&quot;");
		return value;
	}
	
	public static boolean isClickProject(IProject project){
		IVirtualComponent component = ComponentCore.createComponent(project);
		try {
			if(WebArtifactEdit.isValidWebModule(component)){
				IFacetedProject facetedProject = ProjectFacetsManager.create(project);
				Object facets[] = facetedProject.getProjectFacets().toArray();
				for(int i=0;i<facets.length;i++){
					IProjectFacetVersion facet = (IProjectFacetVersion)facets[i];
					if(facet.getProjectFacet().getId().equals("click")){
						return true;
					}
				}
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns IFile that was related with IDocument.
	 * 
	 * @param document the document object
	 * @return the file that was related with the specified document
	 */
	public static IFile getResource(IDocument document) {
		IFile resource = null;
		String baselocation = null;

		if (document != null) {
			IStructuredModel model = null;
			try {
				model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
				if (model != null) {
					baselocation = model.getBaseLocation();
				}
			} finally {
				if (model != null){
					model.releaseFromRead();
				}
			}
		}

		if (baselocation != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath filePath = new Path(baselocation);
			if (filePath.segmentCount() > 0) {
				resource = root.getFile(filePath);
			}
		}
		return resource;
	}
	
	public static IJavaProject getJavaProject(Object obj){
		if(obj instanceof IJavaProject){
			return (IJavaProject)obj;
		}
		if(obj instanceof IJavaElement){
			return ((IJavaElement)obj).getJavaProject();
		}
		if(obj instanceof IResource){
			return JavaCore.create(((IResource)obj).getProject());
		}
		return null;
	}
	
	public static IPackageFragmentRoot getSourceFolder(Object obj){
		if(obj instanceof IPackageFragmentRoot){
			return (IPackageFragmentRoot)obj;
		} else if(obj instanceof IJavaElement){
			IJavaElement parent = ((IJavaElement)obj).getParent();
			if(parent!=null){
				return getSourceFolder(parent);
			}
		}
		return null;
	}
	
	/**
	 * Returns the WebArtifactEdit from the project for write.
	 * 
	 * @param project the project
	 * @return the WebArtifactEdit for write
	 */
	public static WebArtifactEdit getWebArtifactEditForWrite(IProject project) {
		return WebArtifactEdit.getWebArtifactEditForWrite(project);
	}
	
	/**
	 * Returns the WebArtifactEdit from the project for read.
	 * 
	 * @param project the project
	 * @return the WebArtifactEdit for read
	 */
	public static WebArtifactEdit getWebArtifactEditForRead(IProject project) {
		return WebArtifactEdit.getWebArtifactEditForRead(project);
	}
	
	/**
	 * 
	 * 
	 * @param webApp
	 * @return
	 */
	public static Servlet findClickServlet(WebApp webApp) {
		Servlet servlet = null;
		Iterator it = webApp.getServlets().iterator();
		while (it.hasNext()) {
			servlet = (Servlet) it.next();
			if (servlet.getWebType().isServletType()) {
				if (((ServletType) servlet.getWebType()).getClassName().equals(CLICK_SERVLET_CLASS)) {
					break;
				}
			} else if (servlet.getWebType().isJspType()) {
				if (((JSPType) servlet.getWebType()).getJspFile().equals(CLICK_SERVLET_CLASS)) {
					break;
				}
			}
		}
		return servlet;
	}
	
	public static void removeURLMappings(WebApp webApp, Servlet servlet) {
		String servletName = servlet.getServletName();
		if (servletName != null) {
			Iterator oldMappings = webApp.getServletMappings().iterator();
			while (oldMappings.hasNext()) {
				ServletMapping mapping = (ServletMapping) oldMappings.next();
				if (mapping.getServlet().getServletName().equals(servletName)) {
					webApp.getServletMappings().remove(mapping);
				}
			}
		}
	}
	
	public static void createOrUpdateFilelist(WebApp webApp) {
		WelcomeFileList filelist = webApp.getFileList();
		
		if(filelist==null){
			filelist = WebapplicationFactory.eINSTANCE.createWelcomeFileList();
			filelist.addFileNamed("index.htm");
			filelist.setWebApp(webApp);
		} else {
			filelist.getFile().removeAll(filelist.getFile());
			filelist.addFileNamed("index.htm");
		}
	}
	
	/**
	 * Adds or updates the servlet information in the web.xml.
	 * 
	 * @param webApp
	 * @param config
	 * @param servlet
	 * @return
	 */
	public static Servlet createOrUpdateServletRef(WebApp webApp, IDataModel config, Servlet servlet) {
		//String displayName = config.getStringProperty(CLICK_SERVLET_NAME);

		if (servlet == null) {
			// Create the servlet instance and set up the parameters from data
			// model
			servlet = WebapplicationFactory.eINSTANCE.createServlet();
			servlet.setServletName(CLICK_SERVLET_NAME);

			ServletType servletType = WebapplicationFactory.eINSTANCE.createServletType();
			servletType.setClassName(CLICK_SERVLET_CLASS);
			servlet.setWebType(servletType);
			servlet.setLoadOnStartup(new Integer(1));
			// Add the servlet to the web application model
			webApp.getServlets().add(servlet);
		} else {
			// update
			servlet.setServletName(CLICK_SERVLET_NAME);
			servlet.setLoadOnStartup(new Integer(1));
		}
		return servlet;
	}
	
	public static void setUpURLMappings(WebApp webApp, String[] urlMappingList, Servlet servlet) {
		// Add mappings
		for (int i=0;i<urlMappingList.length;i++) {
			String pattern = urlMappingList[i];
			ServletMapping mapping = WebapplicationFactory.eINSTANCE.createServletMapping();
			mapping.setServlet(servlet);
			mapping.setName(servlet.getServletName());
			mapping.setUrlPattern(pattern);
			webApp.getServletMappings().add(mapping);
		}
	}
	
	public static IDOMElement getElement(IDOMElement base, String tagName){
		NodeList list = base.getElementsByTagName(tagName);
		if(list.getLength()==0){
			return null;
		} else {
			return (IDOMElement)list.item(0);
		}
	}
	
	public static String[] createComboValues(String[] values){
		String[] result = new String[values.length + 1];
		result[0] = "";
		for(int i=0;i<values.length;i++){
			result[i+1] = values[i];
		}
		return result;
	}
	
	/**
	 * Returns the path string of the web application root folder.
	 * 
	 * @param project the project
	 * @return the path string of the web application root folder.
	 *    If any errors occurs, returns blank string.
	 */
	public static String getWebAppRootFolder(IProject project){
		WebArtifactEdit edit = getWebArtifactEditForRead(project);
		try {
			J2EEFlexProjDeployable deployable = new J2EEFlexProjDeployable(project);
			IContainer[] containers = deployable.getResourceFolders();
			if(containers.length > 0){
				return containers[0].getProjectRelativePath().toString();
			}
		} catch(Exception ex){
		} finally {
			if(edit!=null){
				edit.dispose();
			}
		}
		return "";
	}
	
	/**
	 * Returns the active <code>IWorkbenchPage</code>.
	 * 
	 * @return the active workbench page
	 */
	public static IWorkbenchPage getActivePage(){
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	/**
	 * Returns the <code>IFile</code> of click.xml in the specified project.
	 * 
	 * @param project the project
	 * @return <code>IFile</code> of click.xml
	 */
	public static IFile getClickConfigFile(IProject project){
		String webAppRoot = getWebAppRootFolder(project);
		IFile file = project.getFile(new Path(webAppRoot).append("WEB-INF/click.xml"));
		if(file.exists()){
			return file;
		}
		return null;
	}
	
	/**
	 * Returns the <code>IStructuredModel</code> of click.xml in the specified project.
	 * 
	 * @param project the project
	 * @return <code>IStructuredModel</code> for click.xml
	 */
	public static IStructuredModel getClickXMLModel(IProject project){
		IStructuredModel model = null;
		try {
			IFile file = ClickUtils.getClickConfigFile(project);
			if(file==null){
				return null;
			}
			model = StructuredModelManager.getModelManager().getModelForRead(file);
		} catch(Exception ex){
			ClickPlugin.log(ex);
		}
		return model;
	}
	
	public static boolean getAutoMapping(IProject project){
		IStructuredModel model = getClickXMLModel(project);
		try {
			NodeList list = (((IDOMModel)model).getDocument()).getElementsByTagName(ClickPlugin.TAG_PAGES);
			if(list.getLength()==1){
				Element pages = (Element)list.item(0);
				if(pages.hasAttribute(ClickPlugin.ATTR_PACKAGE)){
					String autoMapping = pages.getAttribute(ClickPlugin.ATTR_AUTO_MAPPING);
					if("true".equals(autoMapping)){
						return true;
					}
					return false;
				}
			}
		} catch(Exception ex){
		} finally {
			if(model!=null){
				model.releaseFromRead();
			}
		}
		return false;
	}
	
	public static String getPagePackageName(IProject project){
		IStructuredModel model = getClickXMLModel(project);
		try {
			NodeList list = (((IDOMModel)model).getDocument()).getElementsByTagName(ClickPlugin.TAG_PAGES);
			if(list.getLength()==1){
				Element pages = (Element)list.item(0);
				if(pages.hasAttribute(ClickPlugin.ATTR_PACKAGE)){
					return pages.getAttribute(ClickPlugin.ATTR_PACKAGE);
				}
			}
		} catch(Exception ex){
		} finally {
			if(model!=null){
				model.releaseFromRead();
			}
		}
		return null;
	}
	
	/**
	 * Returns the the HTML file path which paired to the specified class.
	 *  
	 * @param project the project
	 * @param className the classname
	 * @return the HTML file path which registered in the click.xml.
	 *     If unable to find the paired HTML, returns <code>null</code>.
	 */
	public static String getHTMLfromClass(IProject project, String className){
		
		String packageName = getPagePackageName(project);
		IStructuredModel model = getClickXMLModel(project);
		try {
			NodeList list = (((IDOMModel)model).getDocument()).getElementsByTagName(ClickPlugin.TAG_PAGE);
			for(int i=0;i<list.getLength();i++){
				Element element = (Element)list.item(i);
				String clazz = element.getAttribute(ClickPlugin.ATTR_CLASSNAME);
				if(clazz!=null){
					if(packageName!=null && packageName.length()>0){
						clazz = packageName + "." + clazz;
					}
					if(clazz.equals(className)){
						return element.getAttribute(ClickPlugin.ATTR_PATH);
					}
				}
			}
			
			if(getAutoMapping(project) && packageName!=null && packageName.length()>0){
				String root = getWebAppRootFolder(project);
				if(className.startsWith(packageName + ".")){
					String path = className.substring(packageName.length() + 1);
					path = path.replaceAll("\\.", "/");
					path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
					
					// Login -> login.htm
					IFolder folder = project.getFolder(root);
					IResource resource = folder.findMember(path + ".htm");
					if(resource!=null && resource.exists() && resource instanceof IFile){
						return path + ".htm";
					}
					// Login -> loginPage.htm
					resource = folder.findMember(path + "Page.htm");
					if(resource!=null && resource.exists() && resource instanceof IFile){
						return path + "Page.htm";
					}
				}
			}

		} catch(Exception ex){
			ClickPlugin.log(ex);
		} finally {
			if(model!=null){
				model.releaseFromRead();
			}
		}
		return null;
	}
	
	/**
	 * Returns the full qualified classname of the page class
	 * which paired to the specified HTML file.
	 * 
	 * @param project the project
	 * @param htmlName the HTML file path which registered to the click.xml 
	 * @return the full qulified classname.
	 *   If unable to find the paired class, returns <code>null</code>.
	 */
	public static String getClassfromHTML(IProject project, String htmlName){
		
		String packageName = getPagePackageName(project);
		
		IStructuredModel model = getClickXMLModel(project);
		try {
			NodeList list = (((IDOMModel)model).getDocument()).getElementsByTagName(ClickPlugin.TAG_PAGE);
			for(int i=0;i<list.getLength();i++){
				Element element = (Element)list.item(i);
				String path = element.getAttribute(ClickPlugin.ATTR_PATH);
				if(path!=null && path.equals(htmlName)){
					String className = element.getAttribute(ClickPlugin.ATTR_CLASSNAME);
					if(className!=null && className.length()>0 && 
							packageName!=null && packageName.length()>0){
						className = packageName + "." + className;
					}
					return className;
				}
			}
			
			if(getAutoMapping(project) && packageName!=null && packageName.length()>0){
				
				String className = "";
				
		        if (htmlName.indexOf("/") != -1) {
		            StringTokenizer tokenizer = new StringTokenizer(htmlName, "/");
		            while (tokenizer.hasMoreTokens()) {
		                String token = tokenizer.nextToken();
		                if (tokenizer.hasMoreTokens()) {
		                    packageName = packageName + token + ".";
		                } else {
		                    className = token;
		                }
		            }
		        } else {
		            className = htmlName.substring(0, htmlName.lastIndexOf('.'));
		        }
		        
		        StringTokenizer tokenizer = new StringTokenizer(className, "_-");
		        className = "";
		        while (tokenizer.hasMoreTokens()) {
		            String token = tokenizer.nextToken();
		            token = Character.toUpperCase(token.charAt(0)) + token.substring(1);
		            className += token;
		        }

		        className = packageName + "." + className;
		        
		        return className;
			}
			
		} catch(Exception ex){
			ClickPlugin.log(ex);
		} finally {
			if(model!=null){
				model.releaseFromRead();
			}
		}
		return null;
	}
	
	public static void openErrorDialog(String message){
		IWorkbenchPage page = ClickUtils.getActivePage();
		MessageDialog.openError(page.getWorkbenchWindow().getShell(),
				ClickPlugin.getString("message.error"), message);
	}
}
