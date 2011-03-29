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
package org.apache.click.eclipse;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import org.apache.click.eclipse.core.config.DefaultClickConfigurationProvider;
import org.apache.click.eclipse.core.config.IClickConfigurationProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
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

	public static final String PLUGIN_ID = "org.apache.click.eclipse";

	//The shared instance.
	private static ClickPlugin plugin;
	private ResourceBundle resource;
	private ColorManager colorManager;

	private List<IClickConfigurationProvider>
		configProviders = new ArrayList<IClickConfigurationProvider>();

	public static final String CLICK_PAGE_CLASS = "org.apache.click.Page";
	public static final String CLICK_CONTROL_IF = "org.apache.click.Control";
	public static final String CLICK_PAGE_INTERCEPTOR_IF = "org.apache.click.PageInterceptor";

	public static final String TAG_CLICK_APP = "click-app";
	public static final String TAG_HEADERS = "headers";
	public static final String TAG_HEADER = "header";
	public static final String TAG_PAGES = "pages";
	public static final String TAG_PAGE = "page";
	public static final String TAG_CONTROLS = "controls";
	public static final String TAG_CONTROL = "control";
	public static final String TAG_CONTROL_SET = "control-set";
	public static final String TAG_FORMAT = "format";
	public static final String TAG_MODE = "mode";
	public static final String TAG_EXCLUDES = "excludes";
	public static final String TAG_PROPERTY = "property";
	public static final String TAG_FILE_UPLOAD_SERVICE = "file-upload-service";
	public static final String TAG_LOG_SERVICE = "log-service";
	public static final String TAG_TEMPLATE_SERVICE = "template-service";
	public static final String TAG_RESOURCE_SERVICE = "resource-service";
	public static final String TAG_PAGE_INTERCEPTOR = "page-interceptor";
	public static final String ATTR_CHARSET = "charset";
	public static final String ATTR_LOCALE = "locale";
	public static final String ATTR_CLASSNAME = "classname";
	public static final String ATTR_TYPE = "type";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_VALUE = "value";
	public static final String ATTR_PATH = "path";
	public static final String ATTR_AUTO_MAPPING = "automapping";
	public static final String ATTR_AUTO_BINDING = "autobinding";
	public static final String ATTR_PACKAGE = "package";
	public static final String ATTR_PATTERN = "pattern";
	public static final String ATTR_SCOPE = "scope";

	public static final String[] BOOLEAN_VALUES = {"true", "false"};
	public static final String[] AUTO_BINDING_VALUES = {"default", "annotation", "none"};
	public static final String[] LOGTO_VALUES = {"console", "servlet"};
	public static final String[] MODE_VALUES = {"production", "profile", "development", "debug", "trace"};
	public static final String[] HEADER_TYPE_VALUES = {"String", "Integer", "Date"};
	public static final String[] SCOPE_VALUES = {"application", "request"};

	public static final String PREF_TEMPLATES = "click.templates";
	public static final String PREF_COLOR_VAR = "click.color.variable";
	public static final String PREF_COLOR_DIR = "click.color.directive";
	public static final String PREF_COLOR_CMT = "click.color.comment";
	public static final String PREF_VELOCITY_VARS = "click.velocity.variables";

	/**
	 * The constructor.
	 */
	public ClickPlugin() {
		plugin = this;
		resource = ResourceBundle.getBundle("org.apache.click.eclipse.ClickPlugin");

		configProviders.addAll(loadContributedClasses(
				"configurationProvider", "configurationProvider"));
		configProviders.add(new DefaultClickConfigurationProvider());
	}

	public ResourceBundle getResourceBundle(){
		return this.resource;
	}

	private static List<IClickConfigurationProvider> loadContributedClasses(String extPointId, String elementName){
		List<IClickConfigurationProvider> result = new ArrayList<IClickConfigurationProvider>();
		try {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(PLUGIN_ID + "." + extPointId);
			IExtension[] extensions = point.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if (elementName.equals(elements[j].getName())) {
						result.add((IClickConfigurationProvider) elements[j].createExecutableExtension("class"));
					}
				}
			}
		} catch (Exception ex) {
			log(ex);
		}
		return result;
	}

	public ColorManager getColorManager(){
		if(this.colorManager==null){
			this.colorManager = new ColorManager();
		}
		return this.colorManager;
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
	public static String getString(String key, Object... substitutions){
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
		colorManager.dispose();
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
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
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

	public IClickConfigurationProvider getConfigurationProvider(IProject project){
		for(int i=0;i<configProviders.size();i++){
			IClickConfigurationProvider configProvider
				= (IClickConfigurationProvider) configProviders.get(i);
			if(configProvider.isSupportedProject(project)){
				return configProvider;
			}
		}
		throw new RuntimeException("Can not find the configuration provider!");
	}
}
