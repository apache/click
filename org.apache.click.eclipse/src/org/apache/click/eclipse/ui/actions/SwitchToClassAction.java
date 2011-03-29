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

import java.text.MessageFormat;


import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ClickUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * Switches to the page class from the HTML file.
 * 
 * @author Naoki Takezoe
 * @see ClickUtils#getPageClassFromTemplate(IFile)
 */
public class SwitchToClassAction implements IEditorActionDelegate {
	
	private IEditorPart editor;
	
	public void selectionChanged(IAction action, ISelection selection) {
		// Nothing to do
	}
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

	public void run(IAction action) {
		IEditorInput input = this.editor.getEditorInput();
		if(input instanceof IFileEditorInput){
			IFile file = ((IFileEditorInput)input).getFile();
			
			if(!ClickUtils.isClickProject(file.getProject())){
				ClickUtils.openErrorDialog(MessageFormat.format(
					ClickPlugin.getString("wizard.newPage.error.notClickProject"), 
					new Object[]{ file.getProject().getName() }));
				return;
			}
			
			try {
				IType type = ClickUtils.getPageClassFromTemplate(file);
				if(type!=null){
					JavaUI.openInEditor(type);
					return;
				}
			} catch(Exception ex){
				ClickPlugin.log(ex);
			}
			ClickUtils.openErrorDialog(
					ClickPlugin.getString("message.error.noPage"));
		}
	}

}
