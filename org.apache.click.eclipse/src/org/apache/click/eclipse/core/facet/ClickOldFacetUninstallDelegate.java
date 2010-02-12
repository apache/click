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
package org.apache.click.eclipse.core.facet;

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
