/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Mock implementation of {@link javax.servlet.ServletContext}.
 * <p/>
 * This implementation supports all of the standard context methods except for
 * request dispatching which just indicates what is being dispatched to, rather
 * than performing an actual dispatch.
 * <p/>
 * The context can be configured with a path parameter that should point to an
 * directory location that represents the place where the contents of
 * the WAR bundle are located. The path can either be an absolute or relative
 * path. If the path is not found, the classpath will be checked for such a
 * directory. Setting this value allows all of the resource location
 * functionality to work as in a fully functioning web application. If this
 * value is not set then not resource location functionality will work and
 * instead null will always be returned.
 * <p/>
 * This class was adapted from <a href="http://wicket.apache.org">Apache Wicket</a>.
 */
public class MockServletContext implements ServletContext {

    // -------------------------------------------------------- Constants

    /**
     * The servlet context default context path, <em>"/mock"</em>.
     */
    public static final String DEFAULT_CONTEXT_PATH = "/mock";

    // -------------------------------------------------------- Private variables

    /** Map of attributes. */
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    /** Map of initialization parameters. */
    private final Map<String, String> initParameters = new HashMap<String, String>();

    /** Map of mime types. */
    private final Map<String, String> mimeTypes = new HashMap<String, String>();

    /** The context temporary path. */
    private String tempPath;

    /** The web application path. */
    private String webappPath;

    /**
     * The web application root file. The File is created from the
     * {@link #webappPath} value.
     */
    private File webappRoot;

    /** The servlet context name, <em>"mock"</em>. */
    private String servletContextName = "mock";

    /**
     * The context path, by default its value is set to
     * {@link #DEFAULT_CONTEXT_PATH}.
     */
    private String contextPath = DEFAULT_CONTEXT_PATH;

    /**
     * Default constructor for this mock object.
     * <p/>
     * The servlet context name is set to 'mock'.
     * The web content root and temporary work direcotry are set to null.
     */
    public MockServletContext() {
        this(DEFAULT_CONTEXT_PATH, null, null);
    }

    /**
     * Create the mock object. As part of the creation, the context sets the
     * root directory where web application content is stored. This must be an
     * ABSOLUTE directory relative to where the tests are being executed.
     * <p/>
     * For example: <code>System.getProperty("user.dir") + "/src/webapp"</code>
     * <p/>
     * In addition to setting the web root directory, this constructor also sets
     * up a temporary work directory for things like file uploads.
     * <p/>
     * <b>Note</b> this temporary work directory is set as the value of the
     * ServletContext attribute 'javax.servlet.context.tempdir'.
     * <p/>
     * The temporary work directory defaults to
     * System.getProperty("java.io.tmpdir").
     *
     * @param contextPath the servlet context path
     * @param webappPath The path to the root of the web application
     */
    public MockServletContext(final String contextPath,
        final String webappPath) {
        this(contextPath, webappPath, System.getProperty("java.io.tmpdir"));
    }

    /**
     * Create the mock object. As part of the creation, the context sets the
     * root directory where web application content is stored. This must be an
     * ABSOLUTE directory relative to where the tests are being executed.
     * <p/>
     * For example: <code>System.getProperty("user.dir") + "/src/webapp"</code>
     * <p/>
     * In addition to setting the web root directory, this constructor also sets
     * up a temporary work directory for things like file uploads.
     * <p/>
     * <b>Note</b> this temporary work directory is set as the value of the
     * ServletContext attribute 'javax.servlet.context.tempdir'.
     *
     * @param contextPath the servlet context path
     * @param webappPath the path to the root of the web application
     * @param tempPath the temporary work directory
     */
    public MockServletContext(final String contextPath,
        final String webappPath, final String tempPath) {
        setContextPath(contextPath);

        //Setup temp path, before webapp path, since setWebappPath() will
        //default tempPath to java.io.tmpdir if tempPath does not have a value
        //yet
        this.setTempPath(tempPath);
        this.setWebappPath(webappPath);

        mimeTypes.put("html", "text/html");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("xml", "text/xml");
        mimeTypes.put("js", "text/plain");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("png", "image/png");
    }

    /**
     * Creates the web application root File {@link #getWebappRoot()}.
     *
     * @throws IllegalStateException if the {@link #getWebappPath()} cannot
     * be found
     */
    public void createWebappRoot() {
        webappRoot = null;

        if (StringUtils.isBlank(getWebappPath())) {
            return;
        }

        webappRoot = new File(getWebappPath());
        if (webappRoot.exists() && webappRoot.isDirectory()) {
            //If the webappRoot is a legal directory, we can return
            System.out.println("    WEB root directory defined at -> "
                + webappRoot.getAbsolutePath());
            return;
        }

        //Build up a string of locations that were checked
        String checkedPaths = webappRoot.getAbsolutePath();

        //If webappRoot is not a legal directory, look for the webappRoot on
        //the classpath
        URL url = null;
        try {
            url = getResource(getWebappPath());
            if (url == null) {
                webappRoot = null;
            } else {
                URI uri = new URI(url.toString());
                webappRoot = new File(uri);
                if (webappRoot.exists() && webappRoot.isDirectory()) {
                    //If the webappRoot is a legal directory on the classpath
                    //we can return
                    System.out.println("    WEB root directory defined at -> "
                        + webappRoot.getAbsolutePath());
                    return;
                }
            }
            if (webappRoot == null) {
                checkedPaths += ". Also note that the path '" + getWebappPath()
                    + "' was checked but not found on the classpath";
            } else {
                checkedPaths += ", " + webappRoot.getAbsolutePath();
            }
        } catch (Exception ex) {
            String msg = "error occurred while checking for existence of the web"
                + " application root directory at : " + url;
            throw new RuntimeException(msg, ex);
        }

        //At this stage it seems that the path cannot be mapped to a
        //valid directory, so throw an exception so user can provide the
        //correct path
        throw new IllegalStateException("ERROR: The "
            + "directory cannot be found: " + getWebappPath() + ". "
            + "The following absolute locations were checked for the path: "
            + checkedPaths + ".");
    }

    /**
     * Creates a temporary directory as specified by {@link #getTempPath()}.
     *
     * @throws IllegalStateException if the {@link #getTempPath()} is not valid
     */
    public void createTempDir() {
        attributes.put("javax.servlet.context.tempdir", null);

        if (StringUtils.isNotBlank(getTempPath())) {
            final File tempDirectory = new File(getTempPath());
            if (!tempDirectory.exists()) {
                tempDirectory.mkdirs();
            }
            if (tempDirectory.exists() && tempDirectory.isDirectory()) {
                deleteDirectoryOnShutdown(tempDirectory);
                attributes.put("javax.servlet.context.tempdir", tempDirectory);
                System.out.println("    WEB temp directory defined at -> "
                    + tempDirectory.getAbsolutePath());
            } else {
                throw new IllegalStateException("ERROR: The "
                    + "directory cannot be found: " + getTempPath() + ". "
                    + "The following absolute locations were checked for the "
                    + "path: " + tempDirectory.getAbsolutePath());
            }
        }
    }

    /**
     * Set the servlet context name to the specified value.
     *
     * @param servletContextName the servlet context name
     */
    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    /**
     * Return the temporary path where files are stored during test runs.
     *
     * @return the temporary path where files are stored during test runs
     */
    public String getTempPath() {
        return tempPath;
    }

    /**
     * Set the temporary path where files are stored during test runs.
     *
     * @param tempPath the temporary path where files are stored during test
     * runs
     */
    public void setTempPath(String tempPath) {
        if (StringUtils.isBlank(tempPath)) {
            return;
        }

        //If the specified temp path is the same as the temp dir, add a mock
        //folder to stop possible locking on Windows OS.
        if (System.getProperty("java.io.tmpdir").equals(tempPath)) {
            if (!tempPath.endsWith("/")) {
                tempPath += "/";
            }
            tempPath = tempPath + "click-temp";
        }
        this.tempPath = tempPath;

        //Create the temporary context directory
        createTempDir();
    }

    /**
     * Return the web application path where resources like javascript, css
     * and images can be picked up.
     *
     * @return the web application path
     */
    public String getWebappPath() {
        return webappPath;
    }

    /**
     * Return the web application root File where resources like javascript, css
     * and images can be picked up.
     *
     * @return the web application root File
     */
    public File getWebappRoot() {
        return webappRoot;
    }

    /**
     * Sets the mock web application path to the specified webappPath.
     * <p/>
     * <b>Note:</b> this method will also set the web application's temporary
     * directory to the value {@link #getTempPath()}. If {@link #getTempPath()}
     * is not set, this method will default tempPath to:
     * <tt>System.getProperty("java.io.tmpdir")</tt>.
     *
     * @param webappPath set the context web application path
     */
    public void setWebappPath(String webappPath) {
        this.webappPath = webappPath;

        if (StringUtils.isBlank(webappPath)) {
            return;
        }

        //Create the context root
        createWebappRoot();

        if (getTempPath() == null) {
            setTempPath(System.getProperty("java.io.tmpdir"));
        }
    }

    /**
     * Add an init parameter.
     *
     * @param name The parameter name
     * @param value The parameter value
     */
    public void addInitParameter(final String name, final String value) {
        initParameters.put(name, value);
    }

    /**
     * Add the map of init parameters.
     *
     * @param initParameters A map of init parameters
     */
    public void addInitParameters(final Map<String, String> initParameters) {
        if (initParameters == null) {
            return;
        }
        initParameters.putAll(initParameters);
    }

    // Configuration methods
    /**
     * Add a new recognized mime type.
     *
     * @param fileExtension The file extension (e.g. "jpg")
     * @param mimeType The mime type (e.g. "image/jpeg")
     */
    public void addMimeType(final String fileExtension, final String mimeType) {
        mimeTypes.put(fileExtension, mimeType);
    }

    /**
     * Get an attribute with the given name.
     *
     * @param name The attribute name
     * @return The value, or null
     */
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    /**
     * Get all of the attribute names.
     *
     * @return The attribute names
     */
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    // -------------------------------------------------------- ServletContext interface methods

    /**
     * Get the context for the given URL path.
     *
     * @param name The url path
     * @return Always returns this
     */
    public ServletContext getContext(String name) {
        return this;
    }

    /**
     * Return the servlet context path.
     *
     * @return the servletContext path
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * Sets the servlet context path.
     *
     * @param contextPath the servlet context path
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * Get the init parameter with the given name.
     *
     * @param name The name
     * @return The parameter, or null if no such parameter
     */
    public String getInitParameter(final String name) {
        return initParameters.get(name);
    }

    /**
     * Get the name of all of the init parameters.
     *
     * @return The init parameter names
     */
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }

    /**
     * Return the major version of the Servlet spec that this package supports,
     * defaults to 2.
     *
     * @return the major version of the Servlet spec that this package supports,
     * defaults to 2.
     */
    public int getMajorVersion() {
        return 2;
    }

    /**
     * Get the mime type for the given file. Uses a hardcoded map of mime
     * types set at initialization time. If the mime type was not explicitly
     * set, this method will fallback to
     * {@link org.apache.click.util.ClickUtils#getMimeType(String)}.
     *
     * @param name The name to get the mime type for
     *
     * @return The mime type
     */
    public String getMimeType(final String name) {
        int index = name.lastIndexOf('.');
        if (index == -1 || index == (name.length() - 1)) {
            return null;
        }
        String type = name.substring(index + 1);
        if (mimeTypes.containsKey(type)) {
            return mimeTypes.get(type);
        } else {
            return ClickUtils.getMimeType(type);
        }
    }

    /**
     * Return the minor version of the Servlet spec that this package supports,
     * defaults to 3.
     *
     * @return the minor version of the Servlet spec that this package supports,
     * defaults to 3.
     */
    public int getMinorVersion() {
        return 3;
    }

    /**
     * Get the real file path of the given resource name.
     *
     * @param name The name
     * @return The real path or null
     */
    public String getRealPath(String name) {
        if (webappRoot == null) {
            return null;
        }

        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        File f = new File(webappRoot, name);
        if (!f.exists()) {
            return null;
        } else {
            return f.getPath();
        }
    }

    /**
     * Returns a RequestDispatcher for the specified path. The dispatcher
     * will not dispatch to the resource. It only records the specified path
     * so that one can test if the correct path was dispatched to.
     *
     * @param path a String specifying the pathname to the resource
     * @return a dispatcher for the specified path
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return new MockRequestDispatcher(path);
    }

    /**
     * Returns a RequestDispatcher for the specified name. The dispatcher
     * will not dispatch to the resource. It only records the specified name
     * so that one can test if the correct name was dispatched to.
     *
     * @param name a String specifying the name of a servlet to wrap
     * @return a dispatcher for the specified name
     */
    public RequestDispatcher getNamedDispatcher(final String name) {
        return getRequestDispatcher(name);
    }

    /**
     * Get the URL for a particular resource that is relative to the web app
     * root directory.
     *
     * @param name The name of the resource to get
     * @return The resource, or null if resource not found
     * @throws MalformedURLException If the URL is invalid
     */
    public URL getResource(String name) throws MalformedURLException {
        if (webappRoot == null) {
            name = removeLeadingSlash(name);
            return Thread.currentThread().getContextClassLoader().getResource(name);
        }

        File f = new File(webappRoot, name);
        if (!f.exists()) {
            name = removeLeadingSlash(name);
            return Thread.currentThread().getContextClassLoader().getResource(name);
        } else {
            return f.toURI().toURL();
        }
    }

    /**
     * Get an input stream for a particular resource that is relative to the
     * web app root directory or the current classpath. If the webappRoot is
     * not set, this method will try and load the resource from the classpath.
     *
     * @param name The name of the resource to get
     * @return The input stream for the resource, or null if resource is not
     * found
     */
    public InputStream getResourceAsStream(String name) {
        if (webappRoot == null) {
            name = removeLeadingSlash(name);
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        }

        File f = new File(webappRoot, name);
        if (!f.exists()) {
            name = removeLeadingSlash(name);
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        } else {
            try {
                return new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Get the resource paths starting from the web app root directory and then
     * relative to the given name.
     *
     * @param name The starting name
     * @return The set of resource paths at this location
     * @throws IllegalArgumentException if the specified name does not start
     * with a "/" character
     */
    public Set<String> getResourcePaths(String name) {
        if (!name.startsWith("/")) {
            throw new IllegalArgumentException("Path " + name
                + " does not start with a \"/\" character");
        }
        if (webappRoot == null) {
            return new HashSet<String>();
        }

        name = name.substring(1);
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        String[] elements = null;
        if (name.trim().length() == 0) {
            elements = new String[0];
        } else {
            elements = name.split("/");
        }

        //Find the most specific matching path
        File current = webappRoot;
        for (int i = 0; i < elements.length; i++) {
            File[] files = current.listFiles();
            boolean match = false;
            for (int f = 0; f < files.length; f++) {
                if (files[f].getName().equals(elements[i])
                    && files[f].isDirectory()) {
                    current = files[f];
                    match = true;
                    break;
                }
            }
            if (!match) {
                return null;
            }
        }

        //List of resources in the matching path
        File[] files = current.listFiles();
        Set<String> result = new HashSet<String>();
        int stripLength = webappRoot.getPath().length();
        for (int f = 0; f < files.length; f++) {
            String s = files[f].getPath().substring(stripLength).replace('\\', '/');
            if (files[f].isDirectory()) {
                s = s + "/";
            }
            result.add(s);
        }
        return result;
    }

    /**
     * Get the server info.
     *
     * @return The server info
     */
    public String getServerInfo() {
        return "Click Mock Environment";
    }

    /**
     * NOT USED - Servlet Spec requires that this always returns null.
     *
     * @param name Not used
     *
     * @return null
     *
     * @throws ServletException Not used
     */
    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    /**
     * Return the name of the servlet context.
     *
     * @return The name
     */
    public String getServletContextName() {
        return servletContextName;
    }

    /**
     * NOT USED - Servlet spec requires that this always returns null.
     *
     * @return null
     */
    public Enumeration<String> getServletNames() {
        return null;
    }

    /**
     * NOT USED - Servlet spec requires that this always returns null.
     *
     * @return null
     */
    public Enumeration<?> getServlets() {
        return null;
    }

    /**
     * Log the exception to System.err and the message to System.out.
     *
     * @param e The exception to log
     * @param msg The message to log
     */
    public void log(Exception e, String msg) {
        log(msg, e);
    }

    /**
     * Log the message to System.out.
     *
     * @param msg The message to log
     */
    public void log(String msg) {
        System.out.println(msg);
    }

    /**
     * Log the cause to System.err and the message to System.out.
     *
     * @param msg The message to log
     * @param cause The cause exception
     */
    public void log(String msg, Throwable cause) {
        log(msg);
        cause.printStackTrace();
    }

    /**
     * Remove an attribute with the given name.
     *
     * @param name The name
     */
    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    /**
     * Set an attribute.
     *
     * @param name The name of the attribute
     * @param o The value
     */
    public void setAttribute(final String name, final Object o) {
        attributes.put(name, o);
    }

    /**
     * Delete the specified directory and any subdirectories.
     *
     * @param directory to delete
     * @return true if the directory was successfully deleted, false otherwise
     */
    static synchronized boolean deleteDirectory(final File directory) {
        if (directory == null) {
            return true;
        }

        if (!directory.exists() || !directory.isDirectory()) {
            return true;
        }

        Throwable shutdownException = null;
        try {
            System.out.println("Deleting temporary directory '" + directory.getAbsolutePath() + "'");
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            shutdownException = e;
        } finally {

            if (directory.exists()) {
                System.err.println("=======================================================================");
                HtmlStringBuffer buffer = new HtmlStringBuffer();
                buffer.append("    WARNING: could not delete all files ");
                buffer.append("in the temporary directory: '");
                buffer.append(directory.getAbsolutePath());
                buffer.append("'.\n");
                buffer.append("    This can occur (especially on a Windows OS) when some of the files in the ");
                buffer.append("directory are locked by another process. ");
                buffer.append("You can delete this directory manually.");
                System.err.println(buffer.toString());
                System.err.println("=======================================================================\n");
                if (shutdownException != null) {
                    shutdownException.printStackTrace(System.err);
                }

                return false;
            }
        }
        return true;
    }

    /**
     * Deletes the specified directory when the JVM is shutdown.
     *
     * @param directory the directory to delete
     */
    private void deleteDirectoryOnShutdown(final File directory) {
        if (directory == null) {
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                //delete the temporary directory and all subdirectories
                deleteDirectory(directory);
            }
        });
    }

    /**
     * Remove the leading slash '/' from the specified name.
     *
     * @param name the name from which to remove the leading slash '/'
     * @return the name with the leading slash removed
     */
    private String removeLeadingSlash(String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        return name;
    }
}
