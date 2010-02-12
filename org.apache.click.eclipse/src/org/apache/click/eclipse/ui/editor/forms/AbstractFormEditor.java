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
package org.apache.click.eclipse.ui.editor.forms;


import org.apache.click.eclipse.ClickPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

/**
 * The base class for form editors.
 * 
 * @author Naoki Takezoe
 */
public abstract class AbstractFormEditor extends EditorPart {

	protected FormToolkit toolkit;
	protected Form form;
	
	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void updateMenu(){
	}
	
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.setText(ClickPlugin.getString("editor.clickXML.title"));
		form.getBody().setLayout(new GridLayout(1, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.decorateFormHeading(form);
	}
	
	public abstract void initModel(IStructuredModel model);
	
	public abstract void modelUpdated(IStructuredModel model);
	
	public void setFocus() {
		form.setFocus();
	}

}
