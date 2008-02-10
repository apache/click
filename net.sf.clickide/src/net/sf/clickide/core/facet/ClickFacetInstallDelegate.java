package net.sf.clickide.core.facet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.cayenne.CayennePlugin;
import net.sf.clickide.core.builder.ClickProjectNature;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.Filter;
import org.eclipse.jst.j2ee.webapplication.FilterMapping;
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
					config.getBooleanProperty(ClickFacetInstallDataModelProvider.USE_CAYENNE));

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
				List enables = new ArrayList();
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
	
	private void deploySpringFiles(IProject project, IDataModel config, 
			IProgressMonitor monitor) throws JavaModelException {
		// TODO implement here
	}
	
	/**
	 * Deploy <tt>cayenne-nodeps.jar</tt> into <tt>WEB-INF/lib</tt>.
	 */
	private void deployCayenneFiles(IProject project, IDataModel config, 
			IProgressMonitor monitor) throws JavaModelException {
		IPath destPath = project.getLocation().append(ClickFacetUtil.getWebContentPath(project));
		File webInf = destPath.append("WEB-INF").toFile();
		
		URL url = CayennePlugin.getDefault().getBundle().getEntry("cayenne/cayenne-nodeps.jar");
		File file = new File(webInf, "lib/cayenne-nodeps.jar");
		try {
			copyStream(url.openStream(), new FileOutputStream(file));
		} catch(IOException ex){
			ClickPlugin.log(ex);
		}
	}
	
	private static void copyStream(InputStream in, OutputStream out){
		try {
			byte[] buf = new byte[1024 * 8];
			int length = 0;
			while((length = in.read(buf))!=-1){
				out.write(buf, 0, length);
			}
		} catch(IOException ex){
			ClickPlugin.log(ex);
		} finally {
			if(in!=null){
				try {
					in.close();
				} catch(Exception ex){}
			}
			if(out!=null){
				try {
					out.close();
				} catch(Exception ex){}
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
				copyStream(url.openStream(), new FileOutputStream(file));
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

	private void createServletAndModifyWebXML(IProject project, final IDataModel config, 
			IProgressMonitor monitor, boolean useSpring, boolean useCayenne) {
		
		WebApp webApp = null;
		WebArtifactEdit artifactEdit = null;
		try {
			artifactEdit = ClickUtils.getWebArtifactEditForWrite(project);
			webApp = artifactEdit.getWebApp();

			// create or update servlet ref
			Servlet servlet = ClickUtils.findClickServlet(webApp);
			if (servlet != null) {
				// remove old mappings
				ClickUtils.removeURLMappings(webApp, servlet);
			}
			
			servlet = ClickUtils.createOrUpdateServletRef(webApp, config, servlet);

			// init mappings
			String[] listOfMappings = {"*.htm"};
			ClickUtils.setUpURLMappings(webApp, listOfMappings, servlet);
			
			// welcome-file-list
			ClickUtils.createOrUpdateFilelist(webApp);
			
			// Add Cayenne Support
			if(useCayenne){
				Filter filter = WebapplicationFactory.eINSTANCE.createFilter();
				filter.setFilterClassName("net.sf.click.extras.cayenne.DataContextFilter");
				filter.setName("DataContextFilter");
				
				// TODO 
//				InitParam initParam = WebapplicationFactory.eINSTANCE.createInitParam();
//				initParam.setParamName("session-scope");
//				initParam.setParamValue("false");
//				filter.getInitParamValues().add(initParam);
				
				webApp.getFilters().add(filter);
				
				FilterMapping mapping = WebapplicationFactory.eINSTANCE.createFilterMapping();
				mapping.setServletName(servlet.getServletName());
				mapping.setFilter(filter);
				webApp.getFilterMappings().add(mapping);
			}
		} catch(Exception ex){
			deployWebXMLFor25(project, useSpring, useCayenne);
		} finally {
			if (artifactEdit != null) {
				// save and dispose
				artifactEdit.saveIfNecessary(monitor);
				artifactEdit.dispose();
			}
			
		}
	}
	
	private void deployWebXMLFor25(IProject project, boolean useSpring, boolean useCayenne){
		IPath destPath = project.getLocation().append(ClickFacetUtil.getWebContentPath(project));
		File webInf = destPath.append("WEB-INF").toFile();
		try {
			File file = new File(webInf, "web.xml");
			if(!file.exists()){
				file.createNewFile();
			}
			
			URL url = ClickPlugin.getDefault().getBundle().getEntry(
					ClickFacetUtil.CLICK_DIR + "/web.xml");
			
			VelocityContext context = new VelocityContext();
			context.put("useSpring", new Boolean(useSpring));
			context.put("useCayenne", new Boolean(useCayenne));
			StringWriter writer = new StringWriter();
			
			InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
			Velocity.evaluate(context, writer, null, reader);
			
			copyStream(new ByteArrayInputStream(writer.toString().getBytes("UTF-8")), 
					new FileOutputStream(file));
		} catch(Exception ex){
			ClickPlugin.log(ex);
		}
	}

}
