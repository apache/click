/*
 * Copyright 2008 Malcolm A. Edgar
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import javax.servlet.ServletContext;

import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.Format;
import net.sf.click.util.HtmlStringBuffer;
import ognl.Ognl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Provides a Click XML configuration service class.
 * <p/>
 * This class reads Click configuration information from a file named
 * <tt>click.xml</tt>. The service will first lookup the <tt>click.xml</tt>
 * under the applications <tt>WEB-INF</tt> directory, and if not found
 * attempt to load the configuration file from the classpath root.
 * <p/>
 * Configuring Click through the <tt>click.xml</tt> file is the most common
 * technique.
 * <p/>
 * However you can instruct Click to use a different service implementation.
 * Please see {@link ConfigService} for more details.
 *
 * @author Malcolm Edgar
 */
public class XmlConfigService implements ConfigService, EntityResolver {

    /** The name of the Click logger: &nbsp; "<tt>net.sf.click</tt>". */
    static final String CLICK_LOGGER = "net.sf.click";

    /** The click deployment directory path: &nbsp; "/click". */
    static final String CLICK_PATH = "/click";

    /** The default common page headers. */
    static final Map DEFAULT_HEADERS;

    /**
     * The default velocity properties filename: &nbsp;
     * "<tt>/WEB-INF/velocity.properties</tt>".
     */
    static final String DEFAULT_VEL_PROPS = "/WEB-INF/velocity.properties";

    /** The click DTD file name: &nbsp; "<tt>click.dtd</tt>". */
    static final String DTD_FILE_NAME = "click.dtd";

    /**
     * The resource path of the click DTD file: &nbsp;
     * "<tt>/net/sf/click/click.dtd</tt>".
     */
    static final String DTD_FILE_PATH = "/net/sf/click/" + DTD_FILE_NAME;

    /**
     * The user supplied macro file name: &nbsp; "<tt>macro.vm</tt>".
     */
    static final String MACRO_VM_FILE_NAME = "macro.vm";

    /** The production application mode. */
    static final int PRODUCTION = 0;

    /** The profile application mode. */
    static final int PROFILE = 1;

    /** The development application mode. */
    static final int DEVELOPMENT = 2;

    /** The debug application mode. */
    static final int DEBUG = 3;

    /** The trace application mode. */
    static final int TRACE = 4;

    static final String[] MODE_VALUES =
        { "production", "profile", "development", "debug", "trace" };

    private static final Object PAGE_LOAD_LOCK = new Object();

    /**
     * The name of the Velocity logger: &nbsp; "<tt>org.apache.velocity</tt>".
     */
    static final String VELOCITY_LOGGER = "org.apache.velocity";

    /**
     * The global Velocity macro file name: &nbsp;
     * "<tt>VM_global_library.vm</tt>".
     */
    static final String VM_FILE_NAME = "VM_global_library.vm";

    /** Initialize the default headers. */
    static {
        DEFAULT_HEADERS = new HashMap();
        DEFAULT_HEADERS.put("Pragma", "no-cache");
        DEFAULT_HEADERS.put("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
        DEFAULT_HEADERS.put("Expires", new Date(1L));
    }

    // ------------------------------------------------ Package Private Members

    /** The Map of global page headers. */
    Map commonHeaders;

    /** The page automapping override page class for path list. */
    final List excludesList = new ArrayList();

    /** The map of ClickApp.PageElm keyed on path. */
    final Map pageByPathMap = new HashMap();

    /** The map of ClickApp.PageElm keyed on class. */
    final Map pageByClassMap = new HashMap();

    /** The list of page packages. */
    final List pagePackages = new ArrayList();

    // -------------------------------------------------------- Private Members

    /** The automatically bind controls, request parameters and models flag. */
    private Boolean autobinding;

    /** The Commons FileUpload service class. */
    private FileUploadService fileUploadService;

    /** The format class. */
    private Class formatClass;

    /** The charcter encoding of this application. */
    private String charset;

    /** The default application locale.*/
    private Locale locale;

    /** The application log service. */
    private LogService logService;

    /**
     * The application mode:
     * [ PRODUCTION | PROFILE | DEVELOPMENT | DEBUG | TRACE ].
     */
    private int mode;

    /** The ServletContext instance. */
    private ServletContext servletContext;

    /** The application TemplateService. */
    private TemplateService templateService;

    // --------------------------------------------------------- Public Methods

    /**
     * @see ConfigService#onInit(ServletContext)
     *
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the application
     */
    public void onInit(ServletContext servletContext) throws Exception {

        Validate.notNull(servletContext, "Null servletContext parameter");

        this.servletContext = servletContext;

        // Set default logService early to log errors when services fail.
        logService = new ConsoleLogService();

        InputStream inputStream = ClickUtils.getClickConfig(servletContext);

        try {
            Document document = ClickUtils.buildDocument(inputStream, this);

            Element rootElm = document.getDocumentElement();

            // Load the log service
            loadLogService(rootElm);

            // Load the application mode and set the logger levels
            loadMode(rootElm);

            // Deploy click resources
            deployFiles(rootElm);

            // Load the format class
            loadFormatClass(rootElm);

            // Load the common headers
            loadHeaders(rootElm);

            // Load the pages
            loadPages(rootElm);

            // Load the error and not-found pages
            loadDefaultPages();

            // Load the charset
            loadCharset(rootElm);

            // Load the locale
            loadLocale(rootElm);

            // Load the File Upload service
            loadFileUploadService(rootElm);

            // Load the Templating service
            loadTemplateService(rootElm);

        } finally {
            ClickUtils.close(inputStream);
        }
    }

    /**
     * @see ConfigService#onDestroy()
     */
    public void onDestroy() {
        if (getFileUploadService() != null) {
            getFileUploadService().onDestroy();
        }
        if (getTemplateService() != null) {
            getTemplateService().onDestroy();
        }
        if (getLogService() != null) {
            getLogService().onDestroy();
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the application mode String value: &nbsp; <tt>["production",
     * "profile", "development", "debug"]</tt>.
     *
     * @return the application mode String value
     */
    public String getApplicationMode() {
        return MODE_VALUES[mode];
    }

    /**
     * @see ConfigService#getCharset()
     *
     * @return the application character encoding
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @see ConfigService#getFileUploadService()
     *
     * @return the FileUpload service
     */
    public FileUploadService getFileUploadService() {
        return fileUploadService;
    }

    /**
     * @see ConfigService#getTemplateService()
     *
     * @return the FileUpload service
     */
    public TemplateService getTemplateService() {
        return templateService;
    }

    /**
     * @see ConfigService#createFormat()
     *
     * @return a new format object
     */
    public Format createFormat() {
        try {
            return (Format) formatClass.newInstance();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see ConfigService#getLocale()
     *
     * @return the application locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @see ConfigService#getLogService()
     *
     * @return the application log service.
     */
    public LogService getLogService() {
        return logService;
    }

    /**
     * @see ConfigService#isPagesAutoBinding()
     *
     * @return true if request parameters should be automatically bound to public
     * page fields
     */
    public boolean isPagesAutoBinding() {
        if (autobinding != null) {
            return autobinding.booleanValue();
        } else {
            return true;
        }
    }

    /**
     * @see ConfigService#isProductionMode()
     *
     * @return true if the application is in "production" mode
     */
    public boolean isProductionMode() {
        return (mode == PRODUCTION);
    }

    /**
     * @see ConfigService#isProfileMode()
     *
     * @return true if the application is in "profile" mode
     */
    public boolean isProfileMode() {
        return (mode == PROFILE);
    }

    /**
     * @see ConfigService#isJspPage(String)
     *
     * @param path the Page ".htm" path
     * @return true if JSP exists for the given ".htm" path
     */
    public boolean isJspPage(String path) {
        String jspPath = StringUtils.replace(path, ".htm", ".jsp");
        return pageByPathMap.containsKey(jspPath);
    }

    /**
     * @see ConfigService#getPageClass(String)
     *
     * @param path the page path
     * @return the page class for the given path or null if no class is found
     */
    public Class getPageClass(String path) {

        // If in production or profile mode.
        if (mode <= PROFILE) {
            PageElm page = (PageElm) pageByPathMap.get(path);
            if (page == null) {
                String jspPath = StringUtils.replace(path, ".htm", ".jsp");
                page = (PageElm) pageByPathMap.get(jspPath);
            }

            if (page != null) {
                return page.getPageClass();
            } else {
                return null;
            }

        // Else in development, debug or trace mode
        } else {

            synchronized (PAGE_LOAD_LOCK) {
                PageElm page = (PageElm) pageByPathMap.get(path);
                if (page == null) {
                    String jspPath = StringUtils.replace(path, ".htm", ".jsp");
                    page = (PageElm) pageByPathMap.get(jspPath);
                }

                if (page != null) {
                    return page.getPageClass();
                }

                Class pageClass = null;

                try {
                    URL resource = servletContext.getResource(path);
                    if (resource != null) {
                        for (int i = 0; i < pagePackages.size(); i++) {
                            String pagesPackage = pagePackages.get(i).toString();

                            pageClass = getPageClass(path, pagesPackage);

                            if (pageClass != null) {
                                page = new PageElm(path, pageClass, commonHeaders);

                                pageByPathMap.put(page.getPath(), page);
                                addToClassMap(page);

                                if (logService.isDebugEnabled()) {
                                    String msg = path + " -> " + pageClass.getName();
                                    logService.debug(msg);
                                }

                                break;
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    //ignore
                }
                return pageClass;
            }
        }
    }

    /**
     * @see ConfigService#getPagePath(Class)
     *
     * @param pageClass the page class
     * @return path the page path
     * @throws IllegalArgumentException if the Page Class is not configured
     * with a unique path
     */
    public String getPagePath(Class pageClass) {
        Object object = pageByClassMap.get(pageClass);

        if (object instanceof XmlConfigService.PageElm) {
            XmlConfigService.PageElm page = (XmlConfigService.PageElm) object;
            return page.getPath();

        } else if (object instanceof List) {
            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append("Page class resolves to multiple paths: ");
            buffer.append(pageClass.getName());
            buffer.append(" -> [");
            for (Iterator it = ((List) object).iterator(); it.hasNext();) {
                PageElm pageElm = (PageElm) it.next();
                buffer.append(pageElm.getPath());
                if (it.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append("]");
            throw new IllegalArgumentException(buffer.toString());

        } else {
            return null;
        }
    }

    /**
     * @see ConfigService#getPageHeaders(String)
     *
     * @param path the path of the page
     * @return a Map of headers for the given page path
     */
    public Map getPageHeaders(String path) {
        PageElm page = (PageElm) pageByPathMap.get(path);
        if (page == null) {
            String jspPath = StringUtils.replace(path, ".htm", ".jsp");
            page = (PageElm) pageByPathMap.get(jspPath);
        }

        if (page != null) {
            return page.getHeaders();
        } else {
            return null;
        }
    }

    /**
     * @see ConfigService#getNotFoundPageClass()
     *
     * @return the page not found <tt>Page</tt> <tt>Class</tt>
     */
    public Class getNotFoundPageClass() {
        PageElm page = (PageElm) pageByPathMap.get(NOT_FOUND_PATH);

        if (page != null) {
            return page.getPageClass();

        } else {
            return net.sf.click.Page.class;
        }
    }

    /**
     * @see ConfigService#getErrorPageClass()
     *
     * @return the error handling page <tt>Page</tt> <tt>Class</tt>
     */
    public Class getErrorPageClass() {
        PageElm page = (PageElm) pageByPathMap.get(ERROR_PATH);

        if (page != null) {
            return page.getPageClass();

        } else {
            return net.sf.click.util.ErrorPage.class;
        }
    }

    /**
     * @see ConfigService#getPageField(Class, String)
     *
     * @param pageClass the page class
     * @param fieldName the name of the field
     * @return the public field of the pageClass with the given name or null
     */
    public Field getPageField(Class pageClass, String fieldName) {
        return (Field) getPageFields(pageClass).get(fieldName);
    }

    /**
     * @see ConfigService#getPageFieldArray(Class)
     *
     * @param pageClass the page class
     * @return an array public fields for the given page class
     */
    public Field[] getPageFieldArray(Class pageClass) {
        Object object = pageByClassMap.get(pageClass);

        if (object instanceof XmlConfigService.PageElm) {
            XmlConfigService.PageElm page = (XmlConfigService.PageElm) object;
            return page.getFieldArray();

        } else if (object instanceof List) {
            List list = (List) object;
            XmlConfigService.PageElm page = (XmlConfigService.PageElm) list.get(0);
            return page.getFieldArray();

        } else {
            return null;
        }
    }

    /**
     * @see ConfigService#getPageFields(Class)
     *
     * @param pageClass the page class
     * @return a Map of public fields for the given page class
     */
    public Map getPageFields(Class pageClass) {
        Object object = pageByClassMap.get(pageClass);

        if (object instanceof XmlConfigService.PageElm) {
            XmlConfigService.PageElm page = (XmlConfigService.PageElm) object;
            return page.getFields();

        } else if (object instanceof List) {
            List list = (List) object;
            XmlConfigService.PageElm page = (XmlConfigService.PageElm) list.get(0);
            return page.getFields();

        } else {
            return Collections.EMPTY_MAP;
        }
    }

    /**
     * @see ConfigService#getServletContext()
     *
     * @return the application servlet context
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * This method resolves the click.dtd for the XML parser using the
     * classpath resource: <tt>/net/sf/click/click.dtd</tt>.
     *
     * @see EntityResolver#resolveEntity(String, String)
     *
     * @param publicId the DTD public id
     * @param systemId the DTD system id
     * @return resolved entity DTD input stream
     * @throws SAXException if an error occurs parsing the document
     * @throws IOException if an error occurs reading the document
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {

        InputStream inputStream = ClickUtils.getResourceAsStream(DTD_FILE_PATH, getClass());

        if (inputStream != null) {
            return new InputSource(inputStream);
        } else {
            throw new IOException("could not load resource: " + DTD_FILE_PATH);
        }
    }

    // -------------------------------------------------------- Package Private Methods

    /**
     * Loads all Click Pages defined in the <tt>click.xml</tt> file, including
     * manually defined Pages, auto mapped Pages and excluded Pages.
     *
     * @param rootElm the root xml element containing the configuration
     * @throws java.lang.ClassNotFoundException if the specified Page class can
     * not be found on the classpath
     */
    void loadPages(Element rootElm) throws ClassNotFoundException {
        List pagesList = ClickUtils.getChildren(rootElm, "pages");

        if (pagesList.isEmpty()) {
            String msg = "required configuration 'pages' element missing.";
            throw new RuntimeException(msg);
        }

        List templates = getTemplateFiles();

        for (int i = 0; i < pagesList.size(); i++) {

            Element pagesElm = (Element) pagesList.get(i);

            // Deterime whether to use automapping
            boolean automap = true;
            String automapStr = pagesElm.getAttribute("automapping");
            if (StringUtils.isBlank(automapStr)) {
                automapStr = "true";
            }

            if ("true".equalsIgnoreCase(automapStr)) {
                automap = true;
            } else if ("false".equalsIgnoreCase(automapStr)) {
                automap = false;
            } else {
                String msg = "Invalid pages automapping attribute: " + automapStr;
                throw new RuntimeException(msg);
            }

            // Determine whether to use autobinding.
            String autobindingStr = pagesElm.getAttribute("autobinding");
            if (StringUtils.isBlank(autobindingStr)) {
                autobindingStr = "true";
            }

            if ("true".equalsIgnoreCase(autobindingStr)) {
                autobinding = Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(autobindingStr)) {
                autobinding = Boolean.FALSE;
            } else {
                String msg = "Invalid pages autobinding attribute: " + autobindingStr;
                throw new RuntimeException(msg);
            }

            // TODO: if autobinding is set to false an there are multiple pages how should this be handled
            // Perhaps autobinding should be moved to <click-app> and be a application wide setting?
            // However the way its implemented above is probably fine for backward compatibility
            // purposes, meaning the last defined autobinding wins

            String pagesPackage = pagesElm.getAttribute("package");
            if (StringUtils.isBlank(pagesPackage)) {
                pagesPackage = "";
            }

            pagesPackage = pagesPackage.trim();
            if (pagesPackage.endsWith(".")) {
                pagesPackage =
                    pagesPackage.substring(0, pagesPackage.length() - 2);
            }

            // Add the pages package to the list of page packages
            pagePackages.add(pagesPackage);

            buildManualPageMapping(pagesElm, pagesPackage);

            if (automap) {
                buildAutoPageMapping(pagesElm, pagesPackage, templates);
            }
        }

        buildClassMap();
    }

    /**
     * Add manually defined Pages to the {@link #pageByPathMap}.
     *
     * @param pagesElm the xml element containing manually defined Pages
     * @param pagesPackage the pages package prefix
     *
     * @throws java.lang.ClassNotFoundException if the specified Page class can
     * not be found on the classpath
     */
    void buildManualPageMapping(Element pagesElm, String pagesPackage) throws ClassNotFoundException {

        List pageList = ClickUtils.getChildren(pagesElm, "page");

        if (!pageList.isEmpty() && logService.isDebugEnabled()) {
            logService.debug("click.xml pages:");
        }

        for (int i = 0; i < pageList.size(); i++) {
            Element pageElm = (Element) pageList.get(i);

            XmlConfigService.PageElm page =
                new XmlConfigService.PageElm(pageElm, pagesPackage, commonHeaders);

            pageByPathMap.put(page.getPath(), page);

            if (logService.isDebugEnabled()) {
                String msg =
                    page.getPath() + " -> " + page.getPageClass().getName();
                logService.debug(msg);
            }
        }
    }

    /**
     * Build the {@link #pageByPathMap} by associating template files with
     * matching Java classes found on the classpath.
     * <p/>
     * This method also rebuilds the {@link #excludesList}. This list contains
     * URL paths that should not be auto-mapped.
     *
     * @param pagesElm the xml element containing the excluded URL paths
     * @param pagesPackage the pages package prefix
     * @param templates the list of templates to map to Page classes
     */
    void buildAutoPageMapping(Element pagesElm, String pagesPackage, List templates) throws ClassNotFoundException {

        // Build list of automap path page class overrides
        excludesList.clear();
        for (Iterator i = ClickUtils.getChildren(pagesElm, "excludes").iterator();
             i.hasNext();) {

            excludesList.add(new XmlConfigService.ExcludesElm((Element) i.next()));
        }

        if (logService.isDebugEnabled()) {
            logService.debug("automapped pages:");
        }

        for (int i = 0; i < templates.size(); i++) {
            String pagePath = (String) templates.get(i);

            if (!pageByPathMap.containsKey(pagePath)) {

                Class pageClass = getPageClass(pagePath, pagesPackage);

                if (pageClass != null) {
                    XmlConfigService.PageElm page = new XmlConfigService.PageElm(pagePath,
                            pageClass,
                            commonHeaders);

                    pageByPathMap.put(page.getPath(), page);

                    if (logService.isDebugEnabled()) {
                        String msg =
                            pagePath + " -> " + pageClass.getName();
                        logService.debug(msg);
                    }
                }
            }
        }
    }

    /**
     * Build the {@link #pageByClassMap} from the {@link #pageByPathMap} and
     * delegate to {@link #buildClassMap(PageElm)}.
     */
    void buildClassMap() {
        // Build pages by class map
        for (Iterator i = pageByPathMap.values().iterator(); i.hasNext();) {
            XmlConfigService.PageElm page = (XmlConfigService.PageElm) i.next();
            addToClassMap(page);
        }
    }

    /**
     * Add the specified page to the {@link #pageByClassMap} where the Map's key
     * holds the Page class and value holds the {@link PageElm}.
     *
     * @param page the PageElm containing metadata about a specific page
     */
    void addToClassMap(PageElm page) {
        Object value = pageByClassMap.get(page.pageClass);
        if (value == null) {
            pageByClassMap.put(page.pageClass, page);

        } else if (value instanceof List) {
            ((List) value).add(value);

        } else if (value instanceof XmlConfigService.PageElm) {
            List list = new ArrayList();
            list.add(value);
            list.add(page);
            pageByClassMap.put(page.pageClass, list);

        } else {
            // should never occur
            throw new IllegalStateException();
        }
    }

    /**
     * Load the Page headers from the specified xml element.
     *
     * @param parentElm the element to load the headers from
     * @return the map of Page headers
     */
    static Map loadHeadersMap(Element parentElm) {
        Map headersMap = new HashMap();

        List headerList = ClickUtils.getChildren(parentElm, "header");

        for (int i = 0, size = headerList.size(); i < size; i++) {
            Element header = (Element) headerList.get(i);

            String name = header.getAttribute("name");
            String type = header.getAttribute("type");
            String propertyValue = header.getAttribute("value");

            Object value = null;

            if ("".equals(type) || "String".equalsIgnoreCase(type)) {
                value = propertyValue;
            } else if ("Integer".equalsIgnoreCase(type)) {
                value = Integer.valueOf(propertyValue);
            } else if ("Date".equalsIgnoreCase(type)) {
                value = new Date(Long.parseLong(propertyValue));
            } else {
                value = null;
                String message =
                    "Invalid property type [String|Integer|Date]: "
                    + type;
                throw new IllegalArgumentException(message);
            }

            headersMap.put(name, value);
        }

        return headersMap;
    }

    /**
     * Find and return the page class for the specified pagePath and
     * pagesPackage.
     * <p/>
     * For example if the pagePath is <tt>'/edit-customer.htm'</tt> and
     * package is <tt>'com.mycorp'</tt>, the matching page class will be:
     * <tt>com.mycorp.EditCustomer</tt> or <tt>com.mycorp.EditCustomerPage</tt>.
     * <p/>
     * If the page path is <tt>'/admin/add-customer.htm'</tt> and package is
     * <tt>'com.mycorp'</tt>, the matching page class will be:
     * <tt>com.mycorp.admin.AddCustomer</tt> or
     * <tt>com.mycorp.admin.AddCustomerPage</tt>.
     *
     * @param pagePath the path used for matching against a page class name
     * @param pagesPackage the package of the page class
     * @return the page class for the specified pagePath and pagesPackage
     */
    Class getPageClass(String pagePath, String pagesPackage) {
        // To understand this method lets walk through an example as the
        // code plays out. Imagine this method is called with the arguments:
        // pagePath='/pages/edit-customer.htm'
        // pagesPackage='net.sf.click'

        // Add period at end.
        // packageName = 'net.sf.click.'
        String packageName = pagesPackage + ".";
        String className = "";

        // Strip off extension.
        // path = '/pages/edit-customer'
        String path = pagePath.substring(0, pagePath.lastIndexOf("."));

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

        // CamelCase className.
        // className = 'EditCustomer'
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
                if (logService.isDebugEnabled()) {
                    logService.debug(pagePath + " -> CLASS NOT FOUND");
                }
                if (logService.isTraceEnabled()) {
                    logService.trace("class not found: " + className);
                }
            }
        }

        return pageClass;
    }

    // -------------------------------------------------------- Private Methods

    private Element getResourceRootElement(String path) throws IOException {
        Document document = null;
        InputStream inputStream = null;
        try {
            inputStream = ClickUtils.getResourceAsStream(path, getClass());

            if (inputStream != null) {
                document = ClickUtils.buildDocument(inputStream, this);
            }

        } finally {
            ClickUtils.close(inputStream);
        }

        if (document != null) {
            return document.getDocumentElement();

        } else {
            return null;
        }
    }

    private void deployControls(Element rootElm) throws Exception {

        if (rootElm == null) {
            return;
        }

        Element controlsElm = ClickUtils.getChild(rootElm, "controls");

        if (controlsElm == null) {
            return;
        }

        List deployableList = ClickUtils.getChildren(controlsElm, "control");

        for (int i = 0; i < deployableList.size(); i++) {
            Element deployableElm = (Element) deployableList.get(i);

            String classname = deployableElm.getAttribute("classname");
            if (StringUtils.isBlank(classname)) {
                String msg =
                    "'control' element missing 'classname' attribute.";
                throw new RuntimeException(msg);
            }

            Class deployClass = ClickUtils.classForName(classname);
            Control control = (Control) deployClass.newInstance();

            control.onDeploy(servletContext);
        }
    }

    private void deployControlSets(Element rootElm) throws Exception {
        if (rootElm == null) {
            return;
        }

        Element controlsElm = ClickUtils.getChild(rootElm, "controls");

        if (controlsElm == null) {
            return;
        }

        List controlSets = ClickUtils.getChildren(controlsElm, "control-set");

        for (int i = 0; i < controlSets.size(); i++) {
            Element controlSet = (Element) controlSets.get(i);
            String name = controlSet.getAttribute("name");
            if (StringUtils.isBlank(name)) {
                String msg =
                        "'control-set' element missing 'name' attribute.";
                throw new RuntimeException(msg);
            }
            deployControls(getResourceRootElement("/" + name));
        }
    }

    /**
     * Deploy files from jars and Controls.
     *
     * @param rootElm the click.xml configuration DOM element
     * @throws java.lang.Exception if files cannot be deployed
     */
    private void deployFiles(Element rootElm) throws Exception {

        // Deploy application files if they are not already present.
        // Only deploy if servletContext.getRealPath() returns a valid path.
        boolean resourcesDeployable = (servletContext.getRealPath("/") != null);

        if (resourcesDeployable) {
            String[] resources = {
                "/net/sf/click/control/control.css",
                "/net/sf/click/control/control.js",
                "/net/sf/click/util/error.htm",
                "/net/sf/click/not-found.htm",
                "/net/sf/click/control/VM_global_library.vm"
            };

            ClickUtils.deployFiles(servletContext, resources, "click");

            deployControls(getResourceRootElement("/click-controls.xml"));
            deployControls(getResourceRootElement("/extras-controls.xml"));
            deployControls(rootElm);
            deployControlSets(rootElm);

            deployFilesInJars();
        } else {
            String msg = "Could not auto deploy files to 'click' web folder."
                + " You may need to manually include click resources in your"
                + " web application.";
            getLogService().warn(msg);
        }
    }

    /**
     * Deploy from jars, files that are specified in the folder 'META-INF/web'.
     * <p/>
     * Only jars available on the classpath are scanned.
     *
     * @throws java.lang.Exception if the files cannot be deployed
     */
    private void deployFilesInJars() throws Exception {

        // Find all jars under WEB-INF/lib and deploy all resources from these jars
        long startTime = System.currentTimeMillis();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Enumeration en = classLoader.getResources("META-INF/web");
        while (en.hasMoreElements()) {
            URL url = (URL) en.nextElement();
            String path = url.getFile();

            // Decode the url, esp on Windows where file paths can have their
            // spaces encoded. decodeURL will convert C:\Program%20Files\project
            // to C:\Program Files\project
            path = ClickUtils.decodeURL(path);

            // Strip file prefix
            if (path.startsWith("file:")) {
                path = path.substring(5);
            }

            String jarPath = null;

            // Extract the jar name from the entry
            if (path.indexOf('!') > 0) {
                jarPath = path.substring(0, path.indexOf('!'));
            } else {
                // If jar name cannot be extracted, skip the resource
                logService.trace("Path does not represent a Jar -> '" + path + "'");
                continue;
            }

            File jar = new File(jarPath);

            if (jar.exists()) {
                deployFilesInJar(jar);

            } else {
                logService.error("Could not deploy the jar '" + jarPath
                    + "'. Please ensure this file exists in the specified"
                    + " location.");
            }
        }

        if (logService.isTraceEnabled()) {
            logService.trace("deployed files from jars - "
                + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    /**
     * Deploy files from the specified jar.
     * <p/>
     * Only files specified in the folder 'META-INF/web' will be deployed.
     *
     * @param jar the jar which resources will be deployed
     * @throws java.lang.Exception if for some reason the files cannot be
     * deployed
     */
    private void deployFilesInJar(File jar) throws Exception {
        if (jar == null) {
            throw new IllegalArgumentException("Jar cannot be null");
        }

        InputStream inputStream = null;
        JarInputStream jarInputStream = null;

        try {

            inputStream = new FileInputStream(jar);
            jarInputStream = new JarInputStream(inputStream);
            JarEntry jarEntry = null;

            // Indicates whether feedback should be logged about the files deployed
            // from jar
            boolean logFeedback = true;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                // jarEntryName example -> META-INF/web/click/table.css
                String jarEntryName = jarEntry.getName();

                // Deploy all resources under "META-INF/web/"
                int pathIndex = jarEntryName.indexOf("META-INF/web/");
                if (pathIndex == 0) {
                    if (logFeedback && logService.isTraceEnabled()) {
                        logService.trace("deploy files from jar -> "
                                         + jar.getCanonicalPath());

                        // Only provide feedback once per jar
                        logFeedback = false;
                    }
                    pathIndex += "META-INF/web/".length();

                    // By default deploy to the web root dir
                    String targetDir = "";

                    // resourceName example -> click/table.css
                    String resourceName = jarEntryName.substring(pathIndex);
                    int index = resourceName.lastIndexOf('/');

                    if (index != -1) {
                        // targetDir example -> click
                        targetDir = resourceName.substring(0, index);
                    }

                    // Copy resources to web folder
                    ClickUtils.deployFile(servletContext,
                        jarEntryName,
                        targetDir);
                }
            }
        } finally {
            ClickUtils.close(jarInputStream);
            ClickUtils.close(inputStream);
        }
    }

    private void loadMode(Element rootElm) {
        Element modeElm = ClickUtils.getChild(rootElm, "mode");

        String modeValue = "development";

        if (modeElm != null) {
            if (StringUtils.isNotBlank(modeElm.getAttribute("value"))) {
                modeValue = modeElm.getAttribute("value");
            }
        }

        modeValue = System.getProperty("click.mode", modeValue);

        if (modeValue.equalsIgnoreCase("production")) {
            mode = PRODUCTION;
        } else if (modeValue.equalsIgnoreCase("profile")) {
            mode = PROFILE;
        } else if (modeValue.equalsIgnoreCase("development")) {
            mode = DEVELOPMENT;
        } else if (modeValue.equalsIgnoreCase("debug")) {
            mode = DEBUG;
        } else if (modeValue.equalsIgnoreCase("trace")) {
            mode = TRACE;
        } else {
            logService.error("invalid application mode: " + modeValue);
            mode = DEBUG;
        }

        // Set log levels
        if (logService instanceof ConsoleLogService) {
            int logLevel = ConsoleLogService.INFO_LEVEL;

            if (mode == PRODUCTION) {
                logLevel = ConsoleLogService.WARN_LEVEL;

            } else if (mode == DEVELOPMENT) {

            } else if (mode == DEBUG) {
                logLevel = ConsoleLogService.DEBUG_LEVEL;

            } else if (mode == TRACE) {
                logLevel = ConsoleLogService.TRACE_LEVEL;
            }

            ((ConsoleLogService) logService).setLevel(logLevel);
        }
    }

    private void loadDefaultPages() throws ClassNotFoundException {

        if (!pageByPathMap.containsKey(ERROR_PATH)) {
            XmlConfigService.PageElm page =
                new XmlConfigService.PageElm("net.sf.click.util.ErrorPage", ERROR_PATH);

            pageByPathMap.put(ERROR_PATH, page);
        }

        if (!pageByPathMap.containsKey(NOT_FOUND_PATH)) {
            XmlConfigService.PageElm page =
                new XmlConfigService.PageElm("net.sf.click.Page", NOT_FOUND_PATH);

            pageByPathMap.put(NOT_FOUND_PATH, page);
        }
    }

    private void loadHeaders(Element rootElm) {
        Element headersElm = ClickUtils.getChild(rootElm, "headers");

        if (headersElm != null) {
            commonHeaders =
                Collections.unmodifiableMap(loadHeadersMap(headersElm));
        } else {
            commonHeaders = Collections.unmodifiableMap(DEFAULT_HEADERS);
        }
    }

    private void loadFormatClass(Element rootElm)
            throws ClassNotFoundException {

        Element formatElm = ClickUtils.getChild(rootElm, "format");

        if (formatElm != null) {
            String classname = formatElm.getAttribute("classname");

            if (classname == null) {
                String msg = "'format' element missing 'classname' attribute.";
                throw new RuntimeException(msg);
            }

            formatClass = ClickUtils.classForName(classname);

        } else {
            formatClass = net.sf.click.util.Format.class;
        }
    }

    private void loadFileUploadService(Element rootElm) throws Exception {

        Element fileUploadServiceElm = ClickUtils.getChild(rootElm, "file-upload-service");

        if (fileUploadServiceElm != null) {
            Class fileUploadServiceClass = CommonsFileUploadService.class;

            String classname = fileUploadServiceElm.getAttribute("classname");

            if (StringUtils.isNotBlank(classname)) {
                fileUploadServiceClass = ClickUtils.classForName(classname);
            }

            fileUploadService = (FileUploadService) fileUploadServiceClass.newInstance();

            Map propertyMap = loadPropertyMap(fileUploadServiceElm);

            for (Iterator i = propertyMap.keySet().iterator(); i.hasNext();) {
                String name = i.next().toString();
                String value = propertyMap.get(name).toString();

                Ognl.setValue(name, fileUploadService, value);
            }

        } else {
            fileUploadService = new CommonsFileUploadService();
        }

        if (getLogService().isDebugEnabled()) {
            String msg = "initializing FileLoadService: "
                + fileUploadService.getClass().getName();
            getLogService().debug(msg);
        }

        fileUploadService.onInit(servletContext);
    }

    private void loadLogService(Element rootElm) throws Exception {
        Element logServiceElm = ClickUtils.getChild(rootElm, "log-service");

        if (logServiceElm != null) {
            Class logServiceClass = ConsoleLogService.class;

            String classname = logServiceElm.getAttribute("classname");

            if (StringUtils.isNotBlank(classname)) {
                logServiceClass = ClickUtils.classForName(classname);
            }

            logService = (LogService) logServiceClass.newInstance();

            Map propertyMap = loadPropertyMap(logServiceElm);

            for (Iterator i = propertyMap.keySet().iterator(); i.hasNext();) {
                String name = i.next().toString();
                String value = propertyMap.get(name).toString();

                Ognl.setValue(name, logService, value);
            }

        } else {
            logService = new ConsoleLogService();
        }

        logService.onInit(getServletContext());

        if (getLogService().isDebugEnabled()) {
            String msg = "initialized LogService: "
                + logService.getClass().getName();
            getLogService().debug(msg);
        }
    }

    private void loadTemplateService(Element rootElm) throws Exception {
        Element templateServiceElm = ClickUtils.getChild(rootElm, "template-service");

        if (templateServiceElm != null) {
            Class templateServiceClass = VelocityTemplateService.class;

            String classname = templateServiceElm.getAttribute("classname");

            if (StringUtils.isNotBlank(classname)) {
                templateServiceClass = ClickUtils.classForName(classname);
            }

            templateService = (TemplateService) templateServiceClass.newInstance();

            Map propertyMap = loadPropertyMap(templateServiceElm);

            for (Iterator i = propertyMap.keySet().iterator(); i.hasNext();) {
                String name = i.next().toString();
                String value = propertyMap.get(name).toString();

                Ognl.setValue(name, templateService, value);
            }

        } else {
            templateService = new VelocityTemplateService();
        }

        if (getLogService().isDebugEnabled()) {
            String msg = "initializing TemplateService: "
                + templateService.getClass().getName();
            getLogService().debug(msg);
        }

        templateService.onInit(servletContext);
    }

    private static Map loadPropertyMap(Element parentElm) {
        Map propertyMap = new HashMap();

        List propertyList = ClickUtils.getChildren(parentElm, "property");

        for (int i = 0, size = propertyList.size(); i < size; i++) {
            Element property = (Element) propertyList.get(i);

            String name = property.getAttribute("name");
            String value = property.getAttribute("value");

            propertyMap.put(name, value);
        }

        return propertyMap;
    }

    private void loadCharset(Element rootElm) {
        String charset = rootElm.getAttribute("charset");
        if (charset != null && charset.length() > 0) {
            this.charset = charset;
        }
    }

    private void loadLocale(Element rootElm) {
        String value = rootElm.getAttribute("locale");
        if (value != null && value.length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(value, "_");
            if (tokenizer.countTokens() == 1) {
                String language = tokenizer.nextToken();
                locale = new Locale(language);
            } else if (tokenizer.countTokens() == 2) {
                String language = tokenizer.nextToken();
                String country = tokenizer.nextToken();
                locale = new Locale(language, country);
            }
        }
    }

    /**
     * Return the list of templates within the web application.
     *
     * @return list of all templates within the web application
     */
    private List getTemplateFiles() {
        List fileList = new ArrayList();

        Set resources = servletContext.getResourcePaths("/");

        // Add all resources withtin web application
        for (Iterator i = resources.iterator(); i.hasNext();) {
            String resource = (String) i.next();

            if (resource.endsWith(".htm") || resource.endsWith(".jsp")) {
                fileList.add(resource);

            } else if (resource.endsWith("/")) {
                if (!resource.equalsIgnoreCase("/WEB-INF/")) {
                    processDirectory(resource, fileList);
                }
            }
        }

        Collections.sort(fileList);

        return fileList;
    }

    private void processDirectory(String dirPath, List fileList) {
        Set resources = servletContext.getResourcePaths(dirPath);

        if (resources != null) {
            for (Iterator i = resources.iterator(); i.hasNext();) {
                String resource = (String) i.next();

                if (resource.endsWith(".htm") || resource.endsWith(".jsp")) {
                    fileList.add(resource);

                } else if (resource.endsWith("/")) {
                    processDirectory(resource, fileList);
                }
            }
        }
    }

    private Class getExcludesPageClass(String path) {
        for (int i = 0; i < excludesList.size(); i++) {
            XmlConfigService.ExcludesElm override =
                (XmlConfigService.ExcludesElm) excludesList.get(i);

            if (override.isMatch(path)) {
                return override.getPageClass();
            }
        }

        return null;
    }

    // ---------------------------------------------------------- Inner Classes

    static class PageElm {

        final Map fields;

        final Field[] fieldArray;

        final Map headers;

        final Class pageClass;

        final String path;

        public PageElm(Element element, String pagesPackage, Map commonHeaders)
            throws ClassNotFoundException {

            // Set headers
            Map aggregationMap = new HashMap(commonHeaders);
            Map pageHeaders = loadHeadersMap(element);
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
            String value = element.getAttribute("classname");
            if (value != null) {
                if (pagesPackage.trim().length() > 0) {
                    value = pagesPackage + "." + value;
                }
            } else {
                String msg = "No classname defined for page path " + path;
                throw new RuntimeException(msg);
            }

            pageClass = ClickUtils.classForName(value);

            if (!Page.class.isAssignableFrom(pageClass)) {
                String msg = "Page class " + value
                             + " is not a subclass of net.sf.click.Page";
                throw new RuntimeException(msg);
            }


            fieldArray = pageClass.getFields();

            fields = new HashMap();
            for (int i = 0; i < fieldArray.length; i++) {
                Field field = fieldArray[i];
                fields.put(field.getName(), field);
            }
        }

        private PageElm(String path, Class pageClass, Map commonHeaders) {

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

        public PageElm(String classname, String path)
            throws ClassNotFoundException {

            this.fieldArray = null;
            this.fields = Collections.EMPTY_MAP;
            this.headers = Collections.EMPTY_MAP;
            pageClass = ClickUtils.classForName(classname);
            this.path = path;
        }

        public Field[] getFieldArray() {
            return fieldArray;
        }

        public Map getFields() {
            return fields;
        }

        public Map getHeaders() {
            return headers;
        }

        public Class getPageClass() {
            return pageClass;
        }

        public String getPath() {
            return path;
        }
    }

    static class ExcludesElm {

        final Set pathSet = new HashSet();
        final Set fileSet = new HashSet();

        public ExcludesElm(Element element) throws ClassNotFoundException {

            String pattern = element.getAttribute("pattern");

            if (StringUtils.isNotBlank(pattern)) {
                StringTokenizer tokenizer = new StringTokenizer(pattern, ", ");
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();

                    if (token.charAt(0) != '/') {
                        token = "/" + token;
                    }

                    int index = token.lastIndexOf(".");
                    if (index != -1) {
                        token = token.substring(0, index);
                        fileSet.add(token);

                    } else {
                        index = token.indexOf("*");
                        if (index != -1) {
                            token = token.substring(0, index);
                        }
                        pathSet.add(token);
                    }
                }
            }
        }

        public Class getPageClass() {
            return XmlConfigService.ExcludePage.class;
        }

        public boolean isMatch(String resourcePath) {
            if (fileSet.contains(resourcePath)) {
                return true;
            }

            for (Iterator i = pathSet.iterator(); i.hasNext();) {
                String path = i.next().toString();
                if (resourcePath.startsWith(path)) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return getClass().getName()
                + "[fileSet=" + fileSet + ",pathSet=" + pathSet + "]";
        }
    }

    /**
     * Provide an Excluded Page class.
     * <p/>
     * <b>PLEASE NOTE</b> this class is <b>not</b> for public use, and can be
     * ignore.
     *
     * @author Malcolm Edgar
     */
    public static class ExcludePage extends Page {

        static final Map HEADERS = new HashMap();

        static {
            HEADERS.put("Cache-Control", "max-age=3600, public");
        }

        /**
         * @see Page#getHeaders()
         *
         * @return the map of HTTP header to be set in the HttpServletResponse
         */
        public Map getHeaders() {
            return HEADERS;
        }
    }

}
