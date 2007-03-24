package net.sf.clickide.core.facet;

import java.io.File;

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
 * Uninstalls the click facet.
 * 
 * @author Naoki Takezoe
 */
public class ClickFacetUninstallDelegate implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion fv, 
			Object config, IProgressMonitor monitor) throws CoreException {
	
		if (monitor != null) {
			monitor.beginTask("", 2); //$NON-NLS-1$
		}
		
		removeClickFiles(project, (IDataModel) config, monitor);
		
		// Removes the nature
		ClickProjectNature.removeNature(project);
		if (monitor != null) {
			monitor.worked(1);
		}
		
		try {
			// Removes the facet
			uninstallClickReferencesFromWebApp(project, monitor);

			if (monitor != null) {
				monitor.worked(1);
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}	
	}
	
	private void removeClickFiles(IProject project, IDataModel config, IProgressMonitor monitor) {
		IPath destPath = project.getLocation().append(ClickFacetUtil.getWebContentPath(project));
		
		File webInf = destPath.append("WEB-INF").toFile();
		for(int i=0;i<ClickFacetUtil.COPY_FILES.length;i++){
			File file = new File(webInf, ClickFacetUtil.COPY_FILES[i]);
			if(file.exists()){
				file.delete();
			}
		}

	}
	
	private void uninstallClickReferencesFromWebApp(IProject project, IProgressMonitor monitor) {
		WebArtifactEdit artifactEdit = ClickUtils.getWebArtifactEditForWrite(project);
		WebApp webApp = artifactEdit.getWebApp();

		try {
			Servlet servlet = ClickUtils.findClickServlet(webApp);
			if (servlet == null){
				return;
			}
			// remove faces url mappings
			removeClickURLMappings(webApp, servlet);
			// remove servlet
			removeClickServlet(webApp, servlet);

		} finally {
			if (artifactEdit != null) {
				artifactEdit.saveIfNecessary(monitor);
				artifactEdit.dispose();
			}
		}
	}
	
	private void removeClickURLMappings(WebApp webApp, Servlet servlet) {
		while (webApp.getServletMapping(servlet) != null) {
			webApp.getServletMappings().remove(
					webApp.getServletMapping(servlet));
		}
	}
	
	private void removeClickServlet(WebApp webApp, Servlet servlet) {
		webApp.getServlets().remove(servlet);
	}

}
