package net.sf.clickide;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Manages SWT's <code>Color</code> objects which are configued by the preference page.
 * 
 * @author Naoki Takezoe
 */
public class ColorManager {
	
	private IPreferenceStore store;
	private Map colors = new HashMap();
	
	private IPropertyChangeListener listener = new IPropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent event) {
			updateColors();
		}
	};
	
	public ColorManager(){
		store = ClickPlugin.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(listener);
		updateColors();
	}
	
	/**
	 * Returns the cached <code>Color<code> object.
	 * 
	 * @param key the preference key defined as the static variable in <code>ClickPlugin</code>
	 * @return the <code>Color</code> object
	 */
	public Color get(String key){
		return (Color)colors.get(key);
	}
	
	private void updateColors(){
		colors.put(ClickPlugin.PREF_COLOR_VAR, new Color(Display.getDefault(), 
				StringConverter.asRGB(store.getString(ClickPlugin.PREF_COLOR_VAR))));
		colors.put(ClickPlugin.PREF_COLOR_DIR, new Color(Display.getDefault(), 
				StringConverter.asRGB(store.getString(ClickPlugin.PREF_COLOR_DIR))));
		colors.put(ClickPlugin.PREF_COLOR_CMT, new Color(Display.getDefault(), 
				StringConverter.asRGB(store.getString(ClickPlugin.PREF_COLOR_CMT))));
	}
	
	private void disposeColors(){
		for(Iterator i = colors.values().iterator(); i.hasNext();){
			Color color = (Color)i.next();
			color.dispose();
		}
		colors.clear();
	}
	
	/**
	 * Disposes all <code>Color</code> objects.
	 */
	public void dispose(){
		disposeColors();
		store.removePropertyChangeListener(listener);
	}
	
}
