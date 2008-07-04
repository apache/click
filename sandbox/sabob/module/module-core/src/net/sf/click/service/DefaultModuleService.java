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

    private Map modules = new LinkedHashMap();

    private Set loaded = new HashSet();

    public void onInit(ConfigService configService) {
    }

    public void onDestroy(ConfigService configService) {
    }

    public void onDeploy(ConfigService configService, String moduleName) {
    }

    public void loadModule(ConfigService configService, String moduleName) {
    }

    public void deployModules(ConfigService configService, String moduleName) {
    }

    public Map getModules() {
        return modules;
    }

    public void loadModules(ConfigService configService) throws Exception {
        InputStream inputStream = null;
        try {
            //Find all jars under WEB-INF/lib
            Map warJars = ModuleUtils.findWarJars(configService.getServletContext());

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Find all click-module.properties files on the classpath.
            // Load each properties file, get the module "class" property and
            // instantiate the module.
            Enumeration en = classLoader.getResources("click-module.properties");
            while (en.hasMoreElements()) {
                URL url = (URL) en.nextElement();
                inputStream = url.openStream();

                // Load the properties into Properties object.
                Properties properties = new Properties();
                properties.load(inputStream);

                // if url is: jar:file:/C:/dev/os/click/click-module/test/web/WEB-INF/lib/click-module-example1.jar!/click-module.properties
                // jarName would be: click-module-example1.jar
                String jarName = ModuleUtils.extractJarNameFromURL(url);

                // Ensure the click-module.properties is in one of the
                // WEB-INF/lib jars.
                if (warJars.containsKey(jarName)) {
                    String moduleClassName = properties.getProperty("class");
                    ClickModule module = createModule(moduleClassName);
                    modules.put(module.getModuleName(), module);

                    // Deploy module resources.
                    String jarLocation = (String) warJars.get(jarName);
                    InputStream is = configService.getServletContext()
                        .getResourceAsStream(jarLocation);
                    deployModuleResources(configService, is, jarLocation, module);
                }
            }
        } finally {
            ClickUtils.close(inputStream);
        }
    }

    public void deployModuleResources(ConfigService configService,
        InputStream jarInputStream, String jarLocation, ClickModule module) {
        try {
            if (jarInputStream == null) {
                jarInputStream = new FileInputStream(jarLocation);
            }
            JarInputStream jis = new JarInputStream(jarInputStream);
            JarEntry jarEntry = null;
            while ((jarEntry = jis.getNextJarEntry()) != null) {
                String jarEntryName = jarEntry.getName();

                // Deploy all resources under "META-INF/web/"
                int pathIndex = jarEntryName.indexOf("META-INF/web/");
                if (pathIndex == 0) {
                    pathIndex += "META-INF/web/".length();
                }

                if (pathIndex != -1) {
                    String resourceName = jarEntryName.substring(pathIndex);

                    // example -> /module1
                    String path = module.getModulePath();

                    // resourceName -> /com/corp/pages/customers/customer.htm
                    if (resourceName.startsWith(module.getModulePackageAsPath())) {
                        // pagePath -> /customers/customer.htm
                        String pagePath = resourceName.substring(module.
                            getModulePackageAsPath().length());

                        // pagePath -> customers/customer.htm
                        pagePath = pagePath.indexOf('/') == 0 ? pagePath.
                            substring(1) : pagePath;

                        // Add trailing slash '/module1/'
                        path = path.endsWith("/") ? path : path + "/";

                        int index = pagePath.lastIndexOf('/');
                        if (index != -1) {
                            // path -> /module1/customers
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

    ClickModule createModule(String moduleClassName) {
        try {
            Class moduleClass = ClickUtils.classForName(moduleClassName);
            if (!(ClickModule.class.isAssignableFrom(moduleClass))) {
                throw new IllegalArgumentException(moduleClassName + " must be " +
                    "a subclass of net.sf.click.ClickModule.");
            }

            ClickModule clickModule = (ClickModule) moduleClass.newInstance();
            validateCommonModuleErrors(clickModule);

            // TODO check if module has been overridden in click.xml.
            //if (clickModule.getModuleName().equals(TODO)) {
            // clickModule = TODO 
            //}

            return clickModule;
        } catch (Exception iae) {
            throw new RuntimeException(iae);
        }
    }

    void validateCommonModuleErrors(ClickModule module) {
        // Ensure modulePath must starts with a '/' character
        if (module.getModulePath().indexOf('/') != 0) {
            throw new IllegalStateException("Module [" + module.getModuleName() +
                "] path must start with a '/' character. Module path current " +
                "value is [" + module.getModulePath() + "]");
        }
    }
}
