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


import org.apache.click.eclipse.cayenne.editor.CayenneModelerLauncher;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * This wizard creates new Cayenne mapping project.
 * <p>
 * Generates <tt>cayenne.xml</tt> which has the following contents:
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="utf-8"?&gt;
 * &lt;domains project-version="2.0"&gt;
 * &lt;/domains&gt; </pre>
 * 
 * And open generated <tt>cayenne.xml</tt> by the Cayenne Modeler.
 * 
 * @author Naoki Takezoe
 */
public class NewCayenneWizard extends Wizard implements INewWizard {

	private NewCayenneWizardPage page;
	private ISelection selection;
	
	public NewCayenneWizard() {
	}
	
	public void addPages() {
		page = new NewCayenneWizardPage(selection);
		addPage(page);
	}


	public boolean performFinish() {
		IFile file = page.createNewFile();
		if(file==null){
			return false;
		}
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, file, CayenneModelerLauncher.EDITOR_ID);
		} catch(PartInitException ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

}
