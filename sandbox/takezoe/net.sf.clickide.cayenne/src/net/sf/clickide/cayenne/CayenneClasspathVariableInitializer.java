package net.sf.clickide.cayenne;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;

/**
 * Provides classpath variables <tt>CAYENNE_LIB</tt> and <tt>CAYENNE_NODEPS_LIB</tt>.
 * 
 * @author Naoki Takezoe
 */
public class CayenneClasspathVariableInitializer extends
		ClasspathVariableInitializer {

	public static final String VAR_CAYENNE_LIB = "CAYENNE_LIB";
	public static final String VAR_CAYENNE_NODEPS_LIB = "CAYENNE_NODEPS_LIB";

	public void initialize(String variable) {
		try {
			URL url = CayennePlugin.getDefault().getBundle().getEntry("/");
			URL local = FileLocator.toFileURL(url);
			
			{ //cayenne.jar
				String fullPath = new File(
						local.getPath(), "modeler/cayenne.jar").getAbsolutePath();
				JavaCore.setClasspathVariable(VAR_CAYENNE_LIB, new Path(fullPath), null);
			}
			{ // cayenne-nodeps.jar
				String fullPath = new File(
						local.getPath(), "modeler/cayenne-nodeps.jar").getAbsolutePath();
				JavaCore.setClasspathVariable(VAR_CAYENNE_NODEPS_LIB, new Path(fullPath), null);
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
			JavaCore.removeClasspathVariable(VAR_CAYENNE_LIB, null);
			JavaCore.removeClasspathVariable(VAR_CAYENNE_NODEPS_LIB, null);
		}
	}

}
