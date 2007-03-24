package net.sf.clickide.core.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.j2ee.web.componentcore.util.WebArtifactEdit;

/**
 * Provides constants and utility methods about the project facet.
 * 
 * @author Naoki Takezoe
 * @since 1.6.0
 */
public class ClickFacetUtil {
	
	public static String CLICK_DIR = "click-1.2";
	
	public static final String[] COPY_FILES = {
		"/lib/click-1.2.jar",
		"/lib/click-extras-1.2.jar",
		"/click.xml",
	};
	
	public static IPath getWebContentPath(IProject project) {
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
