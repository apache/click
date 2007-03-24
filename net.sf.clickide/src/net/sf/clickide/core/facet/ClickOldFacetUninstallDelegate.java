package net.sf.clickide.core.facet;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;


/**
 * <code>UninstallDelegate</code> for old facets.
 * 
 * @author Naoki Takezoe
 * @since 1.6.0
 * @deprecated
 */
public class ClickOldFacetUninstallDelegate extends ClickFacetUninstallDelegate {

	protected void removeClickFiles(IProject project, IDataModel config, IProgressMonitor monitor) {
		IPath destPath = project.getLocation().append(ClickFacetUtil.getWebContentPath(project));
		
		File webInf = destPath.append("WEB-INF").toFile();
		File libDir = new File(webInf, "lib");
		File[] files = libDir.listFiles();
		
		// removes JAR files
		for(int i=0;i<files.length;i++){
			String name = files[i].getName();
			if(files[i].isFile() && name.startsWith("click-") && name.endsWith(".jar")){
				files[i].delete();
			}
		}
		
		// removes click.xml
		File clickXml = new File(webInf, "click.xml");
		if(clickXml.exists()){
			clickXml.delete();
		}
	}

}
