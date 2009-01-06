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
package org.apache.click.eclipse.ui.editor.attrs;


import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ClickUtils;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * The implementation of <code>IElementEditor</code> for &lt;header&gt;.
 * 
 * @author Naoki Takezoe
 */
public class HeaderAttributeEditor implements IAttributeEditor {
	
	public Composite createForm(FormToolkit toolkit, Composite parent, final IDOMElement element) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(ClickUtils.createGridLayout(2));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text textName = AttributeEditorUtils.createText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.headers.name"),
				ClickPlugin.ATTR_NAME);
		textName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textName.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_NAME);
				} else {
					element.setAttribute(ClickPlugin.ATTR_NAME, textName.getText());
				}
			}
		});
		
		final Text textValue = AttributeEditorUtils.createText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.headers.value"), 
				ClickPlugin.ATTR_VALUE);
		textValue.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textValue.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_VALUE);
				} else {
					element.setAttribute(ClickPlugin.ATTR_VALUE, textValue.getText());
				}
			}
		});
		
		final Combo combo = AttributeEditorUtils.createCombo(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.headers.type"), 
				ClickPlugin.ATTR_TYPE, 
				ClickUtils.createComboValues(ClickPlugin.HEADER_TYPE_VALUES));
		combo.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				if(combo.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_TYPE);
				} else {
					element.setAttribute(ClickPlugin.ATTR_TYPE, combo.getText());
				}
			}
		});
		
		return composite;
	}

}
