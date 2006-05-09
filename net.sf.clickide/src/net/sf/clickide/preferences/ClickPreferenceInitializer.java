package net.sf.clickide.preferences;

import java.io.IOException;
import java.io.InputStream;

import net.sf.clickide.ClickPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

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
