package net.sf.clickide.core.facet;

import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickFacetInstallDataModelProvider extends FacetInstallDataModelProvider {
	
	public static final String USE_SPRING = "useSpring";
	public static final String USE_CAYENNE = "useCayenne";
	
	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(USE_SPRING);
		names.add(USE_CAYENNE);
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(FACET_ID)) {
			return "click";
		} else if(propertyName.equals(USE_SPRING)){
			return "false";
		} else if(propertyName.equals(USE_CAYENNE)){
			return "false";
		}
		return super.getDefaultProperty(propertyName);
	}
	
}
