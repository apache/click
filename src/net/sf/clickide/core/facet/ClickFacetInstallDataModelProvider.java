package net.sf.clickide.core.facet;

import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickFacetInstallDataModelProvider extends FacetInstallDataModelProvider {
	
	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(FACET_ID)) {
			return "click";
		}
		return super.getDefaultProperty(propertyName);
	}
	
}
