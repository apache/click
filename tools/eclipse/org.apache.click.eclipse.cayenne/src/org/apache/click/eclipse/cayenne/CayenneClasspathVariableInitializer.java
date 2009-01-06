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
						local.getPath(), "cayenne/cayenne.jar").getAbsolutePath();
				JavaCore.setClasspathVariable(VAR_CAYENNE_LIB, new Path(fullPath), null);
			}
			{ // cayenne-nodeps.jar
				String fullPath = new File(
						local.getPath(), "cayenne/cayenne-nodeps.jar").getAbsolutePath();
				JavaCore.setClasspathVariable(VAR_CAYENNE_NODEPS_LIB, new Path(fullPath), null);
			}
			
		} catch(Exception ex){
			ex.printStackTrace();
			JavaCore.removeClasspathVariable(VAR_CAYENNE_LIB, null);
			JavaCore.removeClasspathVariable(VAR_CAYENNE_NODEPS_LIB, null);
		}
	}

}
