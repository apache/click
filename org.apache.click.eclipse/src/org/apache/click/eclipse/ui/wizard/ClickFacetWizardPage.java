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

import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ClickUtils;
import org.apache.click.eclipse.core.facet.ClickFacetInstallDataModelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;

/**
 *
 * @author Naoki Takezoe
 * @since 2.0.0
 */
public class ClickFacetWizardPage extends AbstractFacetWizardPage {

	private IDataModel model;
	private Text rootPackage;
	private Button useSpring;
	private Button useCayenne;
	private Button usePerformanceFilter;

	public ClickFacetWizardPage() {
		super("ClickFacetWizardPage");
		setTitle(ClickPlugin.getString("wizard.facet.title"));
		setDescription(ClickPlugin.getString("wizard.facet.description"));
	}

	public void setConfig(Object config) {
		this.model = (IDataModel) config;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		ClickUtils.createLabel(composite, ClickPlugin.getString("wizard.facet.rootPackage"));
		rootPackage = new Text(composite, SWT.BORDER);
		rootPackage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		rootPackage.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				doValidate();
//			}
//		});

		usePerformanceFilter = new Button(composite, SWT.CHECK);
		usePerformanceFilter.setText(ClickPlugin.getString("wizard.facet.usePerformanceFilter"));
		usePerformanceFilter.setLayoutData(ClickUtils.createGridData(2, GridData.FILL_HORIZONTAL));

		useSpring = new Button(composite, SWT.CHECK);
		useSpring.setText(ClickPlugin.getString("wizard.facet.useSpring"));
		useSpring.setLayoutData(ClickUtils.createGridData(2, GridData.FILL_HORIZONTAL));

		useCayenne = new Button(composite, SWT.CHECK);
		useCayenne.setText(ClickPlugin.getString("wizard.facet.useCayenne"));
		useCayenne.setLayoutData(ClickUtils.createGridData(2, GridData.FILL_HORIZONTAL));

		setControl(composite);
//		doValidate();
	}

//	private void doValidate(){
//		if(rootPackage.getText().length() == 0){
//			setErrorMessage(ClickPlugin.getString("wizard.facet.error.rootPackage.empty"));
//			setPageComplete(false);
//		} else {
//			setErrorMessage(null);
//			setPageComplete(true);
//		}
//	}

	public void transferStateToConfig() {
		model.setBooleanProperty(ClickFacetInstallDataModelProvider.USE_PERFORMANCE_FILTER,
				usePerformanceFilter.getSelection());
		model.setBooleanProperty(ClickFacetInstallDataModelProvider.USE_SPRING,
				useSpring.getSelection());
		model.setBooleanProperty(ClickFacetInstallDataModelProvider.USE_CAYENNE,
				useCayenne.getSelection());
		model.setStringProperty(ClickFacetInstallDataModelProvider.ROOT_PACKAGE,
				rootPackage.getText());
	}
}
