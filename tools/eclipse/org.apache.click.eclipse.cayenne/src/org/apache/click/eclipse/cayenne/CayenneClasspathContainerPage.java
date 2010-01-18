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

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 *
 * @author Naoki Takezoe
 */
public class CayenneClasspathContainerPage extends WizardPage implements IClasspathContainerPage {

	private IClasspathEntry containerEntryResult;

	public CayenneClasspathContainerPage() {
		super("CayenneClasspathContainerPage");
		setTitle("Cayenne Libraries");
		setDescription(CayennePlugin.getString("wizard.classpathContainer.description"));
	}

	public boolean finish() {
		containerEntryResult = JavaCore.newContainerEntry(
				new Path(CayenneClasspathContainerInitializer.CONTAINER_ID));
		return true;
	}

	public IClasspathEntry getSelection() {
		return containerEntryResult;
	}

	public void setSelection(IClasspathEntry entry) {
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		setControl(composite);
	}

}
