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
package net.sf.click.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import net.sf.click.ClickModule;
import net.sf.click.ModuleUtils;
import net.sf.click.util.ClickUtils;

/**
 *
 * @author Bob Schellink
 */
public class DefaultModuleService implements ModuleService {

    private Map plugins = new LinkedHashMap();
    
    private Set loaded = new HashSet();
    
    public void onInit(ConfigService configService) {
        
    }
    
    public void onDestroy(ConfigService configService) {
        
    }

    public void onDeploy(ConfigService configService, String pluginName) {
        
    }

    public void loadPlugin(ConfigService configService, String pluginName) {
        
    }

    public void deployPlugin(ConfigService configService, String pluginName) {
        
    }

    public Map getModules() {
        return plugins;
    }

    public void loadPlugins(ConfigService configService) throws Exception {
        InputStream inputStream = null;
        try {
            //Find all jars under WEB-INF/lib
            Map webJars = ModuleUtils.findAllJars(configService.getServletContext());

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
                String jarName = ModuleUtils.extractJarNameFromURL(url);
                
                // Ensure the click-plugin.properties is in one of the 
                // WEB-INF/lib jars.
                if (webJars.containsKey(jarName)) {
                    String pluginClassName = properties.getProperty("class");
                    ClickModule plugin = createPlugin(pluginClassName);
                    plugins.put(plugin.getModuleName(), plugin);

                    // Deploy plugin resources.
                    String jarLocation = (String) webJars.get(jarName);
                    InputStream is = configService.getServletContext()
                        .getResourceAsStream(jarLocation);
                    deployPluginResources(configService, is, jarLocation, plugin);
                }
            }
        } finally {
            ClickUtils.close(inputStream);
        }
    }

    public void deployPluginResources(ConfigService configService, InputStream jarInputStream, String jarLocation, ClickModule plugin) {
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
                    String path = plugin.getModulePath();
                    if (resourceName.startsWith(plugin.getModulePackageAsPath())) {
                        String pagePath = resourceName.substring(plugin.getModulePackageAsPath().length());
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
                    ClickUtils.deployFile(configService.getServletContext(),
                      jarEntryName,
                      path);
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to load jar file '" + jarLocation + "'");
        }
    }

    ClickModule createPlugin(String pluginClassName) {
        try {
            Class pluginClass = ClickUtils.classForName(pluginClassName);
            if (!(ClickModule.class.isAssignableFrom(pluginClass))) {
                throw new IllegalArgumentException(pluginClassName + " must be "
                    + "a subclass of net.sf.click.ClickModule.");
            }

            ClickModule clickModule = (ClickModule) pluginClass.newInstance();
            validateCommonPluginErrors(clickModule);

            // TODO check if plugin has been overridden in click.xml.
            //if (clickModule.getModuleName().equals(TODO)) {
               // clickModule = TODO 
            //}

            return clickModule;
        } catch (Exception iae) {
            throw new RuntimeException(iae);
        }
    }

    void validateCommonPluginErrors(ClickModule plugin) {
        // Ensure pluginPath must starts with a '/' character
        if (plugin.getModulePath().indexOf('/') != 0) {
            throw new IllegalStateException("Plugin [" + plugin.getModuleName()
                + "] path must start with a '/' character. Plugin path current "
                + "value is [" + plugin.getModulePath() + "]");
        }
    }
}
