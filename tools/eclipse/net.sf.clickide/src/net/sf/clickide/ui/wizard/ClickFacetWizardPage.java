package net.sf.clickide.ui.wizard;

import net.sf.clickide.core.facet.ClickFacetInstallDataModelProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;

/**
 * 
 * @author Naoki Takezoe
 * @since 2.0.0
 */
public class ClickFacetWizardPage extends AbstractFacetWizardPage {
	
	private IDataModel model;
	private Button useSpring;
	private Button useCayenne;
	
	public ClickFacetWizardPage() {
		super("ClickFacetWizardPage");
		setTitle("Options");
		setDescription("Select additinal features for the Click project.");
	}

	public void setConfig(Object config) {
		this.model = (IDataModel) config;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		useSpring = new Button(composite, SWT.CHECK);
		useSpring.setText("Use Spring Framework");
		
		useCayenne = new Button(composite, SWT.CHECK);
		useCayenne.setText("Use Apache Cayenne");
		
		setControl(composite);
	}

	public void transferStateToConfig() {
		model.setBooleanProperty(ClickFacetInstallDataModelProvider.USE_SPRING, 
				useSpring.getSelection());
		model.setBooleanProperty(ClickFacetInstallDataModelProvider.USE_CAYENNE, 
				useCayenne.getSelection());
	}
}
