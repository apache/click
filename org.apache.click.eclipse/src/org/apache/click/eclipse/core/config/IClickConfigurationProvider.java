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
package org.apache.click.eclipse.core.config;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;

/**
 * Provides framework configurations.
 * 
 * @author Naoki Takezoe
 */
public interface IClickConfigurationProvider {
	
	/**
	 * Returns the charset.
	 * 
	 * @param project the project
	 * @return the charset
	 */
	public String getCharset(IProject project);
	
	/**
	 * Returns the format class.
	 * 
	 * @param project the project
	 * @return the format class
	 */
	public IType getFormat(IProject project);
	
	public boolean getAutoMapping(IProject project);
	
	/**
	 * Returns the root package name which contains page classes.
	 * 
	 * @param project the project
	 * @return the root package name of page classes
	 */
	public String getPagePackageName(IProject project);
	
	/**
	 * Returns the HTML template path from the page classname.
	 * 
	 * @param project the project
	 * @param className the page classname
	 * @return the HTML template path
	 */
	public String getHTMLfromClass(IProject project, String className);
	
	/**
	 * Returns the page classname from the HTML template path.
	 * 
	 * @param project the project
	 * @param htmlName the HTML template path
	 * @return the page classname
	 */
	public String getClassfromHTML(IProject project, String htmlName);
	
	public boolean isSupportedProject(IProject project);
	
}
