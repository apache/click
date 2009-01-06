/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.eclipse.cayenne.editor;

import java.io.File;


import org.apache.click.eclipse.cayenne.CayennePlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.ui.IEditorLauncher;

/**
 * 
 * @author Naoki Takezoe
 */
public class CayenneModelerLauncher implements IEditorLauncher {
	
	public static final String EDITOR_ID = CayenneModelerLauncher.class.getName();
	
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
			
			Launch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
			IVMRunner vmRunner = JavaRuntime.getDefaultVMInstall().getVMRunner(
					ILaunchManager.RUN_MODE);
			
			vmRunner.run(vmConfig, launch, null);
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
