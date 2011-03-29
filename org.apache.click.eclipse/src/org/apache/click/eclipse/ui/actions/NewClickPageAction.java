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
package org.apache.click.eclipse.ui.actions;


import org.apache.click.eclipse.ui.wizard.NewClickPageWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Opens the New Click Page wizard.
 * 
 * @author Naoki Takezoe
 * @see NewClickPageWizard
 */
public class NewClickPageAction implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchWindow window;
	private IStructuredSelection selection;
	
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection){
			this.selection = (IStructuredSelection)selection;
		} else {
			this.selection = null;
		}
	}

	/**
	 * Opens {@link org.apache.click.eclipse.ui.wizard.NewClickPageWizard}.
	 */
	public void run(IAction action) {
		NewClickPageWizard wizard = new NewClickPageWizard();
		wizard.init(PlatformUI.getWorkbench(), this.selection);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}