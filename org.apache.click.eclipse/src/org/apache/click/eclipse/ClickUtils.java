/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.eclipse;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.w3c.dom.NodeList;


/**
 * Provides utility methods for ClickIDE.
 *
 * @author Naoki Takezoe
 */
public class ClickUtils {

	public static final String CLICK_SERVLET_NAME = "ClickServlet";
	public static final String CLICK_SERVLET_CLASS = "org.apache.click.ClickServlet";
	public static final String CLICK_SPRING_SERVLET_CLASS = "org.apache.click.extras.spring.SpringClickServlet";
	public static final String CAYENNE_FILTER_CLASS = "org.apache.click.extras.cayenne.DataContextFilter";
	public static final String DEFAULT_FORMAT_CLASS = "org.apache.click.util.Format";

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
		@SuppressWarnings("rawtypes")
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
			@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
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
//				if (webApp.getJ2EEVersionID() >= J2EEVersionConstants.J2EE_1_4_ID) {
//					// J2EE 1.4
//					ParamValue initParam = CommonFactory.eINSTANCE.createParamValue();
//					initParam.setName("spring-path");
//					initParam.setValue("/applicationContext.xml");
//					servlet.getInitParams().add(initParam);
//				} else {
//					// J2EE 1.2 or 1.3
//					InitParam initParam = WebapplicationFactory.eINSTANCE.createInitParam();
//					initParam.setParamName("spring-path");
//					initParam.setParamValue("/applicationContext.xml");
//					servlet.getParams().add(initParam);
//				}
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

	@SuppressWarnings("unchecked")
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
	 * Returns the charset.
	 * If the charset is not specified, returns <code>null</code>.
	 *
	 * @param project the project
	 * @return the charset
	 */
	public static String getCharset(IProject project){
		return ClickPlugin.getDefault().getConfigurationProvider(project).getCharset(project);
	}

	/**
	 * Returns <code>IType</code> of the format object.
	 * If format element is not defined, this method returns <code>net.sf.click.util.Format</code>.
	 *
	 * @param project the project
	 * @return IType of the format object
	 */
	public static IType getFormat(IProject project){
		return ClickPlugin.getDefault().getConfigurationProvider(project).getFormat(project);
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
		return ClickPlugin.getDefault().getConfigurationProvider(project).getAutoMapping(project);
	}

	/**
	 * Returns the package name of page classes.
	 * If the package name is not specified, thie method returns <code>null</code>.
	 *
	 * @param project the project
	 * @return the package name of page classes or <code>null</code>
	 */
	public static String getPagePackageName(IProject project){
		return ClickPlugin.getDefault().getConfigurationProvider(project).getPagePackageName(project);
	}

	/**
	 * Returns the the HTML file path which paired to the specified class.
	 *
	 * @param project the project
	 * @param className the classname
	 * @return the HTML file path.
	 *     If unable to find the paired HTML, returns <code>null</code>.
	 */
	public static String getHTMLfromClass(IProject project, String className){
		return ClickPlugin.getDefault().getConfigurationProvider(project).getHTMLfromClass(project, className);
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
	public static String[] getTempleteProposals(String path){
		String lower = path.substring(0, 1).toLowerCase() + path.substring(1);
		String hifun = path.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
		String under = path.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();

		List<String> list = new ArrayList<String>();

		list.add(path + ".htm");
		list.add(path + "Page.htm");
		list.add(lower + ".htm");
		list.add(lower + "Page.htm");
		list.add(hifun + ".htm");
		list.add(hifun + "-page.htm");
		list.add(under + ".htm");
		list.add(under + "_page.htm");

		return list.toArray(new String[list.size()]);
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
		return ClickPlugin.getDefault().getConfigurationProvider(project).getClassfromHTML(project, htmlName);
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

	/**
	 * Copy data from <code>InputStream</code> to <code>OutputStream</code>.
	 */
	public static void copyStream(InputStream in, OutputStream out){
		try {
			byte[] buf = new byte[1024 * 8];
			int length = 0;
			while((length = in.read(buf))!=-1){
				out.write(buf, 0, length);
			}
		} catch(IOException ex){
			ClickPlugin.log(ex);
		} finally {
			closeQuietly(in);
			closeQuietly(out);
		}
	}

	/**
	 * Read <code>InputStream</code> as UTF-8 text.
	 */
	public static String readStream(InputStream in){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyStream(in, out);
		try {
			return new String(out.toByteArray(), "UTF-8");
		} catch(UnsupportedEncodingException ex){
			throw new RuntimeException(ex);
		}
	}

	public static void closeQuietly(Closeable closeable){
		if(closeable != null){
			try {
				closeable.close();
			} catch(Exception ex){
				;
			}
		}
	}

	/**
	 * Tests the given string is null or empty.
	 *
	 * @param value test string
	 * @return true if value is null or empty, false otherwise
	 */
	public static boolean isEmpty(String value){
		return value == null || value.length() == 0;
	}

	/**
	 * Tests the given string is not null and empty.
	 *
	 * @param value test string
	 * @return true if value is not null and empty, false otherwise
	 */
	public static boolean isNotEmpty(String value){
		return !isEmpty(value);
	}


}
