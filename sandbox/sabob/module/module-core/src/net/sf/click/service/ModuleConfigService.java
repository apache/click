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

    /** The application ModuleService. */
    private ModuleService moduleService;

    private LogService logService;

    public LogService getLogService() {
        return logService;
    }

    public void onInit(ServletContext servletContext) throws Exception {
        // Load modules first so ClickApp can process them

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

        moduleService.loadModules(this);

        super.onInit(servletContext);

        for (Iterator it = moduleService.getModules().values().iterator(); it.hasNext();) {
            ClickModule clickModule = (ClickModule) it.next();
            clickModule.onInit();
        }
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
    
    public ModuleService getModuleService() {
        return moduleService;
    }

    private void loadModuleService(Element rootElm) throws Exception {

        Element moduleServiceElm = ClickUtils.getChild(rootElm, "module-service");

        if (moduleServiceElm != null) {
            Class moduleServiceClass = ConsoleLogService.class;

            String classname = moduleServiceElm.getAttribute("classname");

            if (StringUtils.isNotBlank(classname)) {
                moduleServiceClass = ClickUtils.classForName(classname);
            }

            moduleService = (ModuleService) moduleServiceClass.newInstance();

        } else {
            moduleService = new DefaultModuleService();
        }

        if (getLogService().isDebugEnabled()) {
            String msg = "initializing ModuleService: " + moduleService.getClass().
                getName();
            getLogService().debug(msg);
        }

        moduleService.onInit(this);
    }

    /**
     * Note: need to override getPageClass and check not only in the default
     * package, but also every module and their respective package.
     */
    public Class getPageClass(String pagePath, String packageName) {
        // First check if default packageName and pagePath maps to a class
        // in the web application
        Class cls = super.getPageClass(pagePath, packageName);
        if (cls == null) {
            // Otherwise check the available modules for a class that can be
            // mapped to the pagePath
            for (Iterator it = moduleService.getModules().values().iterator(); it.hasNext();) {
                ClickModule module = (ClickModule) it.next();
                String modulePagePath = pagePath;
                if (StringUtils.isNotBlank(module.getModulePath())) {
                    if (!pagePath.startsWith(module.getModulePath())) {
                        // The module templates lives in a different webPath
                        // than the specified pagePath
                        continue;
                    }
                    modulePagePath = modulePagePath.substring(module.getModulePath().
                        length());
                }

                cls = getPageClass(modulePagePath, module.getModulePackage());
                if (cls != null) {
                    break;
                }
            }
        }
        return cls;
    }
}
