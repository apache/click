package net.sf.clickide;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Naoki Takezoe
 */
public class ClickPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static ClickPlugin plugin;
	private ResourceBundle resource;
	
	public static final String CLICK_PAGE_CLASS = "net.sf.click.Page";
	public static final String CLICK_CONTROL_IF = "net.sf.click.Control";
	
	public static final String TAG_CLICK_APP = "click-app";
	public static final String TAG_HEADERS = "headers";
	public static final String TAG_HEADER = "header";
	public static final String TAG_PAGES = "pages";
	public static final String TAG_PAGE = "page";
	public static final String TAG_CONTROLS = "controls";
	public static final String TAG_CONTROL = "control";
	public static final String TAG_FORMAT = "format";
	public static final String TAG_MODE = "mode";
	public static final String TAG_EXCLUDES = "excludes";
	public static final String ATTR_CHARSET = "charset";
	public static final String ATTR_LOCALE = "locale";
	public static final String ATTR_CLASSNAME = "classname";
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_PATH = "path";
	public static final String ATTR_AUTO_MAPPING = "automapping";
	public static final String ATTR_PACKAGE = "package";
	public static final String ATTR_LOGTO = "logto";
	public static final String ATTR_PATTERN = "pattern";
	
	public static final String[] AUTO_MAPPING_VALUES = {"true", "false"};
	public static final String[] LOGTO_VALUES = {"console", "servlet"};
	public static final String[] MODE_VALUES = {"production", "profile", "development", "debug", "trace"};
	public static final String[] HEADER_TYPE_VALUES = {"String", "Integer", "Date"};
	
	public static final String PREF_TEMPLATES = "click.templates";
	
	/**
	 * The constructor.
	 */
	public ClickPlugin() {
		plugin = this;
		resource = ResourceBundle.getBundle("net.sf.clickide.ClickPlugin");
	}
	
	/**
	 * Returns the localized message from <tt>ClickPlugin.properties</tt>.
	 * 
	 * @param key the message key
	 * @return the localized message
	 */
	public static String getString(String key){
		return getDefault().resource.getString(key);
	}
	
	/**
	 * Returns the localized message from <tt>ClickPlugin.properties</tt>.
	 * The message would be formatted with given substitutions.
	 * 
	 * @param key the message key
	 * @param substitutions the substitutions
	 * @return the localized and formatted message
	 */
	public static String getString(String key, Object [] substitutions){
		return MessageFormat.format(getString(key), substitutions);
	}
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ClickPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.sf.clickide", path);
	}
	
	/**
	 * Logs the given <code>Throwable</code>.
	 * 
	 * @param t the <code>Throwable</code> instance which would be logged
	 */
	public static void log(Throwable t){
		IStatus status = new Status(
				IStatus.ERROR, getDefault().getBundle().getSymbolicName(),
				IStatus.ERROR, t.toString(), t);
		
		getDefault().getLog().log(status);
		
		try {
			IWorkbenchPage page = ClickUtils.getActivePage();
			MessageDialog.openError(page.getWorkbenchWindow().getShell(),
					ClickPlugin.getString("message.error"), t.toString());
		} catch(Exception ex){
			// ignore
		}
	}
}
