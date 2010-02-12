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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickTreeContentProvider extends JFaceNodeContentProvider {
	
	private List<String> accept;
	
	public ClickTreeContentProvider(List<String> accept){
		this.accept = accept;
	}
	
	public Object[] getChildren(Object object) {
		List<Object> result = new ArrayList<Object>();
		Object[] children =  super.getChildren(object);
		for(int i=0;i<children.length;i++){
			if(children[i] instanceof IDOMElement){
				String name = ((IDOMElement)children[i]).getNodeName();
				if(this.accept.contains(name)){
					result.add(children[i]);
				}
			}
		}
		return result.toArray();
	}

	public Object[] getElements(Object object) {
		Object[] obj = super.getElements(object);
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof IDOMElement){
				return new Object[]{obj[i]};
			}
		}
		return null;
	}
}
