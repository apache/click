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
import org.apache.click.eclipse.ui.wizard.NewClickPageWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * The implementation of <code>IElementEditor</code> for &lt;page&gt;.
 * 
 * @author Naoki Takezoe
 */
public class PageAttributeEditor implements IAttributeEditor {
	
	public Composite createForm(FormToolkit toolkit, Composite parent, final IDOMElement element) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(ClickUtils.createGridLayout(2));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Control[] controls = AttributeEditorUtils.createLinkText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.pages.path"),
				ClickPlugin.ATTR_PATH);
		final Hyperlink linkPath = (Hyperlink)controls[0];
		final Text textPath = (Text)controls[1];
		textPath.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textPath.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_PATH);
				} else {
					element.setAttribute(ClickPlugin.ATTR_PATH, textPath.getText());
				}
			}
		});
		
		IFile file = (IFile)ClickUtils.getResource(element.getStructuredDocument());
		IJavaProject project = JavaCore.create(file.getProject());
		
		final Text textClass = AttributeEditorUtils.createClassText(
				project, toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.pages.class"), 
				ClickPlugin.ATTR_CLASSNAME,
				ClickPlugin.CLICK_PAGE_CLASS, textPath);
		textClass.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textClass.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_CLASSNAME);
				} else {
					element.setAttribute(ClickPlugin.ATTR_CLASSNAME, textClass.getText());
				}
			}
		});
		
		linkPath.addHyperlinkListener(new HyperlinkAdapter(){
			public void linkActivated(HyperlinkEvent e){
				IFile file = (IFile)ClickUtils.getResource(element.getStructuredDocument());
				IProject project = file.getProject();
				String root = ClickUtils.getWebAppRootFolder(project);
				try {
					IFile targetFile = project.getFile(new Path(root).append(textPath.getText()));
					if(targetFile.exists()){
						IDE.openEditor(ClickUtils.getActivePage(), targetFile);
						return;
					}
				} catch(Exception ex){
				}
				
				NewClickPageWizard wizard = new NewClickPageWizard();
				wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(project));
				wizard.setInitialPageName(textPath.getText());
				wizard.setInitialClassName(textClass.getText());
				WizardDialog dialog = new WizardDialog(textPath.getShell(), wizard);
				dialog.open();
			}
		});
		
		
		return composite;
	}

}
