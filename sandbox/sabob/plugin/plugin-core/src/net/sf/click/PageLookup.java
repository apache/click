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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import net.sf.click.util.ClickUtils;
import org.apache.commons.lang.StringUtils;

/**
 * TODO REMOVE. This class is redundant.
 * 
 * @author Bob Schellink
 */
public class PageLookup {

    private List excludesList = new ArrayList();
    
    Class getExcludesPageClass(String path) {
        for (int i = 0; i < excludesList.size(); i++) {
            ClickApp.ExcludesElm override =
                (ClickApp.ExcludesElm) excludesList.get(i);

            if (override.isMatch(path)) {
                return override.getPageClass();
            }
        }

        return null;
    }

    Class getPageClass(String pagePath, String packageName, List plugins) {
        // First check if default packageName and pagePath maps to a class
        // in the web application
        Class cls = getPageClass(pagePath, packageName);
        if (cls == null) {
            // Otherwise check in the available plugins for a class that can be
            // mapped to the pagePath
            for (Iterator it = plugins.iterator(); it.hasNext(); ) {
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

    Class getPageClass(String pagePath, String pagesPackage) {
        // To understand this code better we will walk through an example as the
        // code plays out. Imagine this method is called with the args:
        // pagePath='/pages/edit-customer.htm'
        // pagesPackage='net.sf.click'

        // Add period at end.
        // packageName = 'net.sf.click.'
        String packageName = pagesPackage + ".";
        String className = "";

        // Strip off extension. 
        // path = '/pages/edit-customer'
        int extensionIndex = pagePath.lastIndexOf(".");
        String path = pagePath;
        if (extensionIndex >= 0) {
            path = pagePath.substring(0, extensionIndex);
        }

        // If page is excluded return the excluded class
        Class excludePageClass = getExcludesPageClass(path);
        if (excludePageClass != null) {
            return excludePageClass;
        }

        // Build complete packageName. 
        // packageName = 'net.sf.click.pages.'
        // className = 'edit-customer'
        if (path.indexOf("/") != -1) {
            StringTokenizer tokenizer = new StringTokenizer(path, "/");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    packageName = packageName + token + ".";
                } else {
                    className = token;
                }
            }
        } else {
            className = path;
        }

        // CamelCase className. className = 'EditCustomer'
        StringTokenizer tokenizer = new StringTokenizer(className, "_-");
        className = "";
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = Character.toUpperCase(token.charAt(0)) + token.substring(1);
            className += token;
        }

        // className = 'net.sf.click.pages.EditCustomer'
        className = packageName + className;

        Class pageClass = null;
        try {
            // Attempt to load class.
            pageClass = ClickUtils.classForName(className);

            if (!Page.class.isAssignableFrom(pageClass)) {
                String msg = "Automapped page class " + className
                             + " is not a subclass of net.sf.click.Page";
                throw new RuntimeException(msg);
            }

        } catch (ClassNotFoundException cnfe) {

            boolean classFound = false;

            // Append "Page" to className and attempt to load class again.
            // className = 'net.sf.click.pages.EditCustomerPage'
            if (!className.endsWith("Page")) {
                String classNameWithPage = className + "Page";
                try {
                    // Attempt to load class.
                    pageClass = ClickUtils.classForName(classNameWithPage);

                    if (!Page.class.isAssignableFrom(pageClass)) {
                        String msg = "Automapped page class " + classNameWithPage
                                     + " is not a subclass of net.sf.click.Page";
                        throw new RuntimeException(msg);
                    }

                    classFound = true;

                } catch (ClassNotFoundException cnfe2) {
                }
            }

            if (!classFound) {
                
                System.out.println(pagePath + " -> CLASS NOT FOUND");
                System.out.println("class not found: " + className);
            }
        }

        return pageClass;
    }

    public static void main(String[] args) {
        PageLookup lookup = new PageLookup();
        Class pageClass = lookup.getPageClass("/pages/edit-customer.htm", "net.sf.click");
        System.out.println("getPageClass(String, String) - > " + pageClass);
        List plugins = new ArrayList();

        // Add plugins
        ClickPlugin plugin = new ClickPlugin() {
            public String getPluginName() { return "corp"; }

            public String getPluginPath() { return "/admin"; }
            
            public String getPluginPackage() { return "co.za.corp"; }
        };

        plugins.add(plugin);
        
        pageClass = lookup.getPageClass("/pages/edit-customer.htm", "net.sf.click", plugins);
        System.out.println("getPageClass(String, List) " + pageClass);
        pageClass = lookup.getPageClass("/admin/my.htm", "net.sf.click", plugins);
        System.out.println("getPageClass(String, List) " + pageClass);
    }
}
