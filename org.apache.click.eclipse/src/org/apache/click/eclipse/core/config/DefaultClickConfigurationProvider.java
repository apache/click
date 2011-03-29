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
package org.apache.click.eclipse.core.config;

import java.util.StringTokenizer;


import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ClickUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A default implementation of <code>IClickConfigurationProvider</code>.
 * 
 * @author Naoki Takezoe
 */
public class DefaultClickConfigurationProvider implements IClickConfigurationProvider {

	/**
	 * Returns the status of the auto mapping in the specified project.
	 * <p>
	 * <strong>Note:</strong> The auto-mapping mode has been available 
	 * in default since Click 1.1.
	 * 
	 * @param project the project
	 * @return true if the auto mapping is enable; false otherwise
	 */
	public boolean getAutoMapping(IProject project) {
		IStructuredModel model = ClickUtils.getClickXMLModel(project);
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
	 * Returns the charset which is defined in <tt>click.xml</tt>.
	 * If <tt>click.xml</tt> doesn't has the charset, returns <code>null</code>.
	 * 
	 * @param project the project
	 * @return the charset
	 */
	public String getCharset(IProject project) {
		IStructuredModel model = ClickUtils.getClickXMLModel(project);
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
	 * Returns the full qualified classname of the page class
	 * which paired to the specified HTML file.
	 * 
	 * @param project the project
	 * @param htmlName the HTML file path 
	 * @return the full qulified classname.
	 *   If unable to find the paired class, returns <code>null</code>.
	 */
	public String getClassfromHTML(IProject project, String htmlName) {
		
		String packageName = getPagePackageName(project);
		
		IStructuredModel model = ClickUtils.getClickXMLModel(project);
		try {
			if(model != null){
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
	 * Returns <code>IType</code> of the format object which is specified in <tt>click.xml</tt>.
	 * If format element is not defined, this method returns <code>net.sf.click.util.Format</code>.
	 * 
	 * @param project the project
	 * @return IType of the format object
	 */
	public IType getFormat(IProject project) {
		IStructuredModel model = ClickUtils.getClickXMLModel(project);
		IJavaProject javaProject = JavaCore.create(project);
		IType formatType = null;
		try {
			if(model == null){
				return javaProject.findType(ClickUtils.DEFAULT_FORMAT_CLASS);
			}
			NodeList list = (((IDOMModel)model).getDocument()).getElementsByTagName(ClickPlugin.TAG_FORMAT);
			String className = null;
			if(list.getLength()==1){
				Element format = (Element)list.item(0);
				className = format.getAttribute(ClickPlugin.ATTR_CLASSNAME);
			}
			if(className==null){
				className = ClickUtils.DEFAULT_FORMAT_CLASS;
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
	 * Returns the the HTML file path which paired to the specified class.
	 *  
	 * @param project the project
	 * @param className the classname
	 * @return the HTML file path.
	 *     If unable to find the paired HTML, returns <code>null</code>.
	 */
	public String getHTMLfromClass(IProject project, String className) {
		String packageName = getPagePackageName(project);
		IStructuredModel model = ClickUtils.getClickXMLModel(project);
		try {
			if(model != null){
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
			}
			
			if(getAutoMapping(project) && packageName!=null && packageName.length()>0){
				String root = ClickUtils.getWebAppRootFolder(project);
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
					String[] templateProposals = ClickUtils.getTempleteProposals(path);
					
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
	 * Returns the package name of page classes which is specified in <tt>click.xml</tt>.
	 * If the package name is not specified, thie method returns <code>null</code>.
	 * 
	 * @param project the project
	 * @return the package name of page classes or <code>null</code>
	 */
	public String getPagePackageName(IProject project) {
		IStructuredModel model = ClickUtils.getClickXMLModel(project);
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
	 * This method always returns <code>true</code>.
	 */
	public boolean isSupportedProject(IProject project) {
		return true;
	}

}
