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


import org.apache.click.eclipse.ClickPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * <code>IAction</code> which removes the <code>IDOMElement</code> 
 * from the parent element.
 * 
 * @author Naoki Takezoe
 */
public class ElementRemoveAction extends Action{
	
	private IDOMElement element;
	
	public ElementRemoveAction(){
		super(ClickPlugin.getString("action.delete"));
	}
	
	public void setElement(IDOMElement element){
		this.element = element;
	}
	
	public void run(){
		element.getParentNode().removeChild(element);
	}
	
}
