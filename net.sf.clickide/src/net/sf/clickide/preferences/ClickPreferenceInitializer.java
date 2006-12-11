package net.sf.clickide.preferences;

import java.io.IOException;
import java.io.InputStream;

import net.sf.clickide.ClickPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * Initializes the preference store.
 * 
 * @author Naoki Takezoe
 */
public class ClickPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = ClickPlugin.getDefault().getPreferenceStore();
		try {
			store.setDefault(ClickPlugin.PREF_TEMPLATES, getResourceAsText("default_templates.xml"));
			store.setDefault(ClickPlugin.PREF_COLOR_VAR, StringConverter.asString(new RGB(128,0,0)));
			store.setDefault(ClickPlugin.PREF_COLOR_DIR, StringConverter.asString(new RGB(0,0,128)));
			store.setDefault(ClickPlugin.PREF_COLOR_CMT, StringConverter.asString(new RGB(0,128,0)));
		} catch(Exception ex){
			ClickPlugin.log(ex);
		}
	}
	
	private static String getResourceAsText(String resource) throws IOException {
		InputStream in = null;
		try {
			in = ClickPreferenceInitializer.class.getResourceAsStream(resource);
			byte[] buf = new byte[in.available()];
			in.read(buf);
			return new String(buf);
		} finally {
			if(in!=null){
				in.close();
			}
		}
	}

}
