package net.sf.clickide.cayenne;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.ui.IEditorLauncher;

/**
 * 
 * @author Naoki Takezoe
 */
public class CayenneModelerLauncher implements IEditorLauncher {
	
	private boolean firstOpen = true;
	
	public void open(IPath file) {
		// Copy JAR files for Cayenne Modeler.
		if(firstOpen){
			try {
				CayennePlugin.copyCayenneModelerLibraries();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		firstOpen = false;
		
		try {
			File[] jarFiles = CayennePlugin.getCayenneModelerClassPaths();
			String[] classpath = new String[jarFiles.length];
			for(int i=0;i<jarFiles.length;i++){
				classpath[i] = jarFiles[i].getAbsolutePath();
			}
			
			VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(
					"org.apache.cayenne.modeler.Main", classpath);
			vmConfig.setProgramArguments(new String[]{file.makeAbsolute().toString()});
			
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(
					IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			
			Launch launch = new Launch(type.newInstance(null, "Cayenne Modeler"),
					ILaunchManager.RUN_MODE, null);
			
			IVMRunner vmRunner = JavaRuntime.getDefaultVMInstall().getVMRunner(
					ILaunchManager.RUN_MODE);
			vmRunner.run(vmConfig, launch, null);
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
