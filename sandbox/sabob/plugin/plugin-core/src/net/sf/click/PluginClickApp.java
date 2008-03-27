/*
 * Copyright 2004-2008 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Bob Schellink
 */
class PluginClickApp extends ClickApp {

    private Map plugins = new LinkedHashMap();

    public void init(ClickLogger clickLogger) throws Exception {
        ClickLogger.setInstance(clickLogger);

        // Load plugins first so ClickApp can process them
        loadPlugins();
        super.init(clickLogger);
        for (Iterator it = plugins.values().iterator(); it.hasNext(); ) {
            ClickPlugin clickPlugin = (ClickPlugin) it.next();
            clickPlugin.onInit();
        }
    }

    Map getPlugins() {
        return plugins;
    }

    void loadPlugins() {
        InputStream inputStream = null;
        try {
            //Find all jars under WEB-INF/lib
            Map webJars = PluginUtils.findAllJars(getServletContext());

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            
            // Find all click-plugin.properties files on the classpath.
            // Load each properties file, get the plugin "class" property and
            // instantiate the plugin.
            Enumeration en = classLoader.getResources("click-plugin.properties");
            while (en.hasMoreElements()) {
                URL url = (URL) en.nextElement();
                inputStream = url.openStream();

                // Load the properties into Properties object.
                Properties properties = new Properties();
                properties.load(inputStream);

                // if url is: jar:file:/C:/dev/os/click/click-plugin/test/web/WEB-INF/lib/click-plugin-example1.jar!/click-plugin.properties
                // jarName would be: click-plugin-example1.jar 
                String jarName = PluginUtils.extractJarNameFromURL(url);
                
                // Ensure the click-plugin.properties is in one of the 
                // WEB-INF/lib jars.
                if (webJars.containsKey(jarName)) {
                    String pluginClassName = properties.getProperty("class");
                    ClickPlugin plugin = createPlugin(pluginClassName);
                    plugins.put(plugin.getPluginName(), plugin);

                    // Deploy plugin resources.
                    String jarLocation = (String) webJars.get(jarName);
                    InputStream is = getServletContext().getResourceAsStream(jarLocation);
                    deployPluginResources(is, jarLocation, plugin);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ClickUtils.close(inputStream);
        }
    }

    public void deployPluginResources(InputStream jarInputStream, String jarLocation, ClickPlugin plugin) {
        try {
            if (jarInputStream == null) {
                jarInputStream = new FileInputStream(jarLocation);
            }
            JarInputStream jis = new JarInputStream(jarInputStream);
            JarEntry jarEntry = null;
            while ((jarEntry = jis.getNextJarEntry()) != null) {
                String jarEntryName = jarEntry.getName();

                // Deploy all resources under "META-INF/web/" or "META-INF/webapp/"
                int pathIndex = jarEntryName.indexOf("META-INF/web/");
                if (pathIndex == 0) {
                    pathIndex += "META-INF/web/".length();
                } else {
                    pathIndex = jarEntryName.indexOf("META-INF/webapp/");
                    if (pathIndex == 0) {
                        pathIndex += "META-INF/webapp/".length();
                    }
                }

                if (pathIndex != -1) {
                    String resourceName = jarEntryName.substring(pathIndex);
                    String path = plugin.getPluginPath();
                    if (resourceName.startsWith(plugin.getPluginPackageAsPath())) {
                        String pagePath = resourceName.substring(plugin.getPluginPackageAsPath().length());
                        pagePath = pagePath.indexOf('/') == 0 ? pagePath.substring(1) : pagePath;

                        // Add trailing slash '/'
                        path = path.endsWith("/") ? path : path + "/";

                        int index = pagePath.lastIndexOf('/');
                        if (index != -1) {
                            path += pagePath.substring(0, index);
                        }
                    } else {
                        // Add trailing slash '/'
                        path = path.endsWith("/") ? path : path + "/";

                        int index = resourceName.lastIndexOf('/');
                        if (index != -1) {
                            path += resourceName.substring(0, index);
                        }
                    }

                    // Copy resources to web folder
                    ClickUtils.deployFile(getServletContext(),
                      jarEntryName,
                      path);
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to load jar file '" + jarLocation + "'");
        }
    }

    ClickPlugin createPlugin(String pluginClassName) {
        try {
            Class pluginClass = ClickUtils.classForName(pluginClassName);
            if (!(ClickPlugin.class.isAssignableFrom(pluginClass))) {
                throw new IllegalArgumentException(pluginClassName + " must be "
                    + "a subclass of net.sf.click.ClickPlugin.");
            }

            ClickPlugin clickPlugin = (ClickPlugin) pluginClass.newInstance();
            validateCommonPluginErrors(clickPlugin);

            // TODO check if plugin has been overridden in click.xml.
            //if (clickPlugin.getPluginName().equals(TODO)) {
               // clickPlugin = TODO 
            //}

            return clickPlugin;
        } catch (Exception iae) {
            throw new RuntimeException(iae);
        }
    }

    void validateCommonPluginErrors(ClickPlugin plugin) {
        // Ensure pluginPath must starts with a '/' character
        if (plugin.getPluginPath().indexOf('/') != 0) {
            throw new IllegalStateException("Plugin [" + plugin.getPluginName()
                + "] path must start with a '/' character. Plugin path current "
                + "value is [" + plugin.getPluginPath() + "]");
        }
    }

    /**
     * Note: need to override getPageClass and check not only in the default
     * package, but also every plugin and their respective package.
     */
    Class getPageClass(String pagePath, String packageName) {
        // First check if default packageName and pagePath maps to a class
        // in the web application
        Class cls = super.getPageClass(pagePath, packageName);
        if (cls == null) {
            // Otherwise check the available plugins for a class that can be
            // mapped to the pagePath
            for (Iterator it = plugins.values().iterator(); it.hasNext(); ) {
                ClickPlugin plugin = (ClickPlugin) it.next();
                String pluginPagePath = pagePath;
                if (StringUtils.isNotBlank(plugin.getPluginPath())) {
                    if (!pagePath.startsWith(plugin.getPluginPath())) {
                        // The plugin templates lives in a different webPath
                        // than the specified pagePath
                        continue;
                    }
                    pluginPagePath = pluginPagePath.substring(plugin.getPluginPath().length());
                }

                cls = getPageClass(pluginPagePath, plugin.getPluginPackage());
                if (cls != null) {
                    break;
                }
            }
        }
        return cls;
    }
}
