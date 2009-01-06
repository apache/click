package org.apache.click.eclipse.core.facet;


import org.apache.click.eclipse.ClickUtils;
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

	public static String CLICK_DIR = "click-1.5";
	public static String CAYENNE_DIR = "cayenne-2.0.4";
	public static String SPRING_DIR = "spring-2.5.4";

	public static final String[] COPY_FILES = {
		"/lib/click-1.5.jar",
		"/lib/click-extras-1.5.jar",
		"/click.xml",
	};

	public static final String[] CAYENNE_LIBS = {
		"/lib/ashwood-1.1.jar",
		"/lib/cayenne-nodeps.jar",
		"/lib/commons-logging-1.0.4.jar",
		"/lib/log4j-1.2.14.jar",
		"/lib/oro-2.0.8.jar"
	};

	public static final String[] SPRING_LIBS = {
		"/lib/jstl-1.1.2.jar",
		"/lib/spring-beans-2.5.4.jar",
		"/lib/spring-context-2.5.4.jar",
		"/lib/spring-core-2.5.4.jar",
		"/lib/spring-web-2.5.4.jar",
		"/lib/standard-1.1.2.jar",
	};

	public static IPath getWebContentPath(IProject project) {
		return new Path(ClickUtils.getWebAppRootFolder(project));
	}
}
