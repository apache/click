/*
 * Copyright 2004-2005 Malcolm A. Edgar
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.view.servlet.WebappLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Application the Click application object which defines the
 * application's configuration, intializes the Velocity Engine and provides
 * page templates.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
class ClickApp implements EntityResolver {

    /**
     * The default Click configuration filename: &nbsp;
     * "<tt>/WEB-INF/click.xml</tt>"
     */
    static final String DEFAULT_APP_CONFIG = "/WEB-INF/click.xml";

    /**
     * The default velocity properties filename: &nbsp;
     * "<tt>/WEB-INF/velocity.properties</tt>"
     */
    static final String DEFAULT_VEL_PROPS = "/WEB-INF/velocity.properties";

    /** The name of the Click logger: &nbsp; "<tt>net.sf.click</tt>" */
    static final String CLICK_LOGGER = "net.sf.click";

    /**
     * The name of the Velocity logger: &nbsp; "<tt>org.apache.velocity</tt>"
     */
    static final String VELOCITY_LOGGER = "org.apache.velocity";

    /** The click deployment directory path: &nbsp; "click" */
    static final String CLICK_PATH = "click";

    /** The calendar deployment directory path: &nbsp; "click/calendar" */
    static final String CALENDAR_PATH = "click" + File.separator + "calendar";

    /** The click DTD file name: &nbsp; "<tt>click.dtd</tt>" */
    static final String DTD_FILE_NAME = "click.dtd";

    /**
     * The resource path of the click DTD file: &nbsp;
     * "<tt>/net/sf/click/click.dtd</tt>";
     */
    static final String DTD_FILE_PATH = "/net/sf/click/" + DTD_FILE_NAME;

    /** The error page file name: &nbsp; "<tt>error.html</tt>" */
    static final String ERROR_FILE_NAME = "error.htm";

    static final String ERROR_PATH = CLICK_PATH + "/" + ERROR_FILE_NAME;

    /** The page not found file name: &nbsp; "<tt>not-found.html</tt>" */
    static final String NOT_FOUND_FILE_NAME = "not-found.htm";

    static final String NOT_FOUND_PATH = CLICK_PATH + "/" + NOT_FOUND_FILE_NAME;

    /**
     * The global Velocity macro file name: &nbsp;
     * "<tt>VM_global_library.vm</tt>"
     */
    static final String VM_FILE_NAME = "VM_global_library.vm";

    /**
     * The user supplied macro file name: &nbsp; "<tt>macro.vm</tt>"
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

    static final String[] CALENDAR_RESOURCES =
        { ".gif", ".js", "-de.js", "-en.js", "-es.js", "-fr.js", "-ko.js",
          "-it.js", "-ja.js", "-ru.js", "-zh.js", "-blue.css", "-blue2.css",
          "-brown.css", "-green.css", "-system.css", "-tas.css",
          "-win2k-1.css", "-win2k-2.css", "-win2k-cold-1.css",
          "-win2k-cold-2.css" };

    private static final Object PAGE_LOAD_LOCK = new Object();

    // -------------------------------------------------------- Package Members

    /** The format class. */
    private Class formatClass;

    /** The Map of global page headers. */
    private Map commonHeaders;

    /** The application logger. */
    private ClickLogger logger;

    /**
     * The application mode:
     * [ PRODUCTION | PROFILE | DEVELOPMENT | DEBUG | TRACE ]
     */
    private int mode;

    /** The map of ClickApp.PageElm keyed on path. */
    private final Map pageByPathMap = new HashMap();

    /** The pages package prefix. */
    private String pagesPackage;

    /** The ServletContext instance. */
    private ServletContext servletContext;

    /** The VelocityEngine instance. */
    private final VelocityEngine velocityEngine = new VelocityEngine();

    // --------------------------------------------------------- Public Methods

    /**
     * Initialize the click application using the given servlet context.
     *
     * @param context the servlet context
     * @throws Exception if an error occurs initializing the application
     */
    void init(ServletContext context) throws Exception {

        servletContext = context;

        logger = new ClickLogger("Click");

        InputStream inputStream =
            servletContext.getResourceAsStream(DEFAULT_APP_CONFIG);

        if (inputStream == null) {
            throw new RuntimeException
                ("could not find click app configuration file: "
                 + DEFAULT_APP_CONFIG);
        }

        try {
            Document document = ClickUtils.buildDocument(inputStream, this);

            Element rootElm = document.getDocumentElement();

            // Load the application mode and set the logger levels
            loadMode(rootElm);

            // Load the format class
            loadFormatClass(rootElm);

            // Load the pages
            loadPages(rootElm);

            // Load the error and not-found pages
            loadDefaultPages();

            // Deploy the application files if not present
            deployFiles();

            // Set ServletContext instance for WebappLoader
            velocityEngine.setApplicationAttribute
                (ServletContext.class.getName(), servletContext);

            // Load velocity properties
            Properties properties = getVelocityProperties(servletContext);

            // Initialize VelocityEngine
            velocityEngine.init(properties);

            // Turn down the Velocity logging level
            if (mode == DEBUG || mode == TRACE) {
                ClickLogger logger = ClickLogger.getInstance(velocityEngine);
                logger.setLevel(ClickLogger.WARN_ID);
            }

            // Cache page templates.
            loadTemplates();

        } finally {
            ClickUtils.close(inputStream);
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method resolves the click.dtd for the XML parser using the
     * classpath resource: <tt>/net/sf/click/click.dtd</tt>
     *
     * @see EntityResolver#resolveEntity(String, String)
     */
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {

        InputStream inputStream = getClass().getResourceAsStream(DTD_FILE_PATH);

        if (inputStream != null) {
            return new InputSource(inputStream);
        } else {
            throw new IOException("could not load resource: " + DTD_FILE_PATH);
        }
    }

    // -------------------------------------------------------- Package Methods

    /**
     * Return the application logger.
     *
     * @return the application logger.
     */
    ClickLogger getLogger() {
        return logger;
    }

    /**
     * Return true if the application is in PRODUCTION mode.
     *
     * @return true if the application is in PRODUCTION mode
     */
    boolean isProductionMode() {
        return (mode == PRODUCTION);
    }

    /**
     * Return the application mode String value: &nbsp; <tt>["production",
     * "profile", "development", "debug"]</tt>.
     *
     * @return the application mode String value
     */
    String getModeValue() {
        return MODE_VALUES[mode];
    }

    /**
     * Return the page <tt>Class</tt> for the given path.
     *
     * @param path the page path
     * @return the page class
     */
    Class getPageClass(String path) {

        // If in Pproduction or profile mode.
        if (mode <= PROFILE) {
            PageElm page = (PageElm) pageByPathMap.get(path);
            if (page != null) {
                return page.getPageClass();
            } else {
                return null;
            }

        // Else in development, debug or trace mode
        } else {

            synchronized(PAGE_LOAD_LOCK) {
                PageElm page = (PageElm) pageByPathMap.get(path);
                if (page != null) {
                    return page.getPageClass();
                }

                Class pageClass = null;
                if (servletContext.getResourcePaths(path) != null) {
                    pageClass = getPageClass(path, pagesPackage);

                    if (pageClass != null) {
                        page = new
                            PageElm(path, pageClass, commonHeaders, formatClass);

                        pageByPathMap.put(page.getPath(), page);

                        if (logger.isDebugEnabled()) {
                            String msg = path + " -> " + pageClass.getName();
                            logger.debug(msg);
                        }
                    }
                }
                return pageClass;
            }
        }
    }

    /**
     * Return a new format object for page of the given path.
     *
     * @param path the path of the page
     * @return a new format object for page of the given path
     */
    Object getPageFormat(String path) {
        PageElm page = (PageElm) pageByPathMap.get(path);

        if (page != null) {
            try {
                return page.getFormatClass().newInstance();
            } catch (IllegalAccessException iae) {
                throw new RuntimeException(iae);
            } catch (InstantiationException ie) {
                throw new RuntimeException(ie);
            }
        } else {
            return null;
        }
    }

    /**
     * Return the headers of the page for the given path.
     *
     * @param path the path of the page
     * @return a Map of headers for the given page path
     */
    Map getPageHeaders(String path) {
        PageElm page = (PageElm) pageByPathMap.get(path);

        if (page != null) {
            return page.getHeaders();
        } else {
            return null;
        }
    }

    /**
     * Return the page not found <tt>Page</tt> <tt>Class</tt>.
     *
     * @return the page not found <tt>Page</tt> <tt>Class</tt>
     */
    Class getNotFoundPageClass() {
        PageElm page = (PageElm) pageByPathMap.get(NOT_FOUND_PATH);

        if (page != null) {
            return page.getPageClass();

        } else {
            return net.sf.click.Page.class;
        }
    }

    /**
     * Return the error handling page <tt>Page</tt> <tt>Class</tt>.
     *
     * @return the error handling page <tt>Page</tt> <tt>Class</tt>
     */
    Class getErrorPageClass() {
        PageElm page = (PageElm) pageByPathMap.get(ERROR_PATH);

        if (page != null) {
            return page.getPageClass();

        } else {
            return net.sf.click.util.ErrorPage.class;
        }
    }

    /**
     * Return the Velocity Template for the give page path.
     *
     * @return the Velocity Template for the give page path
     * @throws Exception if Velocity error occurs
     */
    Template getTemplate(String path) throws Exception {
        return velocityEngine.getTemplate(path);
    }

    // -------------------------------------------------------- Private Methods

    private void deployFiles() {
        final String path = servletContext.getRealPath("/");

        if (path != null) {

            final String clickTarget = path + File.separator + CLICK_PATH;

            // Create files deployment directory
            File clickDir = new File(clickTarget);
            if (!clickDir.exists()) {
                if (!clickDir.mkdir()) {
                    logger.error
                        ("could not create deployment directory: " + clickDir);
                }
            }

            deployFile("/net/sf/click/control/control.css", clickTarget,
                       CLICK_PATH, "control.css");

            deployFile("/net/sf/click/control/control.js", clickTarget,
                       CLICK_PATH, "control.js");

            deployFile("/net/sf/click/util/error.htm", clickTarget,
                       CLICK_PATH, "error.htm");

            deployFile("/net/sf/click/not-found.htm", clickTarget,
                       CLICK_PATH, "not-found.htm");

            deployFile("/net/sf/click/control/VM_global_library.vm", clickTarget,
                       CLICK_PATH, "VM_global_library.vm");

            // Create click/calendar deployment directory
            final String calendarTarget = path + File.separator + CALENDAR_PATH;

            File calendarDir = new File(calendarTarget);
            if (!calendarDir.exists()) {
                if (!calendarDir.mkdir()) {
                    logger.error
                        ("could not create deployment directory: " + calendarDir);
                }
            }

            // Deploy DateField resources files
            for (int i = 0; i < CALENDAR_RESOURCES.length; i++) {
                String calendarResource = "calendar" + CALENDAR_RESOURCES[i];
                deployFile("/net/sf/click/control/calendar/" + calendarResource,
                           calendarTarget,
                           CALENDAR_PATH,
                           calendarResource);
            }

            // Find extra files to deploy
            try {
                ResourceBundle bundle =
                    ResourceBundle.getBundle("click-deploy");

                String filenames = bundle.getString("click.deploy.files");
                if (filenames != null) {
                    StringTokenizer tokens =
                            new StringTokenizer(filenames, ",");

                    while (tokens.hasMoreTokens()) {
                        String key = "click.deploy.path." + tokens.nextToken();

                        String filepath = bundle.getString(key);

                        if (filepath != null) {
                            String filename =
                                filepath.substring(filepath.lastIndexOf('/') + 1);

                            deployFile(filepath,
                                      clickTarget,
                                      CLICK_PATH,
                                      filename);
                        } else {
                            logger.warn("Could not find property: " + key);
                        }
                    }

                } else {
                    logger.warn("could not load /click-deploy.properties");
                }

            } catch (MissingResourceException mre) {
                logger.info("/click-deploy.properties file not found");
            }

        } else {
            logger.error("Servlet real path is null. Could not deploy files");
        }
    }

    private void deployFile(String resource, String path, String dir, String filename) {
        String destination = path;

        if (path.endsWith(File.separator)
            || filename.startsWith(File.separator)) {

            destination = destination + filename;
        } else {
            destination =
                destination + File.separator + File.separator + filename;
        }

        File destinationFile = new File(destination);

        if (!destinationFile.exists()) {
            InputStream inputStream =
                getClass().getResourceAsStream(resource);

            if (inputStream != null) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(destinationFile);
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int length = inputStream.read(buffer);
                        if (length <  0) {
                            break;
                        }
                        fos.write(buffer, 0, length);
                    }

                    if (logger.isTraceEnabled()) {
                        logger.trace
                            ("deployed " + dir + File.separator + filename);
                    }

                } catch (IOException ioe) {
                    logger.error("could not deploy " + destination, ioe);

                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException ioe) {
                            // ignore
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ioe) {
                            // ignore
                        }
                    }
                }
            } else {
                logger.warn("could not locate classpath resource: " + resource);
            }
        }
    }

    private void loadMode(Element rootElm) {
        Element modeElm = getChild(rootElm, "mode");

        String logto = "console";

        if (modeElm != null) {
            String modeValue = modeElm.getAttribute("value");

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
                logger.error("invalid application mode: " + mode);
                mode = DEBUG;
            }

            // Configure loggig to console or servlet context.
            logto = modeElm.getAttribute("logto");
            if (StringUtils.isBlank(logto)) {
                logto = "console";
            }

        } else {
            mode = DEVELOPMENT;
        }

        // Set Click and Velocity log levels
        int clickLogLevel = ClickLogger.INFO_ID;
        Integer velocityLogLevel = new Integer(ClickLogger.ERROR_ID);

        if (mode == PRODUCTION) {
            clickLogLevel = ClickLogger.WARN_ID;

        } else if (mode == DEVELOPMENT) {
            velocityLogLevel = new Integer(ClickLogger.WARN_ID);

        } else if (mode == DEBUG) {
            clickLogLevel = ClickLogger.DEBUG_ID;
            velocityLogLevel = new Integer(ClickLogger.WARN_ID);

        } else if (mode == TRACE) {
            clickLogLevel = ClickLogger.TRACE_ID;
            velocityLogLevel = new Integer(ClickLogger.INFO_ID);
        }

        logger.setLevel(clickLogLevel);
        velocityEngine.setApplicationAttribute
            (ClickLogger.LOG_LEVEL, velocityLogLevel);

        if (logto.equalsIgnoreCase("servlet")) {
            logger.setServletContext(servletContext);
            velocityEngine.setApplicationAttribute
                (ClickLogger.LOG_TO, "servlet");

        } else if (logto.equalsIgnoreCase("console")) {
            // Do nothing

        } else {
            String msg = "Invalid mode logto attribute '" + logto +
                         "' logging to console instead.";
            logger.warn(msg);
        }
    }

    private void loadDefaultPages() throws ClassNotFoundException {

        if (!pageByPathMap.containsKey(ERROR_PATH)) {
            ClickApp.PageElm page = new ClickApp.PageElm
                ("net.sf.click.util.ErrorPage", ERROR_PATH, formatClass);

            pageByPathMap.put(ERROR_PATH, page);
        }

        if (!pageByPathMap.containsKey(NOT_FOUND_PATH)) {
            ClickApp.PageElm page = new ClickApp.PageElm
                ("net.sf.click.Page", NOT_FOUND_PATH, formatClass);

            pageByPathMap.put(NOT_FOUND_PATH, page);
        }
    }

    private void loadFormatClass(Element rootElm) throws ClassNotFoundException {
        Element formatElm = getChild(rootElm, "format");

        if (formatElm != null) {
            String classname = formatElm.getAttribute("classname");

            if (classname == null) {
                throw new RuntimeException
                    ("'format' element missing 'classname' attribute.");
            }

            formatClass = Class.forName(classname);

        } else {
            formatClass = net.sf.click.util.Format.class;
        }
    }

    private void loadTemplates() throws Exception {
        if (mode == ClickApp.PRODUCTION || mode == ClickApp.PROFILE) {

            // Load page templates, which will be cached in ResourceManager
            for (Iterator i = pageByPathMap.keySet().iterator(); i.hasNext();) {
                String path = i.next().toString();

                try {
                    getTemplate(path);
                    if (logger.isDebugEnabled()) {
                        logger.debug("loaded page template " + path);
                    }
                } catch (ParseErrorException pee) {
                    logger.warn("Errors in page '" + path, pee);
                }
            }
        }
    }

    private void loadPages(Element rootElm) throws ClassNotFoundException {
        Element pagesElm = getChild(rootElm, "pages");

        if (pagesElm == null) {
            throw new RuntimeException
                ("required configuration 'pages' element missing.");
        }

        pagesPackage = pagesElm.getAttribute("package");
        if (StringUtils.isBlank(pagesPackage)) {
            pagesPackage = "";
        }

        pagesPackage = pagesPackage.trim();
        if (pagesPackage.endsWith(".")) {
            pagesPackage =
                pagesPackage.substring(0, pagesPackage.length() - 2);
        }

        boolean automap = false;
        String automapStr = pagesElm.getAttribute("automapping");
        if (StringUtils.isBlank(automapStr)) {
            automapStr = "false";
        }

        if ("true".equalsIgnoreCase(automapStr)) {
            automap = true;
        } else if ("false".equalsIgnoreCase(automapStr)) {
            automap = false;
        } else {
            String msg = "Invalid pages automap attribute: " + automapStr;
            throw new RuntimeException(msg);
        }

        Element headersElm = getChild(rootElm, "headers");
        if (headersElm != null) {
            commonHeaders =
                Collections.unmodifiableMap(loadHeadersMap(headersElm));
        } else {
            commonHeaders = Collections.EMPTY_MAP;
        }

        List pageList = getChildren(pagesElm, "page");

        if (!pageList.isEmpty() && logger.isDebugEnabled()) {
            logger.debug("click.xml pages:");
        }

        for (int i = 0; i < pageList.size(); i++) {
            Element pageElm = (Element) pageList.get(i);

            ClickApp.PageElm page = new ClickApp.PageElm(pageElm,
                    pagesPackage,
                    commonHeaders,
                    formatClass);

            pageByPathMap.put(page.getPath(), page);

            if (logger.isDebugEnabled()) {
                String msg =
                    page.getPath() + " -> " + page.getPageClass().getName();
                logger.debug(msg);
            }
        }

        if (automap) {
            if (logger.isDebugEnabled()) {
                logger.debug("automapped pages:");
            }

            List templates = getTemplateFiles();

            for (int i = 0; i < templates.size(); i++) {
                String pagePath = (String) templates.get(i);

                if (!pageByPathMap.containsKey(pagePath)) {

                    Class pageClass = getPageClass(pagePath, pagesPackage);

                    if (pageClass != null) {
                        ClickApp.PageElm page = new ClickApp.PageElm(pagePath,
                                pageClass,
                                commonHeaders,
                                formatClass);

                        pageByPathMap.put(page.getPath(), page);

                        if (logger.isDebugEnabled()) {
                            String msg = pagePath + " -> " + pageClass.getName();
                            logger.debug(msg);
                        }
                    }
                }
            }

        }
    }

    private Properties getVelocityProperties(ServletContext context)
            throws Exception {

        final Properties velProps = new Properties();

        // Set default velocity runtime properties.

        velProps.setProperty(RuntimeConstants.RESOURCE_LOADER, "webapp");
        velProps.setProperty("webapp.resource.loader.class",
                             WebappLoader.class.getName());

        if (mode == PRODUCTION || mode == PROFILE) {
            velProps.put("webapp.resource.loader.cache", "true");
            velProps.put("webapp.resource.loader.modificationCheckInterval",
                         "0");
            velProps.put("velocimacro.library.autoreload", "false");
        } else {
            velProps.put("webapp.resource.loader.cache", "false");
            velProps.put("velocimacro.library.autoreload", "true");
        }

        velProps.put(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                     ClickLogger.class.getName());

        // If 'macro.vm' exists set it as default VM library, otherwise use
        // 'click/VM_global_library.vm'
        String macroPath = context.getRealPath("/" + MACRO_VM_FILE_NAME);
        if (macroPath != null) {
            File file = new File(macroPath);
            if (file.canRead() && file.isFile()) {
                velProps.put("velocimacro.library", MACRO_VM_FILE_NAME);
            } else {
                velProps.put("velocimacro.library", CLICK_PATH + File.separator
                        + VM_FILE_NAME);
            }
        } else {
            velProps.put("velocimacro.library", CLICK_PATH + File.separator
                         + VM_FILE_NAME);
        }

        // Load user velocity properties.
        Properties userProperties = new Properties();

        String filename = DEFAULT_VEL_PROPS;

        InputStream inputStream = context.getResourceAsStream(filename);

        if (inputStream != null) {
            try {
                userProperties.load(inputStream);

            } catch (IOException ioe) {
                String message = "error loading velocity properties file: "
                        + filename;
                logger.error(message, ioe);

            } finally {
                try {
                    inputStream.close();
                } catch (IOException ioe) {
                    // ignore
                }
            }
        }

        // Add user properties.
        Iterator iterator = userProperties.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();

            Object pop = velProps.put(entry.getKey(), entry.getValue());
            if (pop != null && logger.isDebugEnabled()) {
                String message = "user defined property '" + entry.getKey()
                        + "=" + entry.getValue()
                        + "' replaced default propery '" +  entry.getKey()
                        + "=" + pop + "'";
                logger.debug(message);
            }
        }

        if (logger.isTraceEnabled()) {
            TreeMap sortedPropMap = new TreeMap();

            Iterator i = velProps.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                sortedPropMap.put(entry.getKey(), entry.getValue());
            }

            logger.trace("velocity properties: " + sortedPropMap);
        }

        return velProps;
    }

    private static Map loadHeadersMap(Element parentElm) {
        Map headersMap = new HashMap();

        List headerList = getChildren(parentElm, "header");

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

    private List getTemplateFiles() {
        List fileList = new ArrayList();

        Set resources = servletContext.getResourcePaths("/");

        for (Iterator i = resources.iterator(); i.hasNext();) {
            String resource = (String) i.next();

            if (resource.endsWith(".htm")) {
                fileList.add(resource);

            } else if (resource.endsWith("/")) {
                if (!resource.equals("/click/") &&
                    !resource.equalsIgnoreCase("/WEB-INF/")) {
                    processDirectory(resource, fileList);
                }
            }
        }

        return fileList;
    }

    private void processDirectory(String dirPath, List fileList) {
        Set resources = servletContext.getResourcePaths(dirPath);

        for (Iterator i = resources.iterator(); i.hasNext();) {
            String resource = (String) i.next();

            if (resource.endsWith(".htm")) {
                fileList.add(resource);
            } else if (resource.endsWith("/")) {
                processDirectory(resource, fileList);
            }
        }
    }

    private Class getPageClass(final String pagePath, final String pagesPackage) {
        String packageName = pagesPackage + ".";
        String className = "";

        // Strip off .htm extension
        String path = pagePath.substring(0, pagePath.lastIndexOf("."));

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

        StringTokenizer tokenizer = new StringTokenizer(className, "_-");
        className = "";
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = Character.toUpperCase(token.charAt(0)) + token.substring(1);
            className += token;
        }

        className = packageName + className;

        Class pageClass = null;
        try {
            pageClass = Class.forName(className);

            if (!Page.class.isAssignableFrom(pageClass)) {
                String msg = "Automapped page class " + className +
                             " is not a subclass of net.sf.clic.Page";
                throw new RuntimeException(msg);
            }

        } catch (ClassNotFoundException cnfe) {
            if (logger.isDebugEnabled()) {
                logger.debug(pagePath + " -> CLASS NOT FOUND");
            }
            if (logger.isTraceEnabled()) {
                logger.trace("class not found: " + className);
            }
        }

        return pageClass;
    }

    private Element getChild(Element element, String name) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                if (node.getNodeName().equals(name)) {
                    return (Element) node;
                }
            }
        }
        return null;
    }

    private static List getChildren(Element element, String name) {
        List list = new ArrayList();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                if (node.getNodeName().equals(name)) {
                    list.add(node);
                }
            }
        }
        return list;
    }

    // ---------------------------------------------------------- Inner Classes

    private static class PageElm {

        private final Class formatClass;

        private final Map headers;

        private final Class pageClass;

        private final String path;

        private PageElm(Element element, String pagesPackage, Map commonHeaders,
                Class formatClass) throws ClassNotFoundException {

            // Set formatClass
            this.formatClass = formatClass;

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

            pageClass = Class.forName(value);

            if (!Page.class.isAssignableFrom(pageClass)) {
                String msg = "Page class " + value +
                             " is not a subclass of net.sf.clic.Page";
                throw new RuntimeException(msg);
            }
        }

        private PageElm(String path, Class pageClass, Map commonHeaders,
                Class formatClass) {

            this.formatClass = formatClass;
            headers = Collections.unmodifiableMap(commonHeaders);
            this.pageClass = pageClass;
            this.path = path;
        }

        private PageElm(String classname, String path, Class formatClass)
            throws ClassNotFoundException {

            this.formatClass = formatClass;
            this.headers = Collections.EMPTY_MAP;
            pageClass = Class.forName(classname);
            this.path = path;
        }

        private Class getFormatClass() {
            return formatClass;
        }

        private Map getHeaders() {
            return headers;
        }

        private Class getPageClass() {
            return pageClass;
        }

        private String getPath() {
            return path;
        }
    }

}
