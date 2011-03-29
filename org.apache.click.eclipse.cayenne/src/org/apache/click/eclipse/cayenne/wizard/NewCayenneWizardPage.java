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
package org.apache.click.eclipse.cayenne.wizard;

import java.io.InputStream;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewCayenneWizardPage extends WizardNewFileCreationPage {

	public NewCayenneWizardPage(ISelection selection) {
		super("wizardPage",(IStructuredSelection)selection);
		setTitle("New Cayenne Mapping Project");
		setDescription("Create the new Cayenne mapping project.");
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileName("cayenne.xml");
	}
	
	protected InputStream getInitialContents() {
		return NewCayenneWizardPage.class.getResourceAsStream("cayenne.xml");
	}


}
