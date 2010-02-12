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
package org.apache.click.eclipse.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;

/**
 * 
 * @author Naoki Takezoe
 */
public class NewClassWizard extends NewClassCreationWizard {

	private String superClass;
	private String className;
	private List<String> implement;
	
	public NewClassWizard() {
		super();
	}
	
	public void setSuperClass(String superClass){
		this.superClass = superClass;
	}
	
	public void setClassName(String className){
		this.className = className;
	}
	
	public void addInterface(String implement){
		if(this.implement==null){
			this.implement = new ArrayList<String>();
		}
		this.implement.add(implement);
	}
	
	public void addPages() {
		super.addPages();
		NewClassWizardPage fPage = (NewClassWizardPage)getPages()[0];
		if(superClass!=null){
			fPage.setSuperClass(superClass, true);
		}
		if(implement!=null){
			fPage.setSuperInterfaces(implement, true);
		}
		if(className!=null){
			int index = className.lastIndexOf('.');
			if(index!=-1){
				String packageName = className.substring(0,index);
				className = className.substring(index+1,className.length());
				IPackageFragment pack = fPage.getPackageFragmentRoot().getPackageFragment(packageName);
				fPage.setPackageFragment(pack,true);
			}
			fPage.setTypeName(className,true);
		}
	}
}
