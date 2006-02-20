package net.sf.clickide.core.facet;

import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.Servlet;
import org.eclipse.jst.j2ee.webapplication.WebApp;
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
			monitor.beginTask("", 1); //$NON-NLS-1$
		}

		try {
			uninstallJSFReferencesFromWebApp(project, monitor);

			if (monitor != null) {
				monitor.worked(1);
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}	
	}
	
	private void uninstallJSFReferencesFromWebApp(IProject project, IProgressMonitor monitor) {
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
