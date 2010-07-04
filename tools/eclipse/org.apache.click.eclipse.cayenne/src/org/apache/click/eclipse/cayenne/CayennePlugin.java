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
package org.apache.click.eclipse.cayenne;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CayennePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.apache.click.eclipse.cayenne";

	// The shared instance
	private static CayennePlugin plugin;

	private ResourceBundle resource;

	/**
	 * The constructor
	 */
	public CayennePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		resource = ResourceBundle.getBundle("org.apache.click.eclipse.cayenne.messages");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CayennePlugin getDefault() {
		return plugin;
	}

	public static String getString(String key){
		return getDefault().resource.getString(key);
	}

	/**
	 * JAR files for Cayenne Modeler.
	 */
	private static File[] files;

	/**
	 *
	 * @return
	 */
	public static File[] getCayenneModelerClassPaths(){
		if(files!=null){
			return files;
		}
		File dir = CayennePlugin.getDefault().getStateLocation().toFile();
		files = new File[]{
//				new File(dir, "cayenne.jar"),
//				new File(dir, "cayenne-modeler.jar"),
//				new File(dir, "commons-dbcp-1.2.1.jar"),
//				new File(dir, "commons-pool-1.2.jar"),
//				new File(dir, "forms-1.0.3.jar"),
//				new File(dir, "hsqldb-1.8.0.2.jar"),
//				new File(dir, "looks-1.3.1.jar"),
//				new File(dir, "ognl-2.6.7.jar"),
//				new File(dir, "scope-bin-1.0.1.jar"),
				new File(dir, "cayenne-modeler-3.0.jar"),
		};
		return files;
	}

	/**
	 *
	 * @return
	 */
	public static String[] getCayenneModelerClassPathAsStringArray(){
		File[] files = getCayenneModelerClassPaths();
		String[] paths = new String[files.length];
		for(int i=0;i<files.length;i++){
			paths[i] = files[i].getAbsolutePath();
		}
		return paths;
	}

	/**
	 *
	 * @throws CoreException
	 */
	public static void copyCayenneModelerLibraries() throws CoreException {
		File[] files = getCayenneModelerClassPaths();
		for(int i=0;i<files.length;i++){
			if(!files[i].exists()){
				copyFile(CayennePlugin.getDefault().getBundle().getEntry(
						"/cayenne/" + files[i].getName()), files[i]);
			}
		}
	}

//	/**
//	 *
//	 * @throws CoreException
//	 */
//	public static void removeCayenneModelerLibraries() throws CoreException {
//		File[] files = getCayenneModelerClassPaths();
//		for(int i=0;i<files.length;i++){
//			if(files[i].exists()){
//				files[i].delete();
//			}
//		}
//	}

	private static void copyFile(URL url, File file) throws CoreException {
		try {
			InputStream in = url.openStream();
			OutputStream out = new FileOutputStream(file);
			try {
				byte[] buf = new byte[1024 * 8];
				int length = 0;
				while((length = in.read(buf))!=-1){
					out.write(buf, 0, length);
				}
			} finally {
				in.close();
				out.close();
			}
		} catch(Exception ex){
			IStatus status = new Status(
					IStatus.ERROR, CayennePlugin.PLUGIN_ID, 0, ex.toString(), ex);
			throw new CoreException(status);
		}
	}

}
