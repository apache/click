/* 
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

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.click.util.ClickLogger;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

/**
 *
 * @author Bob Schellink
 */
public class DevelopmentStrategy {
    
    private ClickApp clickApp;
    private ClickLogger logger;
    private Map manualPageByPathMap = new HashMap();
    private Map manualPageByClassMap = new HashMap();
    private PathLookupAlgorithm pathLookupAlgorithm = null;
    
    /** Creates a new instance of DevelopmentStrategy */
    public DevelopmentStrategy(ClickApp clickApp) {
        this.clickApp = clickApp;
        pathLookupAlgorithm = new PathLookupAlgorithm(clickApp);
        this.logger = clickApp.getLogger();
    }
    
    public void handleManualPageMapping(Element pagesElm) throws ClassNotFoundException {
        List pageList = clickApp.getChildren(pagesElm, "page");
        
        if (!pageList.isEmpty() && logger.isDebugEnabled()) {
            logger.debug("click.xml pages:");
        }
        
        for (int i = 0; i < pageList.size(); i++) {
            Element pageElm = (Element) pageList.get(i);
            
            PageMetaData page = new PageMetaData(pageElm, clickApp.getPagesPackage(), clickApp.getCommonHeaders());
            manualPageByPathMap.put(page.getPath(), page);
             /*
            pageByPathMap.put(page.getPath(), page);
              
            if (logger.isDebugEnabled()) {
                String msg =
                        page.getPath() + " -> " + page.getPageClass().getName();
                logger.debug(msg);
            }
              */
        }
    }
    
    public void handleBuildingOfClassMap() {
        //Build pages by class map.  The difference between this method and ClickApps method
        //is that the key is a string not the actual class. Also only manually mapped
        //pages will be stored here. The automapped pages are looked up dynamically.
        for (Iterator i = manualPageByPathMap.values().iterator(); i.hasNext();) {
            PageMetaData page = (PageMetaData) i.next();
            
            Object value = manualPageByClassMap.get(page.pageClassAsString);
            
            if (value == null) {
                manualPageByClassMap.put(page.pageClassAsString, page);
                
            } else if (value instanceof List) {
                ((List) value).add(value);
                
            } else if (value instanceof PageMetaData) {
                List list = new ArrayList();
                list.add(value);
                list.add(page);
                manualPageByClassMap.put(page.pageClassAsString, list);
                
            } else {
                // should never occur
                throw new IllegalStateException();
            }
        }
    }
    
    public void clearCache() {
        manualPageByPathMap.clear();
        manualPageByClassMap.clear();
    }

    public PageMetaData lookupManuallyStoredMetaData(String path) {
        //Try and load the manually mapped page first
        PageMetaData page = (PageMetaData) manualPageByPathMap.get(path);
        if (page == null) {
            String jspPath = StringUtils.replace(path, ".htm", ".jsp");
            page = (PageMetaData) manualPageByPathMap.get(jspPath);
        }
        return page;
    }
    
    public PageMetaData lookupManuallyStoredMetaData(Class pageClass) {
        //Try and load the manually mapped page first
        PageMetaData page = null;
        Object object = (PageMetaData) manualPageByClassMap.get(pageClass);
        if (object instanceof PageMetaData) {
            page = (PageMetaData) object;
            return page;
            
        } else if (object instanceof List) {
            String msg =
                    "Page class resolves to multiple paths: " + pageClass.getName();
            throw new IllegalArgumentException(msg);
            
        }
        
        return page;
    }
    
    public Class getPageClass(String path) {
        Class pageClass = null;
        
        //Try and load the manually mapped page first
        PageMetaData page = lookupManuallyStoredMetaData(path);
        
        if (page != null) {
            try {
                return clickApp.loadClass(page.getPageClassAsString());
                //return page.getPageClass();
            } catch (ClassNotFoundException ex) {
                //ignore, this class is not available, so try and load it
                //from the classpath
            }
        }
        
        try {
            //Set resourcePaths = servletContext.getResourcePaths(path);
            URL resource = clickApp.getServletContext().getResource(path);
            
            if (resource != null) {
                pageClass = clickApp.getPageClass(path, clickApp.getPagesPackage());
                
                //No caching of this class or fields are done here.
                /*
                if (pageClass != null) {
                    page = new PageMetaData(path, pageClass, clickApp.getCommonHeaders());
                 
                    //pageByPathMap.put(page.getPath(), page);
                 
                    if (logger.isDebugEnabled()) {
                        String msg = path + " -> " + pageClass.getName();
                        logger.debug(msg);
                    }
                }
                 */
            }
            
        }  catch (MalformedURLException ex) {
            //ignore, will return null
        }
        
        return pageClass;
    }
    
    public String getPagePath(Class pageClass) {
        //Try to lookup path from manually mapped pages first
        PageMetaData page = lookupManuallyStoredMetaData(pageClass);
        if(page != null) {
            return page.getPath();
        }
        
        //If not found we do a reverse algorithm lookup for the path
        return lookupPathFromClass(pageClass);
    }
    
    public String lookupPathFromClass(Class pageClass) {        
        return pathLookupAlgorithm.getPagePath(pageClass);
    }
    
    public Map getPageHeaders(String path) {
        //Try and load the manually mapped page first
        PageMetaData page = lookupManuallyStoredMetaData(path);
        
        if (page != null) {
            return page.getHeaders();
        } else {
            //If path was not found in the manually loaded pages, return common headers
            return Collections.unmodifiableMap(clickApp.getCommonHeaders());
        }
    }
    
    public Field getPageField(Class pageClass, String fieldName) {
        return (Field) getPageFields(pageClass).get(fieldName);
    }

    public Field[] getPageFieldArray(Class pageClass) {
        return pageClass.getFields();
    }

    public Map getPageFields(Class pageClass) {
        Field[] fieldArray = getPageFieldArray(pageClass);
        Map fields = new HashMap();
        for (int i = 0; i < fieldArray.length; i++) {
            Field field = fieldArray[i];
            fields.put(field.getName(), field);
        }
        return fields;
    }
    
    public class PageMetaData {
        
        private Map headers;
        
        private String pageClassAsString;
        
        private String path;
        
        public PageMetaData(Element element, String pagesPackage, Map commonHeaders) {
            
            // Set headers
            Map aggregationMap = new HashMap(commonHeaders);
            Map pageHeaders = clickApp.loadHeadersMap(element);
            aggregationMap.putAll(pageHeaders);
            headers = Collections.unmodifiableMap(aggregationMap);

            // Set path
            String pathValue = element.getAttribute("path");
            if (pathValue.charAt(0) != '/') {
                path = "/" + pathValue;
            } else {
                path = pathValue;
            }
            
            // Set pageClass
            pageClassAsString = element.getAttribute("classname");
            if (pageClassAsString != null) {
                if (pagesPackage.trim().length() > 0) {
                    pageClassAsString = pagesPackage + "." + pageClassAsString;
                }
            } else {
                String msg = "No classname defined for page path " + path;
                throw new RuntimeException(msg);
            }
        }
        /*
        public PageMetaData(String path, Class pageClass, Map commonHeaders) {
         
            headers = Collections.unmodifiableMap(commonHeaders);
            this.pageClass = pageClass;
            this.path = path;
         
            fieldArray = pageClass.getFields();
         
            fields = new HashMap();
            for (int i = 0; i < fieldArray.length; i++) {
                Field field = fieldArray[i];
                fields.put(field.getName(), field);
            }
        }
         
        public PageMetaData(String className, String path)
        throws ClassNotFoundException {
         
            this.fieldArray = null;
            this.fields = Collections.EMPTY_MAP;
            this.headers = Collections.EMPTY_MAP;
            pageClass = clickApp.loadClass(className);
            this.path = path;
        }
         */
        
        /*
        public Field[] getFieldArray() {
            return fieldArray;
        }
         
        public Map getFields() {
            return fields;
        }
         */
        public Map getHeaders() {
            return headers;
        }
        
        public String getPageClassAsString() {
            return pageClassAsString;
        }
        
        public String getPath() {
            return path;
        }
    }
}
