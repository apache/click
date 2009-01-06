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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class ExcludesAttributeEditor implements IAttributeEditor {

	public Composite createForm(FormToolkit toolkit, Composite parent, final IDOMElement element) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(ClickUtils.createGridLayout(2));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text textName = AttributeEditorUtils.createText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.pages.excludePattern"),
				ClickPlugin.ATTR_PATTERN);
		textName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textName.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_PATTERN);
				} else {
					element.setAttribute(ClickPlugin.ATTR_PATTERN, textName.getText());
				}
			}
		});
		
		return composite;
	}

}
