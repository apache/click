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
package org.apache.click.eclipse.cayenne;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

/**
 * Provides classpath container <tt>CAYENNE_LIBS</tt>.
 *
 * @author Naoki Takezoe
 */
public class CayenneClasspathContainerInitializer extends ClasspathContainerInitializer {

	public static final String CONTAINER_ID = "org.apache.click.eclipse.cayenne.CAYENNE_LIBS";

	public void initialize(final IPath containerPath, IJavaProject project) throws CoreException {

		final IClasspathEntry[] entries = new IClasspathEntry[]{
				getLibraryEntry("cayenne/cayenne-server-3.0.jar"),
				getLibraryEntry("cayenne/ashwood-2.0.jar"),
				getLibraryEntry("cayenne/commons-collections-3.1.jar"),
				getLibraryEntry("cayenne/commons-logging-1.1.jar"),
		};

		IClasspathContainer container = new IClasspathContainer() {

			public IPath getPath() {
				return containerPath;
			}

			public int getKind() {
				return IClasspathContainer.K_APPLICATION;
			}

			public String getDescription() {
				return "Cayenne Libraries";
			}

			public IClasspathEntry[] getClasspathEntries() {
				return entries;
			}
		};

		JavaCore.setClasspathContainer(containerPath,
				new IJavaProject[]{project},
				new IClasspathContainer[]{container},
				null);
	}

	private static IClasspathEntry getLibraryEntry(String path){
		IPath bundleBase = getBundleLocation();
		if(bundleBase != null){
			IPath jarLocation = bundleBase.append(path);
			return JavaCore.newLibraryEntry(jarLocation, null, null);
		}
		return null;
	}


	private static IPath getBundleLocation(){
		Bundle bundle = CayennePlugin.getDefault().getBundle();
		if(bundle == null){
			return null;
		}

		URL local = null;
		try {
			local = FileLocator.toFileURL(bundle.getEntry("/"));
		} catch(IOException ex){
			return null;
		}

		String fullPath = new File(local.getPath()).getAbsolutePath();
		return Path.fromOSString(fullPath);
	}
}
