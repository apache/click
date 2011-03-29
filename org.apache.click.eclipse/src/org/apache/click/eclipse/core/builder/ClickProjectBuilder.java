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
package org.apache.click.eclipse.core.builder;

import java.util.Map;


import org.apache.click.eclipse.ClickUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The incremental builder for the Click project.
 *
 * @author Naoki Takezoe
 * @see ClickProjectNature
 */
public class ClickProjectBuilder extends IncrementalProjectBuilder {

	/** The id of this builder. */
	public static final String BUILDER_ID = "org.apache.click.eclipse.ClickProjectBuilder";

	private ClickXMLValidator validator = new ClickXMLValidator();

	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		IFile file = ClickUtils.getClickConfigFile(getProject());
		if(file != null){
			validator.validate(file, monitor);
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		delta.accept(new ClickDeltaVisitor(monitor));
	}

	private class ClickDeltaVisitor implements IResourceDeltaVisitor {

		private IProgressMonitor monitor;

		public ClickDeltaVisitor(IProgressMonitor monitor){
			this.monitor = monitor;
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if(resource instanceof IFile){
				if(resource.getName().endsWith(".htm")){
					fullBuild(monitor);
					return false;
				}
				if(resource.getName().endsWith(".java")){
					fullBuild(monitor);
					return false;
				}
				if(resource.getName().endsWith(".class")){
					fullBuild(monitor);
					return false;
				}
				if(resource.getName().endsWith(".jar")){
					fullBuild(monitor);
					return false;
				}
				if(resource.getName().equals("click.xml")){
					fullBuild(monitor);
					return false;
				}
			}
			return true;
		}
	}


}
