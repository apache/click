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
package org.apache.click.eclipse.preferences;


import org.apache.click.eclipse.ClickPlugin;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class TemplateEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public TemplateEditorPreferencePage(){
		super(GRID);
		setPreferenceStore(ClickPlugin.getDefault().getPreferenceStore());
	}
	
	protected void createFieldEditors() {
		setTitle(ClickPlugin.getString("preferences.templateEditor"));
		Composite parent = getFieldEditorParent();
		
		ColorFieldEditor variable = new ColorFieldEditor(
				ClickPlugin.PREF_COLOR_VAR, 
				ClickPlugin.getString("preferences.templateEditor.colorVariable"), parent);
		addField(variable);
		
		ColorFieldEditor directive = new ColorFieldEditor(
				ClickPlugin.PREF_COLOR_DIR,
				ClickPlugin.getString("preferences.templateEditor.colorDirective"), parent);
		addField(directive);
		
		ColorFieldEditor comment = new ColorFieldEditor(
				ClickPlugin.PREF_COLOR_CMT, 
				ClickPlugin.getString("preferences.templateEditor.colorComment"), parent);
		addField(comment);
	}

	public void init(IWorkbench workbench) {
	}

}
