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

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.util.Format;

/**
 * Provides a Click application configuration service interface.
 * <p/>
 * A single application ConfigService instance is created by the ClickServlet at
 * startup. Once the ConfigService has been initialized it is stored in the
 * ServletContext using the key "<tt>net.sf.click.service.ConfigService</tt>".
 *
 * @author Malcolm Edgar
 */
public interface ConfigService {

    /** The trace application mode. */
    public static final String MODE_TRACE = "trace";

    /** The debug application mode. */
    public static final String MODE_DEBUG = "trace";

    /** The development application mode. */
    public static final String MODE_DEVELOPMENT = "development";

    /** The profile application mode. */
    public static final String MODE_PROFILE = "profile";

    /** The profile application mode. */
    public static final String MODE_PRODUCTION = "production";

    /** The error page file path: &nbsp; "<tt>/click/error.htm</tt>". */
    static final String ERROR_PATH = "/click/error.htm";

    /** The page not found file path: &nbsp; "<tt>/click/not-found.htm</tt>". */
    public static final String NOT_FOUND_PATH = "/click/not-found.htm";

    /**
     * The servlet context attribute name. The ClickServlet stores the
     * application ConfigService instance in the ServletContext using this
     * context attribute name.
     */
    public static final String CONTEXT_NAME = "net.sf.click.service.ConfigService";

    /**
     * Initialize the ConfigurationService with the given application servlet context.
     * <p/>
     * This method is invoked after the ConfigurationService has been constructed.
     *
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the ConfigurationService
     */
    public void onInit(ServletContext servletContext) throws Exception;

    /**
     * Destroy the ConfigurationService. This method will also invoke the
     * <tt>onDestory()</tt> methods on the <tt>FileUploadService</tt> and the
     * <tt>TemplateService</tt>.
     */
    public void onDestroy();

    /**
     * Return the application file upload service, which is used to parse
     * multi-part file upload post requests.
     *
     * @return the application file upload service
     */
    public FileUploadService getFileUploadService();

    /**
     * Return the application templating service.
     *
     * @return the application templating service
     */
    public TemplateService getTemplateService();

    /**
     * Return the Click application mode value: &nbsp;
     * <tt>["production", "profile", "development", "debug", "trace"]</tt>.
     *
     * @return the application mode value
     */
    public String getApplicationMode();

    /**
     * Return the Click application charset or null if not defined.
     *
     * @return the application charset value
     */
    public String getCharset();

    /**
     * Return the error handling page <tt>Page</tt> <tt>Class</tt>.
     *
     * @return the error handling page <tt>Page</tt> <tt>Class</tt>
     */
    public Class getErrorPageClass();

    /**
     * Create and return a new format object instance.
     *
     * @return a new format object instance
     */
    public Format createFormat();

    /**
     * Return true if JSP exists for the given ".htm" path.
     *
     * @param path the Page ".htm" path
     * @return true if JSP exists for the given ".htm" path
     */
    public boolean isJspPage(String path);

    /**
     * Return true if auto binding is enabled. Autobinding will automatically
     * bind any request parameters to public fields, add any public controls to
     * the page and add public fields to the page model.
     *
     * @return true if request parameters should be automatically bound to public
     * page fields
     */
    public boolean isPagesAutoBinding();

    /**
     * Return true if the application is in "production" mode.
     *
     * @return true if the application is in "production" mode
     */
    public boolean isProductionMode();

    /**
     * Return true if the application is in "profile" mode.
     *
     * @return true if the application is in "profile" mode
     */
    public boolean isProfileMode();

    /**
     * Return the Click application locale or null if not defined.
     *
     * @return the application locale value
     */
    public Locale getLocale();

    /**
     * Return the Click application log service.
     *
     * @return the application log service.
     */
    public LogService getLogService();

    /**
     * Return the path for the given page Class.
     *
     * @param pageClass the class of the Page to lookup the path for
     * @return the path for the given page Class
     * @throws IllegalArgumentException if the Page Class is not configured
     * with a unique path
     */
    public String getPagePath(Class pageClass);

    /**
     * Return the page <tt>Class</tt> for the given path.
     *
     * @param path the page path
     * @return the page class for the given path
     * @throws IllegalArgumentException if the Page Class for the path is not
     * found
     */
    public Class getPageClass(String path);

    /**
     * Return Map of public fields for the given page class.
     *
     * @param pageClass the page class
     * @return a Map of public fields for the given page class
     */
    public Map getPageFields(Class pageClass);

    /**
     * Return the public field of the given name for the pageClass,
     * or null if not defined.
     *
     * @param pageClass the page class
     * @param fieldName the name of the field
     * @return the public field of the pageClass with the given name or null
     */
    public Field getPageField(Class pageClass, String fieldName);

    /**
     * Return the headers of the page for the given path.
     *
     * @param path the path of the page
     * @return a Map of headers for the given page path
     */
    public Map getPageHeaders(String path);

    /**
     * Return an array public fields for the given page class.
     *
     * @param pageClass the page class
     * @return an array public fields for the given page class
     */
    public Field[] getPageFieldArray(Class pageClass);


    /**
     * Return the page not found <tt>Page</tt> <tt>Class</tt>.
     *
     * @return the page not found <tt>Page</tt> <tt>Class</tt>
     */
    public Class getNotFoundPageClass();

    /**
     * Return the application servlet context.
     *
     * @return the application servlet context
     */
    public ServletContext getServletContext();

}
