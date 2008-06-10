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

import java.io.InputStream;
import net.sf.click.*;
import java.util.Iterator;
import javax.servlet.ServletContext;
import net.sf.click.util.ClickUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Bob Schellink
 */
public class ModuleConfigService extends XmlConfigService {

    private ServletContext servletContext;

    /** The application PluginService. */
    private ModuleService pluginService;

    private LogService logService;

    public LogService getLogService() {
        return logService;
    }

    public void onInit(ServletContext servletContext) throws Exception {
        // Load plugins first so ClickApp can process them

        // Set default logService early to log errors when services fail.
        logService = new ConsoleLogService();

        InputStream inputStream = ClickUtils.getClickConfig(servletContext);

        try {
            Document document = ClickUtils.buildDocument(inputStream, this);

            Element rootElm = document.getDocumentElement();

            loadModuleService(rootElm);
        } finally {
            ClickUtils.close(inputStream);
        }

        //TODO Workaround setting servletContext for now
        this.servletContext = servletContext;

        pluginService.loadPlugins(this);

        super.onInit(servletContext);

        for (Iterator it = pluginService.getModules().values().iterator(); it.hasNext();) {
            ClickModule clickModule = (ClickModule) it.next();
            clickModule.onInit();
        }
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
    
    public ModuleService getPluginService() {
        return pluginService;
    }

    private void loadModuleService(Element rootElm) throws Exception {

        Element pluginServiceElm = ClickUtils.getChild(rootElm, "plugin-service");

        if (pluginServiceElm != null) {
            Class pluginServiceClass = ConsoleLogService.class;

            String classname = pluginServiceElm.getAttribute("classname");

            if (StringUtils.isNotBlank(classname)) {
                pluginServiceClass = ClickUtils.classForName(classname);
            }

            pluginService = (ModuleService) pluginServiceClass.newInstance();

        } else {
            pluginService = new DefaultModuleService();
        }

        if (getLogService().isDebugEnabled()) {
            String msg = "initializing PluginService: " + pluginService.getClass().
                getName();
            getLogService().debug(msg);
        }

        pluginService.onInit(this);
    }

    /**
     * Note: need to override getPageClass and check not only in the default
     * package, but also every plugin and their respective package.
     */
    public Class getPageClass(String pagePath, String packageName) {
        // First check if default packageName and pagePath maps to a class
        // in the web application
        Class cls = super.getPageClass(pagePath, packageName);
        if (cls == null) {
            // Otherwise check the available plugins for a class that can be
            // mapped to the pagePath
            for (Iterator it = pluginService.getModules().values().iterator(); it.hasNext();) {
                ClickModule plugin = (ClickModule) it.next();
                String pluginPagePath = pagePath;
                if (StringUtils.isNotBlank(plugin.getModulePath())) {
                    if (!pagePath.startsWith(plugin.getModulePath())) {
                        // The plugin templates lives in a different webPath
                        // than the specified pagePath
                        continue;
                    }
                    pluginPagePath = pluginPagePath.substring(plugin.getModulePath().
                        length());
                }

                cls = getPageClass(pluginPagePath, plugin.getModulePackage());
                if (cls != null) {
                    break;
                }
            }
        }
        return cls;
    }
}
