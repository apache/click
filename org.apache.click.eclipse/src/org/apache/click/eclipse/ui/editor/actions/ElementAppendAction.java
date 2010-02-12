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
package org.apache.click.eclipse.ui.editor.actions;


import org.apache.click.eclipse.ui.editor.forms.AbstractFormEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Element;

/**
 * <code>IAction</code> which appends the <code>IDOMElement</code>
 * to the specified element.
 * 
 * @author Naoki Takezoe
 */
public class ElementAppendAction extends Action {
	
	private IDOMElement parentElement;
	private IDOMElement refElement;
	private String newElement;
	private AbstractFormEditor editor;
	
	public ElementAppendAction(String name, IDOMElement parentElement, 
			IDOMElement refElement, AbstractFormEditor editor){
		
		super(name);
		this.parentElement = parentElement;
		this.refElement = refElement;
		this.newElement = name;
		this.editor = editor;
	}
	
	public void run(){
		Element element = parentElement.getOwnerDocument().createElement(newElement);
		
		if(refElement!=null){
			parentElement.insertBefore(element, refElement);
		} else {
			parentElement.appendChild(element);
		}
		
		TreeViewer viewer = (TreeViewer)editor.getAdapter(TreeViewer.class);
		if(viewer!=null){
			viewer.setSelection(new StructuredSelection(element));
			viewer.refresh();
		}
		
		editor.updateMenu();
	}
	
}
