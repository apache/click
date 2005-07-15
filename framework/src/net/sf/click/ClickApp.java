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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import net.sf.click.util.ClickUtils;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.tools.view.servlet.ServletLogger;
import org.apache.velocity.tools.view.servlet.WebappLoader;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Application the Click application object which defines the
 * application's configuration, intializes the Velocity Engine and provides
 * page templates.
 *
 * @author Malcolm Edgar
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

    // -------------------------------------------------------- Package Members

    /** The format class. */
    private Class formatClass;

    /** The application logger. */
    private final Logger logger;

    /**
     * The application mode:
     * [ PRODUCTION | PROFILE | DEVELOPMENT | DEBUG | TRACE ]
     */
    private int mode;

    /** The map of ClickApp.PageElm keyed on path. */
    private final Map pageByPathMap = new HashMap();

    /** The ServletContext instance. */
    private final ServletContext servletContext;

    /** The VelocityEngine instance. */
    private final VelocityEngine velocityEngine = new VelocityEngine();

    /**
     * Initialize the click application using the given servlet context.
     *
     * @param context the servlet context
     * @throws Exception if an error occurs initializing the application
     */
    ClickApp(ServletContext context) throws Exception {

        servletContext = context;

        logger = new Logger(servletContext);

        InputStream inputStream =
            servletContext.getResourceAsStream(DEFAULT_APP_CONFIG);

        if (inputStream == null) {
            throw new RuntimeException
                ("could not find click app configuration file: "
                 + DEFAULT_APP_CONFIG);
        }

        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setEntityResolver(this);

        try {
            Document document = saxBuilder.build(inputStream);

            Element rootElm = document.getRootElement();

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
                String loggerKey = ServletLogger.class.getName();
                ServletLogger servletLogger = (ServletLogger)
                    velocityEngine.getApplicationAttribute(loggerKey);
                servletLogger.setLogLevel(LogSystem.WARN_ID);
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
    Logger getLog() {
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
        PageElm page = (PageElm) pageByPathMap.get(path);

        if (page != null) {
            return page.getPageClass();
        } else {
            return null;
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
     * @throw Exception if Velocity error occurs
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

            // Deploy calendar image file
            deployFile("/net/sf/click/control/calendar.gif", clickTarget,
                       CLICK_PATH, "calendar.gif");

            // Deploy JavaScript file
            deployFile("/net/sf/click/control/calendar-en.js", clickTarget,
                       CLICK_PATH, "calendar-en.js");

            // Deploy CSS styles file
            deployFile("/net/sf/click/control/control.css", clickTarget,
                       CLICK_PATH, "control.css");

            // Deploy JavaScript file
            deployFile("/net/sf/click/control/control.js", clickTarget,
                       CLICK_PATH, "control.js");

            // Deploy error page file
            deployFile("/net/sf/click/util/error.htm", clickTarget,
                       CLICK_PATH, "error.htm");

            // Deploy page not found file
            deployFile("/net/sf/click/not-found.htm", clickTarget,
                       CLICK_PATH, "not-found.htm");

            // Deploy global VM file
            deployFile("/net/sf/click/control/VM_global_library.vm", clickTarget,
                       CLICK_PATH, "VM_global_library.vm");

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

                    if (logger.isDebugEnabled()) {
                        logger.debug
                            ("deployed " + dir + File.separator + filename);
                    }

                } catch (IOException ioe) {
                    logger.warn("could not deploy " + destination, ioe);
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
        Element modeElm = rootElm.getChild("mode");

        if (modeElm != null) {
            String modeValue = modeElm.getAttributeValue("value");

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
        } else {
            mode = DEVELOPMENT;
        }

        // Set Click and Velocity log levels
        int clickLogLevel = Logger.INFO_ID;
        Integer velocityLogLevel = new Integer(LogSystem.ERROR_ID);

        if (mode == PRODUCTION) {
            clickLogLevel = Logger.WARN_ID;

        } else if (mode == DEVELOPMENT) {
            velocityLogLevel = new Integer(LogSystem.WARN_ID);

        } else if (mode == DEBUG) {
            clickLogLevel = Logger.DEBUG_ID;
            velocityLogLevel = new Integer(LogSystem.INFO_ID);

        } else if (mode == TRACE) {
            clickLogLevel = Logger.TRACE_ID;
            velocityLogLevel = new Integer(LogSystem.INFO_ID);
        }

        logger.setLevel(clickLogLevel);
        velocityEngine.setApplicationAttribute
            (ServletLogger.LOG_LEVEL, velocityLogLevel);
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

        Element formatElm = rootElm.getChild("format");

        if (formatElm != null) {
            String classname = formatElm.getAttributeValue("classname");

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

    private void loadPages(Element rootElm)
        throws ClassNotFoundException, JDOMException {

        Element pagesElm = rootElm.getChild("pages");

        if (pagesElm == null) {
            throw new RuntimeException
                ("required configuration 'pages' element missing.");
        }

        Map headersMap = new HashMap();

        Element headersElm = rootElm.getChild("headers");
        if (headersElm != null) {
            headersMap = loadHeadersMap(headersElm);
        }

        List pageList = pagesElm.getChildren();
        for (int i = 0; i < pageList.size(); i++) {
            Element pageElm = (Element) pageList.get(i);

            if (pageElm.getName().equals("page")) {
                ClickApp.PageElm page =
                        new ClickApp.PageElm(pageElm, headersMap, formatClass);

                pageByPathMap.put(page.getPath(), page);

            } else {
                String msg = "click.xml <pages> contains a non <page>"
                    + " element: <" + pageElm.getName() + "/>";
                logger.warn(msg);
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
                     ServletLogger.class.getName());

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

        if (logger.isDebugEnabled()) {
            TreeMap sortedPropMap = new TreeMap();

            Iterator i = velProps.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                sortedPropMap.put(entry.getKey(), entry.getValue());
            }

            logger.debug("velocity properties: " + sortedPropMap);
        }

        return velProps;
    }

    private static Map loadHeadersMap(Element parentElm) {
        Map headersMap = new HashMap();

        List headerList = parentElm.getChildren("header");

        for (int i = 0, size = headerList.size(); i < size; i++) {
            Element header = (Element) headerList.get(i);

            String name = header.getAttributeValue("name");
            String type = header.getAttributeValue("type");
            String propertyValue = header.getAttributeValue("value");

            Object value = null;

            if (type == null || "String".equalsIgnoreCase(type)) {
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

    // ---------------------------------------------------------- Inner Classes

    /**
     * ServletContext log adaptor derived from:
     * org.apache.velocity.tools.view.servlet.ServletLogger
     *
     * @author Malcolm Edgar
     */
    static class Logger {

        final static int TRACE_ID = LogSystem.DEBUG_ID - 1;

        final static int DEBUG_ID = LogSystem.DEBUG_ID;

        final static int INFO_ID = LogSystem.INFO_ID;

        final static int WARN_ID = LogSystem.WARN_ID;

        final static int ERROR_ID = LogSystem.ERROR_ID;

        static final String PREFIX = " Click";

        static final String TRACE_PREFIX = " [trace] ";

        ServletContext servletContext = null;

        int logLevel = DEBUG_ID;

        private Logger(ServletContext context) {
            servletContext = context;
        }

        void debug(Object message) {
            log(DEBUG_ID, String.valueOf(message));
        }

        void debug(Object message, Throwable error) {
            log(DEBUG_ID, String.valueOf(message), error);
        }

        void error(String message) {
            log(ERROR_ID, message);
        }

        void error(Object message, Throwable error) {
            log(ERROR_ID, String.valueOf(message), error);
        }

        void info(Object message) {
            log(INFO_ID, String.valueOf(message));
        }

        void trace(Object message) {
            log(TRACE_ID, String.valueOf(message));
        }

        void trace(Object message, Throwable error) {
            log(TRACE_ID, String.valueOf(message), error);
        }

        void warn(Object message) {
            log(WARN_ID, String.valueOf(message));
        }

        void warn(Object message, Throwable error) {
            log(WARN_ID, String.valueOf(message), error);
        }

        boolean isTraceEnabled() {
            return logLevel <= TRACE_ID;
        }

        boolean isDebugEnabled() {
            return logLevel <= DEBUG_ID;
        }

        boolean isInfoEnabled() {
            return logLevel <= INFO_ID;
        }

        private void setLevel(int level) {
            logLevel = level;
        }

        private void log(int level, String message) {
            if (level < logLevel) {
                return;
            }

            if (level == LogSystem.WARN_ID) {
                servletContext.log(PREFIX + RuntimeConstants.WARN_PREFIX + message);
            }
            else if (level == LogSystem.INFO_ID){
                servletContext.log( PREFIX + RuntimeConstants.INFO_PREFIX + message);
            }
            else if (level == LogSystem.DEBUG_ID) {
                servletContext.log( PREFIX + RuntimeConstants.DEBUG_PREFIX + message);
            }
            else if (level == LogSystem.ERROR_ID) {
                servletContext.log( PREFIX + RuntimeConstants.ERROR_PREFIX + message);
            }
            else if (level == TRACE_ID) {
                servletContext.log( PREFIX + TRACE_PREFIX + message);
            }
        }

        private void log(int level, String message, Throwable error) {
            if (level < logLevel) {
                return;
            }

            Throwable cause = null;
            if (error != null) {
                if (error instanceof ServletException) {
                    cause = ((ServletException) error).getRootCause();
                }
                if (cause == null && error.getCause() != null) {
                    cause = error.getCause();
                } else {
                    cause = error;
                }
            }

            if (level == LogSystem.WARN_ID) {
                servletContext.log(PREFIX + RuntimeConstants.WARN_PREFIX + message, cause);
            }
            else if (level == LogSystem.INFO_ID){
                servletContext.log( PREFIX + RuntimeConstants.INFO_PREFIX + message, cause);
            }
            else if (level == LogSystem.DEBUG_ID) {
                servletContext.log( PREFIX + RuntimeConstants.DEBUG_PREFIX + message, cause);
            }
            else if (level == LogSystem.ERROR_ID) {
                servletContext.log( PREFIX + RuntimeConstants.ERROR_PREFIX + message, cause);
            }
            else if (level == TRACE_ID) {
                servletContext.log( PREFIX + TRACE_PREFIX + message, cause);
            }
        }
    }

    private static class PageElm {

        private final Class formatClass;

        private final Map headers;

        private final Class pageClass;

        private final String path;

        private PageElm(Element element, Map commonHeaders, Class formatClass)
            throws ClassNotFoundException {

            // Set formatClass
            this.formatClass = formatClass;

            // Set headers
            Map aggregationMap = new HashMap(commonHeaders);
            Map pageHeaders = loadHeadersMap(element);
            aggregationMap.putAll(pageHeaders);
            headers = Collections.unmodifiableMap(aggregationMap);

            // Set pageClass
            String value = element.getAttributeValue("classname");
            value = (value != null) ? value : "net.sf.click.Page";

            pageClass = Class.forName(value);

            // Set path
            path = element.getAttributeValue("path");
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
