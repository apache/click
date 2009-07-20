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

	public static String CLICK_DIR = "click-2.1.0";
	public static String CAYENNE_DIR = "cayenne-2.0.4";
	public static String SPRING_DIR = "spring-2.5.6";

	public static final String[] COPY_FILES = {
		"/lib/click-2.1.0-RC1-incubating.jar",
		"/lib/click-extras-2.1.0-RC1-incubating.jar",
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
		"/lib/spring-2.5.6.jar",
		"/lib/spring-security-core-2.0.4.jar",
		"/lib/standard-1.1.2.jar",
	};

	public static IPath getWebContentPath(IProject project) {
		return new Path(ClickUtils.getWebAppRootFolder(project));
	}
}
