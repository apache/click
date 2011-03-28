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
package org.apache.click.eclipse.core.facet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ClickUtils;
import org.apache.click.eclipse.core.builder.ClickProjectNature;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.Listener;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.jst.j2ee.internal.J2EEVersionConstants;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.Filter;
import org.eclipse.jst.j2ee.webapplication.FilterMapping;
import org.eclipse.jst.j2ee.webapplication.InitParam;
import org.eclipse.jst.j2ee.webapplication.Servlet;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.jst.j2ee.webapplication.WebapplicationFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.html.internal.validation.HTMLValidator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidatorMetaData;

/**
 * Installs the click facet.
 * <ol>
 *   <li>Copies click-x.x.jar and click-extras-x.x.jar into WEB-INF/lib.</li>
 *   <li>Copies click.xml into WEB-INF.</li>
 *   <li>Adds servlet and servlet-mapping to web.xml.</li>
 *   <li>Adds <code>ClickProjectNature</code> to the project.</li>
 * </ol>
 * @author Naoki Takezoe
 */
@SuppressWarnings("unchecked")
public class ClickFacetInstallDelegate implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion fv,
			Object cfg, IProgressMonitor monitor) throws CoreException {
		try {
			IDataModel config = null;
			if (cfg != null) {
				config = (IDataModel) cfg;
			}

			if (monitor != null) {
				int totalTasks = 3;
				if(config.getBooleanProperty(ClickFacetInstallDataModelProvider.USE_SPRING)){
					totalTasks++;
				}
				if(config.getBooleanProperty(ClickFacetInstallDataModelProvider.USE_CAYENNE)){
					totalTasks++;
				}
				monitor.beginTask("", totalTasks); //$NON-NLS-1$
			}

			// Add Click JARs to WEB-INF/lib
			deployClickFiles(project, config, monitor);

			// Update web model
			createServletAndModifyWebXML(project, config, monitor,
					config.getBooleanProperty(ClickFacetInstallDataModelProvider.USE_SPRING),
					config.getBooleanProperty(ClickFacetInstallDataModelProvider.USE_CAYENNE),
					config.getBooleanProperty(ClickFacetInstallDataModelProvider.USE_PERFORMANCE_FILTER));

			if (monitor != null) {
				monitor.worked(1);
			}

			// Add the nature
			ClickProjectNature.addNatute(project);

			if (monitor != null) {
				monitor.worked(1);
			}

			// Disable HTML validator
			try {
				ProjectConfiguration projectConfig
					= ConfigurationManager.getManager().getProjectConfiguration(project);
				ValidatorMetaData[] meta = projectConfig.getValidators();
				List<ValidatorMetaData> enables = new ArrayList<ValidatorMetaData>();
				for(int i=0;i<meta.length;i++){
					if(!meta[i].getValidatorUniqueName().equals(HTMLValidator.class.getName())){
						enables.add(meta[i]);
					}
				}
				projectConfig.setDoesProjectOverride(true);

				projectConfig.setEnabledManualValidators(
						(ValidatorMetaData[])enables.toArray(new ValidatorMetaData[enables.size()]));
				projectConfig.setEnabledBuildValidators(
						(ValidatorMetaData[])enables.toArray(new ValidatorMetaData[enables.size()]));

			} catch(Exception ex){
				//ex.printStackTrace();
			}
			if (monitor != null) {
				monitor.worked(1);
			}

			// Install Spring
			if(config.getBooleanProperty(ClickFacetInstallDataModelProvider.USE_SPRING)){
				deploySpringFiles(project, config, monitor);
				if (monitor != null) {
					monitor.worked(1);
				}
			}

			// Install Cayenne
			if(config.getBooleanProperty(ClickFacetInstallDataModelProvider.USE_CAYENNE)){
				deployCayenneFiles(project, config, monitor);
				if (monitor != null) {
					monitor.worked(1);
				}
			}

			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);

		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	/**
	 * Deploy Spring JARs into <tt>WEB-INF/lib</tt>.
	 */
	private void deploySpringFiles(IProject project, IDataModel config,
			IProgressMonitor monitor) throws JavaModelException {
		IPath destPath = project.getLocation().append(ClickFacetUtil.getWebContentPath(project));
		File webInf = destPath.append("WEB-INF").toFile();

		for(int i=0;i<ClickFacetUtil.SPRING_LIBS.length;i++){
			try {
				File file = new File(webInf, ClickFacetUtil.SPRING_LIBS[i]);
				URL url = ClickPlugin.getDefault().getBundle().getEntry(
						ClickFacetUtil.SPRING_DIR + ClickFacetUtil.SPRING_LIBS[i]);
				ClickUtils.copyStream(url.openStream(), new FileOutputStream(file));
			} catch(Exception ex){
				ClickPlugin.log(ex);
			}
		}

		File file = new File(webInf, "spring-beans.xml");
		try {
			if(!file.exists()){
				file.createNewFile();
			}

			URL url = ClickPlugin.getDefault().getBundle().getEntry(
					ClickFacetUtil.SPRING_DIR + "/spring-beans.xml");

			// Replaces ${rootPackage} by the input package name.
			String contents = ClickUtils.readStream(url.openStream());
			contents = contents.replace("${rootPackage}",
					config.getStringProperty(ClickFacetInstallDataModelProvider.ROOT_PACKAGE));

			ClickUtils.copyStream(new ByteArrayInputStream(contents.getBytes("UTF-8")),
					new FileOutputStream(file));

		} catch(Exception ex){
			ClickPlugin.log(ex);
		}
	}

	/**
	 * Deploy Cayenne JARs into <tt>WEB-INF/lib</tt>.
	 */
	private void deployCayenneFiles(IProject project, IDataModel config,
			IProgressMonitor monitor) throws JavaModelException {
		IPath destPath = project.getLocation().append(ClickFacetUtil.getWebContentPath(project));
		File webInf = destPath.append("WEB-INF").toFile();

		for(int i=0;i<ClickFacetUtil.CAYENNE_LIBS.length;i++){
			try {
				File file = new File(webInf, ClickFacetUtil.CAYENNE_LIBS[i]);
				URL url = ClickPlugin.getDefault().getBundle().getEntry(
						ClickFacetUtil.CAYENNE_DIR + ClickFacetUtil.CAYENNE_LIBS[i]);
				ClickUtils.copyStream(url.openStream(), new FileOutputStream(file));
			} catch(Exception ex){
				ClickPlugin.log(ex);
			}
		}

		IJavaProject javaProject = JavaCore.create(project);
		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		for(int i=0;i<roots.length;i++){
			if(roots[i].getResource() instanceof IFolder){
				IFile file = ((IFolder) roots[i].getResource()).getFile("cayenne.xml");
				try {
					file.create(ClickPlugin.getDefault().getBundle().getEntry(
							ClickFacetUtil.CAYENNE_DIR + "/cayenne.xml").openStream(),
							true, monitor);
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
				break;
			}
		}
	}

	private void deployClickFiles(IProject project, IDataModel config, IProgressMonitor monitor) {
		IPath destPath = project.getLocation().append(ClickFacetUtil.getWebContentPath(project));
		File webInf = destPath.append("WEB-INF").toFile();
		for(int i=0;i<ClickFacetUtil.COPY_FILES.length;i++){
			try {
				File file = new File(webInf, ClickFacetUtil.COPY_FILES[i]);
				if(!file.exists() && checkOldFile(file)){
					file.createNewFile();
				} else {
					continue;
				}

				URL url = ClickPlugin.getDefault().getBundle().getEntry(
						ClickFacetUtil.CLICK_DIR + ClickFacetUtil.COPY_FILES[i]);
				if(file.getName().equals("click.xml")){
					// Replaces ${rootPackage} by the input package name.
					String contents = ClickUtils.readStream(url.openStream());
					contents = contents.replace("${rootPackage}",
							config.getStringProperty(ClickFacetInstallDataModelProvider.ROOT_PACKAGE));

					ClickUtils.copyStream(new ByteArrayInputStream(contents.getBytes("UTF-8")),
							new FileOutputStream(file));

				} else {
					ClickUtils.copyStream(url.openStream(), new FileOutputStream(file));
				}
			} catch(Exception ex){
				ClickPlugin.log(ex);
			}
		}
	}

	/**
	 * Checkes whether the old version of the given file exists.
	 */
	private boolean checkOldFile(File file){
		String name = file.getName();
		if(name.startsWith("click-") && name.endsWith(".jar")){
			int index = name.lastIndexOf('-');
			if(index > 0){
				String begin = name.substring(0, index+1);
				File parent = file.getParentFile();
				File[] files = parent.listFiles();
				for(int i=0;i<files.length;i++){
					String fileName = files[i].getName();
					if(files[i].isFile() && fileName.startsWith(begin) && fileName.endsWith(".jar")){
						return false;
					}
				}
			}
		}
		return true;
	}

	private void createServletAndModifyWebXML(
			IProject project, final IDataModel config,IProgressMonitor monitor,
			boolean useSpring, boolean useCayenne, boolean usePerformanceFilter) {

		WebApp webApp = null;
		WebArtifactEdit artifactEdit = null;
		try {
			artifactEdit = ClickUtils.getWebArtifactEditForWrite(project);
			webApp = artifactEdit.getWebApp();

			// create or update servlet ref
			Servlet servlet = ClickUtils.findClickServlet(webApp, useSpring);
			if (servlet != null) {
				// remove old mappings
				ClickUtils.removeURLMappings(webApp, servlet);
			}

			servlet = ClickUtils.createOrUpdateServletRef(webApp, config, servlet, useSpring);

			if(useSpring){
				ParamValue contextParam = CommonFactory.eINSTANCE.createParamValue();
				contextParam.setName("contextConfigLocation");
				contextParam.setValue("WEB-INF/spring-beans.xml");
				webApp.getContextParams().add(contextParam);

				Listener listener = CommonFactory.eINSTANCE.createListener();
				listener.setListenerClassName("org.springframework.web.context.ContextLoaderListener");
				webApp.getListeners().add(listener);
			}

			// Add PerformanceFilter
			if(usePerformanceFilter){
				Filter filter = WebapplicationFactory.eINSTANCE.createFilter();
				filter.setName("PerformanceFilter");
				filter.setFilterClassName("org.apache.click.extras.filter.PerformanceFilter");

				if (webApp.getJ2EEVersionID() >= J2EEVersionConstants.J2EE_1_4_ID) {
					// J2EE 1.4
					ParamValue initParam = CommonFactory.eINSTANCE.createParamValue();
					initParam.setName("cachable-paths");
					initParam.setValue("/assets/*");
					filter.getInitParamValues().add(initParam);
				} else {
					// J2EE 1.2 or 1.3
					InitParam initParam = WebapplicationFactory.eINSTANCE.createInitParam();
					initParam.setParamName("cachable-paths");
					initParam.setParamValue("/assets/*");
					filter.getInitParams().add(initParam);
				}

				webApp.getFilters().add(filter);

				FilterMapping mapping = WebapplicationFactory.eINSTANCE.createFilterMapping();
				mapping.setServletName(servlet.getServletName());
				mapping.setFilter(filter);
				webApp.getFilterMappings().add(mapping);

				String[] filterPatterns = {"*.css", "*.js", "*.gif", "*.png"};
				for(String pattern: filterPatterns){
					mapping = WebapplicationFactory.eINSTANCE.createFilterMapping();
					mapping.setFilter(filter);
					mapping.setUrlPattern(pattern);
					webApp.getFilterMappings().add(mapping);
				}
			}

			// init mappings
			String[] listOfMappings = {"*.htm"};
			ClickUtils.setUpURLMappings(webApp, listOfMappings, servlet);

			// welcome-file-list
			ClickUtils.createOrUpdateFilelist(webApp);

			// Add Cayenne Support
			if(useCayenne){
				Filter filter = WebapplicationFactory.eINSTANCE.createFilter();
				filter.setFilterClassName(ClickUtils.CAYENNE_FILTER_CLASS);
				filter.setName("DataContextFilter");

				if (webApp.getJ2EEVersionID() >= J2EEVersionConstants.J2EE_1_4_ID) {
					// J2EE 1.4
					ParamValue initParam = CommonFactory.eINSTANCE.createParamValue();
					initParam.setName("session-scope");
					initParam.setValue("false");
					filter.getInitParamValues().add(initParam);
				} else {
					// J2EE 1.2 or 1.3
					InitParam initParam = WebapplicationFactory.eINSTANCE.createInitParam();
					initParam.setParamName("session-scope");
					initParam.setParamValue("false");
					filter.getInitParams().add(initParam);
				}

				webApp.getFilters().add(filter);

				FilterMapping mapping = WebapplicationFactory.eINSTANCE.createFilterMapping();
				mapping.setServletName(servlet.getServletName());
				mapping.setFilter(filter);
				webApp.getFilterMappings().add(mapping);
			}
		} catch(Exception ex){
			ClickPlugin.log(ex);
		} finally {
			if (artifactEdit != null) {
				// save and dispose
				artifactEdit.saveIfNecessary(monitor);
				artifactEdit.dispose();
			}

		}
	}

}
