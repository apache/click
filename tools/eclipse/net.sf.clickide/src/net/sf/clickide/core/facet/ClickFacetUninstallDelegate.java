package net.sf.clickide.core.facet;

import java.io.File;
import java.util.List;

import net.sf.clickide.ClickUtils;
import net.sf.clickide.core.builder.ClickProjectNature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;
import org.eclipse.jst.j2ee.webapplication.Filter;
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
		
		IDataModel dataModel = (IDataModel) config;
		
		if (monitor != null) {
			monitor.beginTask("", 3); //$NON-NLS-1$
		}
		
		removeClickFiles(project, dataModel, monitor);
		monitor.worked(1);
		
		// Removes the nature
		ClickProjectNature.removeNature(project);
		if (monitor != null) {
			monitor.worked(1);
		}
		
		try {
			// Removes the facet
			uninstallClickReferencesFromWebApp(project, dataModel, monitor);

			if (monitor != null) {
				monitor.worked(1);
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}	
	}
	
	protected void removeClickFiles(IProject project, IDataModel config, IProgressMonitor monitor) {
		IPath destPath = project.getLocation().append(ClickFacetUtil.getWebContentPath(project));
		
		File webInf = destPath.append("WEB-INF").toFile();
		// remove Click files
		for(int i=0;i<ClickFacetUtil.COPY_FILES.length;i++){
			File file = new File(webInf, ClickFacetUtil.COPY_FILES[i]);
			if(file.exists()){
				file.delete();
			}
		}
		
		// remove Spring files
		for(int i=0;i<ClickFacetUtil.SPRING_LIBS.length;i++){
			File file = new File(webInf, ClickFacetUtil.SPRING_LIBS[i]);
			if(file.exists()){
				file.delete();
			}
		}
		
		// remove Cayenne files
		for(int i=0;i<ClickFacetUtil.CAYENNE_LIBS.length;i++){
			File file = new File(webInf, ClickFacetUtil.CAYENNE_LIBS[i]);
			if(file.exists()){
				file.delete();
			}
		}
	}
	
	private void uninstallClickReferencesFromWebApp(IProject project, 
			IDataModel config, IProgressMonitor monitor) {
		WebArtifactEdit artifactEdit = ClickUtils.getWebArtifactEditForWrite(project);
		WebApp webApp = artifactEdit.getWebApp();

		try {
			Servlet servlet = ClickUtils.findClickServlet(webApp, false);
			if (servlet == null){
				servlet = ClickUtils.findClickServlet(webApp, true);
				if(servlet == null){
					return;
				}
			}
			// remove faces url mappings
			removeClickURLMappings(webApp, servlet);
			
			// remove Cayenne filter
			removeCayenneFilter(webApp);
			
			// remove servlet
			removeClickServlet(webApp, servlet);

		} finally {
			if (artifactEdit != null) {
				artifactEdit.saveIfNecessary(monitor);
				artifactEdit.dispose();
			}
		}
	}
	
	private void removeCayenneFilter(WebApp webApp){
		List filters = webApp.getFilters();
		Filter cayenneFilter = null;
		for(int i=0;i<filters.size();i++){
			Filter filter = (Filter) filters.get(i);
			if(filter.getFilterClassName().equals(ClickUtils.CAYENNE_FILTER_CLASS)){
				cayenneFilter = filter;
				break;
			}
		}
		if(cayenneFilter != null){
			while (webApp.getFilterMapping(cayenneFilter) != null) {
				webApp.getFilterMappings().remove(
						webApp.getFilterMapping(cayenneFilter));
			}
		}
		
		webApp.getFilters().remove(cayenneFilter);
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
