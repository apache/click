package net.sf.clickide;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.jst.j2ee.internal.J2EEVersionConstants;
import org.eclipse.jst.j2ee.internal.deployables.J2EEFlexProjDeployable;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.InitParam;
import org.eclipse.jst.j2ee.webapplication.JSPType;
import org.eclipse.jst.j2ee.webapplication.Servlet;
import org.eclipse.jst.j2ee.webapplication.ServletMapping;
import org.eclipse.jst.j2ee.webapplication.ServletType;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.jst.j2ee.webapplication.WebapplicationFactory;
import org.eclipse.jst.j2ee.webapplication.WelcomeFileList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	
	public static final String CLICK_SERVLET_NAME = "ClickServlet";
	public static final String CLICK_SERVLET_CLASS = "net.sf.click.ClickServlet";
	public static final String CLICK_SPRING_SERVLET_CLASS = "net.sf.click.extras.spring.SpringClickServlet";
	public static final String CAYENNE_FILTER_CLASS = "net.sf.click.extras.cayenne.DataContextFilter";
	
	/**
	 * Creates <code>GridData</code>.
	 * 
	 * @param colspan the horizontal span
	 * @param style the style constants
	 * @return the created <code>GridData</code>
	 */
	public static GridData createGridData(int colspan, int style){
		GridData gd = new GridData(style);
		gd.horizontalSpan = colspan;
		return gd;
	}
	
	/**
	 * Creates <code>GridLayout</code> that has no margins.
	 * 
	 * @param columns 
	 * @return the created <code>GridLayout</code>
	 */
	public static GridLayout createGridLayout(int columns){
		GridLayout layout = new GridLayout(columns, false);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		return layout;
	}
	
	/**
	 * Creates <code>Label</code>.
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
	
	/**
	 * Escape XML special charactors.
	 * 
	 * @param value the string
	 * @return the escaped string
	 */
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
	
	/**
	 * Returns <code>IJavaProject</code> from <code>Object</code>.
	 * <p>
	 * This method allows following types as input
	 * <ul>
	 *   <li><code>IJavaProject</code></li>
	 *   <li><code>IJavaElement</code></li>
	 *   <li><code>IResource</code></li>
	 * </ul>
	 * 
	 * @param obj the input object
	 * @return <code>IJavaProject</code> or <code>null</code>
	 */
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
		if(obj instanceof IAdaptable){
			Object result = ((IAdaptable) obj).getAdapter(IJavaProject.class);
			if(result instanceof IJavaProject){
				return (IJavaProject) result;
			} else if(result instanceof IProject){
				return JavaCore.create((IProject) result);
			}
		}
		try {
			Method method = obj.getClass().getMethod("getProject", new Class[0]);
			IJavaProject project = (IJavaProject)method.invoke(obj, new Object[0]);
			return project;
		} catch(Exception ex){
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
	 * Returns the <code>WebArtifactEdit</code> from the project for write.
	 * 
	 * @param project the project
	 * @return the <code>WebArtifactEdit</code> for write
	 */
	public static WebArtifactEdit getWebArtifactEditForWrite(IProject project) {
		return WebArtifactEdit.getWebArtifactEditForWrite(project);
	}
	
	/**
	 * Returns the <code>WebArtifactEdit</code> from the project for read.
	 * 
	 * @param project the project
	 * @return the <code>WebArtifactEdit</code> for read
	 */
	public static WebArtifactEdit getWebArtifactEditForRead(IProject project) {
		return WebArtifactEdit.getWebArtifactEditForRead(project);
	}
	
	/**
	 * Finds the ClickServlet from the given <code>WebApp</code>.
	 * Returns <code>null</code> if this nethod couldn't find ClickServlet.
	 * 
	 * @param webApp the <code>WebApp</code> object
	 * @param useSpring If <code>true</code> then use Spring Framework with Click
	 * @return the <code>Servlet</code> object of the ClickServlet or <code>null</code>
	 */
	public static Servlet findClickServlet(WebApp webApp, boolean useSpring) {
		String servletClassName = useSpring ? CLICK_SPRING_SERVLET_CLASS : CLICK_SERVLET_CLASS;
		
		Servlet servlet = null;
		Iterator it = webApp.getServlets().iterator();
		while (it.hasNext()) {
			servlet = (Servlet) it.next();
			if (servlet.getWebType().isServletType()) {
				if (((ServletType) servlet.getWebType()).getClassName().equals(servletClassName)) {
					break;
				}
			} else if (servlet.getWebType().isJspType()) {
				if (((JSPType) servlet.getWebType()).getJspFile().equals(servletClassName)) {
					break;
				}
			}
		}
		return servlet;
	}
	
	/**
	 * Removes the URL mapping from the given <code>WebApp</code>s.
	 * 
	 * @param webApp the <code>WebApp</code> object
	 * @param servlet the <code>Servlet</code> object of the ClickServlet
	 */
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
	
	/**
	 * Creates the <code>welcome-file-list</code> which contains the <code>index.htm</code>.
	 * 
	 * @param webApp the <code>WebApp</code> object
	 */
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
	 * @param webApp  the <code>WebApp</code> object
	 * @param config the facet installation configuration
	 * @param servlet the <code>Servlet</code> object of the ClickServlet
	 * @param useSpring If <code>true</code> then use Spring Framework with Click
	 * @return the <code>Servlet</code> object of the ClickServlet
	 */
	public static Servlet createOrUpdateServletRef(WebApp webApp, IDataModel config, 
			Servlet servlet, boolean useSpring) {
		//String displayName = config.getStringProperty(CLICK_SERVLET_NAME);
		
		if (servlet == null) {
			// Create the servlet instance and set up the parameters from data
			// model
			servlet = WebapplicationFactory.eINSTANCE.createServlet();
			servlet.setServletName(CLICK_SERVLET_NAME);

			ServletType servletType = WebapplicationFactory.eINSTANCE.createServletType();
			if(useSpring){
				servletType.setClassName(CLICK_SPRING_SERVLET_CLASS);
				if (webApp.getJ2EEVersionID() >= J2EEVersionConstants.J2EE_1_4_ID) {
					// J2EE 1.4
					ParamValue initParam = CommonFactory.eINSTANCE.createParamValue();
					initParam.setName("spring-path");
					initParam.setValue("/applicationContext.xml");
					servlet.getInitParams().add(initParam);
				} else {
					// J2EE 1.2 or 1.3
					InitParam initParam = WebapplicationFactory.eINSTANCE.createInitParam();
					initParam.setParamName("spring-path");
					initParam.setParamValue("/applicationContext.xml");
					servlet.getParams().add(initParam);
				}
			} else {
				servletType.setClassName(CLICK_SERVLET_CLASS);
			}
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
		try {
			J2EEFlexProjDeployable deployable = new J2EEFlexProjDeployable(project);
			IContainer[] containers = deployable.getResourceFolders();
			if(containers.length > 0){
				return containers[0].getProjectRelativePath().toString();
			}
		} catch(Exception ex){
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
	 * Returns the <code>IFile</code> of <tt>click.xml</tt> in the specified project.
	 * 
	 * @param project the project
	 * @return <code>IFile</code> of <tt>click.xml</tt>
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
	 * Returns the <code>IStructuredModel</code> of <tt>click.xml</tt> in the specified project.
	 * 
	 * @param project the project
	 * @return <code>IStructuredModel</code> for <tt>click.xml</tt>
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
	
	/**
	 * Returns the charset which is defined in <tt>click.xml</tt>.
	 * If <tt>click.xml</tt> doesn't has the charset, returns <code>null</code>.
	 * 
	 * @param project the project
	 * @return the charset
	 */
	public static String getCharset(IProject project){
		IStructuredModel model = getClickXMLModel(project);
		String charset = null;
		try {
			NodeList list = (((IDOMModel)model).getDocument()).getElementsByTagName(ClickPlugin.TAG_CLICK_APP);
			if(list.getLength()==1){
				Element format = (Element)list.item(0);
				charset = format.getAttribute(ClickPlugin.ATTR_CHARSET);
			}
		} catch(Exception ex){
		} finally {
			if(model!=null){
				model.releaseFromRead();
			}
		}
		return charset;
	}
	
	/**
	 * Returns <code>IType</code> of the format object which is specified in <tt>click.xml</tt>.
	 * If format element is not defined, this method returns <code>net.sf.click.util.Format</code>.
	 * 
	 * @param project the project
	 * @return IType of the format object
	 */
	public static IType getFormat(IProject project){
		IStructuredModel model = getClickXMLModel(project);
		IJavaProject javaProject = JavaCore.create(project);
		IType formatType = null;
		try {
			NodeList list = (((IDOMModel)model).getDocument()).getElementsByTagName(ClickPlugin.TAG_FORMAT);
			String className = null;
			if(list.getLength()==1){
				Element format = (Element)list.item(0);
				className = format.getAttribute(ClickPlugin.ATTR_CLASSNAME);
			}
			if(className==null){
				className = "net.sf.click.util.Format";
			}
			formatType = javaProject.findType(className);
		} catch(Exception ex){
		} finally {
			if(model!=null){
				model.releaseFromRead();
			}
		}
		return formatType;
	}
	
	/**
	 * Returns the status of the auto mapping in the specified project.
	 * <p>
	 * <strong>Note:</strong> The auto-mapping mode has been available 
	 * in default since Click 1.1.
	 * 
	 * @param project the project
	 * @return true if the auto mapping is enable; false otherwise
	 */
	public static boolean getAutoMapping(IProject project){
		IStructuredModel model = getClickXMLModel(project);
		try {
			NodeList list = (((IDOMModel)model).getDocument()).getElementsByTagName(ClickPlugin.TAG_PAGES);
			if(list.getLength()==1){
				Element pages = (Element)list.item(0);
				if(pages.hasAttribute(ClickPlugin.ATTR_PACKAGE)){
					String autoMapping = pages.getAttribute(ClickPlugin.ATTR_AUTO_MAPPING);
					if("false".equals(autoMapping)){
						return false;
					}
					return true;
				}
			}
		} catch(Exception ex){
		} finally {
			if(model!=null){
				model.releaseFromRead();
			}
		}
		return true;
	}
	
	/**
	 * Returns the package name of page classes which is specified at click.xml.
	 * If the package name is not specified, thie method returns <code>null</code>.
	 * 
	 * @param project the project
	 * @return the package name of page classes or <code>null</code>
	 */
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
					String dir = null;
					String path = className.substring(packageName.length() + 1);
					
					path = path.replaceAll("\\.", "/");
					
					int index = path.lastIndexOf('/');
					if(index >= 0){
						dir =  path.substring(0, index);
						path = path.substring(index + 1);;
					}
					path = path.replaceFirst("Page$", "");
					String[] templateProposals = getTempleteProposals(path);
					
					IFolder folder = project.getFolder(root);
					for(int i=0;i<templateProposals.length;i++){
						IResource resource = null;
						if(dir!=null){
							templateProposals[i] = dir + "/" + templateProposals[i];
						}
						resource = folder.findMember(templateProposals[i]);
						if(resource!=null && resource.exists() && resource instanceof IFile){
							return templateProposals[i];
						}
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
	 * Returns proposals of template filenames.
	 * <p>
	 * If &quot;UseInfo&quot; is given, this method would return:
	 * <ul>
	 *   <li>user-info.htm</li>
	 *   <li>user-info-page.htm</li>
	 *   <li>user_info.htm</li>
	 *   <li>user_info_page.htm</li>
	 *   <li>UserInfo.htm</li>
	 *   <li>userInfo.htm</li>
	 *   <li>userInfoPage.htm</li>
	 * </ul>
	 * 
	 * @param path the page classname which shouldn't contain package name. 
	 *   Also, &quot;Page&quot; postfix should be removed.
	 * @return proposals of template filenames.
	 */
	private static String[] getTempleteProposals(String path){
		String lower = path.substring(0, 1).toLowerCase() + path.substring(1);
		String hifun = path.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
		String under = path.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
		
		List list = new ArrayList();
		
		list.add(path + ".htm");
		list.add(path + "Page.htm");
		list.add(lower + ".htm");
		list.add(lower + "Page.htm");
		list.add(hifun + ".htm");
		list.add(hifun + "-page.htm");
		list.add(under + ".htm");
		list.add(under + "_page.htm");
		
		return (String[])list.toArray(new String[list.size()]);
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
		                	if(packageName.length()!=0){
		                		packageName += ".";
		                	}
		                    packageName = packageName + token;
		                } else {
		                    className = token.replaceFirst("\\.htm$", "");
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
	
	/**
	 * Opens the message dialog which shows the given message.
	 * 
	 * @param message the display message
	 * @see MessageDialog
	 */
	public static void openErrorDialog(String message){
		IWorkbenchPage page = ClickUtils.getActivePage();
		MessageDialog.openError(page.getWorkbenchWindow().getShell(),
				ClickPlugin.getString("message.error"), message);
	}
	
	/**
	 * Returns the <code>IType</code> object of the page class 
	 * which corresponded to the specified HTML template.
	 * If it can't find the page class, this method returns <code>null</code>.
	 * 
	 * @param file the <code>IFile</code> object of the HTML template
	 * @return the <code>IType</code> object of the page class
	 * @see ClickUtils#getTemplateFromPageClass(IType)
	 */
	public static IType getPageClassFromTemplate(IFile file){
		String fullpath = file.getProjectRelativePath().toString();
		String root = ClickUtils.getWebAppRootFolder(file.getProject());
		if(fullpath.startsWith(root)){
			String path = fullpath.substring(root.length());
			if(path.startsWith("/")){
				path = path.substring(1);
			}
			String className = ClickUtils.getClassfromHTML(file.getProject(), path);
			if(className!=null){
				IJavaProject project = JavaCore.create(file.getProject());
				try {
					IType type = project.findType(className);
					if(type!=null){
						return type;
					}
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
				try {
					IType type = project.findType(className + "Page");
					if(type!=null){
						return type;
					}
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the <code>IFile</code> object of the HTML template which
	 * correnponded to the specified page class.
	 * If it can't find the HTML template, this method returns <code>null</code>.
	 * 
	 * @param type the <code>IType</code> object of the page class
	 * @return the <code>IFile</code> object of the HTML template
	 * @see ClickUtils#getPageClassFromTemplate(IFile)
	 */
	public static IFile getTemplateFromPageClass(IType type){
		IProject project = type.getResource().getProject();
		String html = ClickUtils.getHTMLfromClass(project, type.getFullyQualifiedName());
		if(html!=null){
			String root = ClickUtils.getWebAppRootFolder(project);
			IFolder folder = project.getFolder(root);
			IResource resource = folder.findMember(html);
			if(resource!=null && resource instanceof IFile && resource.exists()){
				return (IFile)resource;
			}
		}
		return null;
	}
	
	/**
	 * Creates a qualified class name from a class name which doesn't contain package name.
	 * 
	 * @param parent a full qualified class name of the class which uses this variable
	 * @param type a class name which doesn't contain package name
	 * @return full a created qualified class name
	 */
	public static String resolveClassName(IType parent,String type){
		if(type.indexOf('.') >= 0){
			return type;
		}
		if(isPrimitive(type)){
			return type;
		}
		IJavaProject project = parent.getJavaProject();
		try {
			IType javaType = project.findType("java.lang." + type);
			if(javaType!=null && javaType.exists()){
				return javaType.getFullyQualifiedName();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			IType javaType = project.findType(parent.getPackageFragment().getElementName() + "." + type);
			if(javaType!=null && javaType.exists()){
				return javaType.getFullyQualifiedName();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			IImportDeclaration[] imports = parent.getCompilationUnit().getImports();
			for(int i=0;i<imports.length;i++){
				String importName = imports[i].getElementName();
				if(importName.endsWith("." + type)){
					return importName;
				}
				if(importName.endsWith(".*")){
					try {
						IType javaType = project.findType(importName.replaceFirst("\\*$",type));
						if(javaType!=null && javaType.exists()){
							return javaType.getFullyQualifiedName();
						}
					} catch(Exception ex){
					}
				}
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return type;
	}
	
	/**
	 * This method judges whether the type is a primitive type. 
	 * 
	 * @param type type (classname or primitive type)
	 * @return 
	 * <ul>
	 *   <li>true - primitive type</li>
	 *   <li>false - not primitive type</li>
	 * </ul>
	 */
	public static boolean isPrimitive(String type){
		if(type.equals("int") || type.equals("long") || type.equals("double") || type.equals("float") || 
				type.equals("char") || type.equals("boolean") || type.equals("byte")){
			return true;
		}
		return false;
	}
	
	public static String removeTypeParameter(String name){
		String simpleName = name;
		if(simpleName.indexOf('<')>=0){
			simpleName = simpleName.substring(0, simpleName.lastIndexOf('<'));
		}
		return simpleName;
	}
	

}
