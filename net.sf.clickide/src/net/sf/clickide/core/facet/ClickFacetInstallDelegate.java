package net.sf.clickide.core.facet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.core.builder.ClickProjectNature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.Servlet;
import org.eclipse.jst.j2ee.webapplication.WebApp;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * Installs the click facet.
 * <ol>
 *   <li>Copies click-x.xx.jar and click-extras-x.xx.jar into WEB-INF/lib.</li>
 *   <li>Copies click.xml into WEB-INF.</li>
 *   <li>Adds servlet and servlet-mapping to web.xml.</li>
 *   <li>Adds <code>ClickProjectNature</code> to the project.</li>
 * </ol>
 * @author Naoki Takezoe
 */
public class ClickFacetInstallDelegate implements IDelegate {
	
	private static String CLICK_DIR = "click-1.0";
	
	private static final String[] COPY_FILES = {
		"/lib/click-1.0.jar",
		"/lib/click-extras-1.0.jar",
		"/click.xml",
	};
	
	public void execute(IProject project, IProjectFacetVersion fv,
			Object cfg, IProgressMonitor monitor) throws CoreException {
		
		if (monitor != null) {
			monitor.beginTask("", 2); //$NON-NLS-1$
		}

		try {
			IDataModel config = null;

			if (cfg != null) {
				config = (IDataModel) cfg;
			} else {
				//FIXME: how would we hit this???
//				config = new JSFFacetInstallConfig();
//				config.setJsfImplID(jsfImplID);
			}
			
			// Add Click JARs to WEB-INF/lib
			deployClickFiles(project, config, monitor);

			// Update web model
			createServletAndModifyWebXML(project, config, monitor);

			if (monitor != null) {
				monitor.worked(1);
			}
			
			// Add the nature
			ClickProjectNature.addNatute(project);
			
			if (monitor != null) {
				monitor.worked(1);
			}
			
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}
	
	private void deployClickFiles(IProject project, IDataModel config, IProgressMonitor monitor) {
		IPath destPath = project.getLocation().append(getWebContentPath(project));
		
		File webInf = destPath.append("WEB-INF").toFile();
		for(int i=0;i<COPY_FILES.length;i++){
			InputStream in = null;
			OutputStream out = null;
			try {
				File file = new File(webInf, COPY_FILES[i]);
			
				if(!file.exists()){
					file.createNewFile();
				} else {
					continue;
				}
				
				URL url = ClickPlugin.getDefault().getBundle().getEntry(CLICK_DIR + COPY_FILES[i]);
				in = url.openStream();
				out = new FileOutputStream(file);
				
				byte[] buf = new byte[1024 * 8];
				int length = 0;
				while((length = in.read(buf))!=-1){
					out.write(buf, 0, length);
				}
			} catch(Exception ex){
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
	}

	private void createServletAndModifyWebXML(IProject project, final IDataModel config, IProgressMonitor monitor) {
		
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
			
		} finally {
			if (artifactEdit != null) {
				// save and dispose
				artifactEdit.saveIfNecessary(monitor);
				artifactEdit.dispose();
			}
		}
	}

	private IPath getWebContentPath(IProject project) {
		WebArtifactEdit web = null;
		try {
			web = WebArtifactEdit.getWebArtifactEditForRead(project);
			IPath webxml = web.getDeploymentDescriptorPath();
			//remove project name, WEB-INF an web.xml from path
			IPath webContentPath = webxml.removeLastSegments(2).removeFirstSegments(1);
			return webContentPath;
		} finally {
			if (web != null) {
				web.dispose();
			}
		}
	}

}
