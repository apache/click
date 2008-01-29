package net.sf.clickide.cayenne;

import org.eclipse.core.runtime.IPath;
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
		
		
	}

}
