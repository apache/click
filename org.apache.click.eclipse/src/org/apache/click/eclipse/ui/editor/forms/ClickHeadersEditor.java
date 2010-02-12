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
import org.apache.click.eclipse.ClickUtils;
import org.apache.click.eclipse.ui.editor.actions.ElementAppendAction;
import org.apache.click.eclipse.ui.editor.attrs.HeaderAttributeEditor;
import org.apache.click.eclipse.ui.editor.attrs.IAttributeEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickHeadersEditor extends AbstractMasterDetailEditor {
	
	protected void createMenu(IDOMElement element){
		if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP) && 
				ClickUtils.getElement(element, ClickPlugin.TAG_HEADERS)==null){
			IDOMElement[] elements = {
					ClickUtils.getElement(element, ClickPlugin.TAG_FORMAT),
					ClickUtils.getElement(element, ClickPlugin.TAG_MODE),
					ClickUtils.getElement(element, ClickPlugin.TAG_CONTROLS)};
			for(int i=0;i<elements.length;i++){
				if(elements[i]!=null){
					newMenu.add(new ElementAppendAction(ClickPlugin.TAG_HEADERS, element, elements[i], this));
					break;
				}
			}
			if(newMenu.getItems().length==0){
				newMenu.add(new ElementAppendAction(ClickPlugin.TAG_HEADERS, element, null, this));
			}
		}
		if(element.getNodeName().equals(ClickPlugin.TAG_HEADERS)){
			newMenu.add(new ElementAppendAction(ClickPlugin.TAG_HEADER, element, null, this));
		}
	}

	protected String[] getAcceptElementNames() {
		return new String[]{ClickPlugin.TAG_HEADERS, ClickPlugin.TAG_HEADER};
	}

	protected IAttributeEditor getAttributeEditor(String elementName) {
		if(elementName.equals(ClickPlugin.TAG_HEADER)){
			return new HeaderAttributeEditor();
		}
		return null;
	}
	
}
