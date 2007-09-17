package net.sf.clickide.core.facet;

import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Provides constants and utility methods about the project facet.
 * 
 * @author Naoki Takezoe
 * @since 1.6.0
 */
public class ClickFacetUtil {
	
	public static String CLICK_DIR = "click-1.3.1";
	
	public static final String[] COPY_FILES = {
		"/lib/click-1.3.1.jar",
		"/lib/click-extras-1.3.1.jar",
		"/click.xml",
	};
	
	public static IPath getWebContentPath(IProject project) {
		return new Path(ClickUtils.getWebAppRootFolder(project));
//		WebArtifactEdit web = null;
//		try {
//			web = WebArtifactEdit.getWebArtifactEditForRead(project);
//			IPath webxml = web.getDeploymentDescriptorPath();
//			//remove project name, WEB-INF an web.xml from path
//			IPath webContentPath = webxml.removeLastSegments(2).removeFirstSegments(1);
//			return webContentPath;
//		} finally {
//			if (web != null) {
//				web.dispose();
//			}
//		}
	}
}
