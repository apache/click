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


import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ui.editor.actions.ElementRemoveAction;
import org.apache.click.eclipse.ui.editor.attrs.IAttributeEditor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

public abstract class AbstractMasterDetailEditor extends AbstractFormEditor {

	protected SashForm sash;
	protected TreeViewer viewer;
	protected Composite currentEditor;

	protected MenuManager menu;
	protected MenuManager newMenu;
	protected ElementRemoveAction deleteAction = new ElementRemoveAction();

	protected abstract String[] getAcceptElementNames();

	protected abstract IAttributeEditor getAttributeEditor(String elementName);

	protected abstract void createMenu(IDOMElement element);

	public void initModel(IStructuredModel model) {
		sash = new SashForm(form.getBody(), SWT.HORIZONTAL);
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		sash.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		final Composite left = toolkit.createComposite(sash);
		left.setLayoutData(new GridData(GridData.FILL_BOTH));
		left.setLayout(new FillLayout());

		Section headerSection = toolkit.createSection(left, Section.TITLE_BAR);
		headerSection.setText(ClickPlugin.getString("editor.clickXML.outline"));

		Composite detailComposite = toolkit.createComposite(sash);
		detailComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		detailComposite.setLayout(new FillLayout());

		Section detailSection = toolkit.createSection(detailComposite, Section.TITLE_BAR);
		detailSection.setText(ClickPlugin.getString("editor.clickXML.details"));

		final Composite right = toolkit.createComposite(detailSection);
		right.setLayout(new FillLayout());
		detailSection.setClient(right);

		Tree tree = toolkit.createTree(headerSection, SWT.NULL);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		headerSection.setClient(tree);

		List<String> acceptElements = new ArrayList<String>();
		String[] acceptElementNames = getAcceptElementNames();
		for(int i=0;i<acceptElementNames.length;i++){
			acceptElements.add(acceptElementNames[i]);
		}

		viewer = new TreeViewer(tree);
		viewer.setContentProvider(new ClickTreeContentProvider(acceptElements));
		viewer.setLabelProvider(new ClickTreeLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event){
				if(currentEditor!=null){
					currentEditor.dispose();
				}
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				Object obj = selection.getFirstElement();
				if(obj != null){
					if(obj instanceof IDOMElement){
						IAttributeEditor editor = getAttributeEditor(((IDOMElement)obj).getNodeName());
						if(editor != null){
							currentEditor = editor.createForm(toolkit, right, (IDOMElement)obj);
						}
					}
				}
				right.layout();
			}
		});

		viewer.setInput(model);
		viewer.expandAll();

		menu = new MenuManager();
		newMenu = new MenuManager(ClickPlugin.getString("action.new"));
		menu.add(newMenu);
		menu.add(new Separator());
		menu.add(deleteAction);
		tree.setMenu(menu.createContextMenu(tree));
		tree.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				updateMenu();
			}
		});
		tree.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.DEL && deleteAction.isEnabled()){
					deleteAction.run();
				}
			}
		});

		toolkit.paintBordersFor(left);
	}

	public void updateMenu(){
		newMenu.removeAll();

		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		Object obj = selection.getFirstElement();

		if(obj instanceof IDOMElement){
			IDOMElement element = (IDOMElement)obj;

			if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP)){
				deleteAction.setEnabled(false);
			} else {
				deleteAction.setEnabled(true);
				deleteAction.setElement(element);
			}

			createMenu(element);
		}
	}

	public void modelUpdated(IStructuredModel model) {
		viewer.setSelection(null);
		viewer.refresh();
	}

	public void setFocus() {
		form.setFocus();
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if(adapter.equals(TreeViewer.class)){
			return this.viewer;
		}
		return super.getAdapter(adapter);
	}

}
