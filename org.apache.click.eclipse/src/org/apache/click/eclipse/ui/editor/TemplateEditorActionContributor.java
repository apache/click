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
package org.apache.click.eclipse.ui.editor;

import java.util.ArrayList;
import java.util.List;


import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ui.actions.ToggleCommentAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.eclipse.wst.html.ui.internal.edit.ui.ActionContributorHTML;

public class TemplateEditorActionContributor extends ActionContributorHTML {

	private List<String> actionIds = new ArrayList<String>();
	private List<IAction> actions = new ArrayList<IAction>();

	public TemplateEditorActionContributor(){
		actionIds.add(ToggleCommentAction.class.getName());
	}

	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);
		doSetActiveEditor(part);
	}

	private void doSetActiveEditor(IEditorPart part) {
		ITextEditor textEditor= null;
		if (part instanceof ITextEditor){
			textEditor = (ITextEditor) part;
		}
		if(textEditor!=null){
			for(int i=0;i<this.actions.size();i++){
				RetargetTextEditorAction action = (RetargetTextEditorAction) actions.get(i);
				IAction targetAction = textEditor.getAction((String) actionIds.get(i));
				if(targetAction!=null){
					action.setAccelerator(targetAction.getAccelerator());
					action.setAction(targetAction);
				} else {
					action.setAccelerator(SWT.NULL);
					action.setAction(null);
				}
			}
		}
	}

	public void init(IActionBars bars) {
		super.init(bars);

		IMenuManager menuManager = bars.getMenuManager();
		IMenuManager editMenu = menuManager.findMenuUsingPath("edit");
		if(editMenu!=null){
			editMenu.insertBefore("additions", new Separator("clickide"));

			if (editMenu != null) {
				for(int i=0;i<actionIds.size();i++){
					RetargetTextEditorAction action = new RetargetTextEditorAction(
							ClickPlugin.getDefault().getResourceBundle(), null);
					this.actions.add(action);
					editMenu.appendToGroup("clickide", action);
				}
			}
		}
	}



}
