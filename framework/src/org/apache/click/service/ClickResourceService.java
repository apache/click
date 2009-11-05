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
package org.apache.click.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.util.ClickUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Provides a default Click static resource service class. This class will
 * serve static resources contained in the web applications JARs, under the
 * resource path META-INF/resources and which are contained under the WAR file
 * web root.
 * <p/>
 * This service is useful for application servers which do not allow Click to
 * automatically deploy resources to the web root /click/ directory.
 */
public class ClickResourceService implements ResourceService {

    /** The click resources cache. */
    protected Map<String, byte[]> resourceCache = new HashMap<String, byte[]>();

    /** The application log service. */
    protected LogService logService;

    /** The application configuration service. */
    protected ConfigService configService;

    /**
     * @see ResourceService#onInit(ServletContext)
     *
     * @param servletContext the application servlet context
     * @throws IOException if an IO error occurs initializing the service
     */
    public void onInit(ServletContext servletContext) throws IOException {

        configService = ClickUtils.getConfigService(servletContext);
        logService = configService.getLogService();

        // Load all JAR resources
        List<String> cacheables = getCacheableDirs();
        for (String cacheable : cacheables) {
            loadJarResources(cacheable);
        }

        // Load file system resources. File system resources override JAR
        // resources
        for (String cacheable : cacheables) {
            loadDirResources(servletContext, cacheable);
        }
    }

    /**
     * @see ResourceService#onDestroy()
     */
    public void onDestroy() {
        resourceCache.clear();
    }

    /**
     * @see ResourceService#isResourceRequest(HttpServletRequest)
     *
     * @param request the servlet request
     * @return true if the request is for a static click resource
     */
    public boolean isResourceRequest(HttpServletRequest request) {
        String resourcePath = ClickUtils.getResourcePath(request);

        // If not a click page and not JSP and not a directory
        return !configService.isTemplate(resourcePath)
            && !resourcePath.endsWith("/");
    }

    /**
     * @see ResourceService#renderResource(HttpServletRequest, HttpServletResponse)
     *
     * @param request the servlet resource request
     * @param response the servlet response
     * @throws IOException if an IO error occurs rendering the resource
     */
    public void renderResource(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String resourcePath = ClickUtils.getResourcePath(request);

        if (!resourceCache.containsKey(resourcePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType = ClickUtils.getMimeType(resourcePath);
        if (mimeType != null) {
            response.setContentType(mimeType);
        }

        byte[] resourceData = resourceCache.get(resourcePath);
        renderResource(response, resourceData);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the list of directories that contains cacheable resources.
     * <p/>
     * By default only resource packaged under the "<tt>/click</tt>" directory
     * will be processed. To serve resources from other directories you need to
     * override this method and return a list of directories to process.
     * <p/>
     * For example:
     *
     * <pre class="prettyprint">
     * public class MyResourceService extends ClickResourceService {
     *
     *     protected List<String> getCacheableDirs() {
     *         // Get default dirs which includes /click
     *         List list = super.getCacheableDirs();
     *
     *         // Add resources packaged under the folder /clickclick
     *         list.add("/clickclick");
     *         // Add resources packaged under the folder /mycorp
     *         list.add("/mycorp");
     *     }
     * } </pre>
     *
     * You also need to add a mapping in your <tt>web.xml</tt> to forward
     * requests for these resources on to Click:
     *
     * <pre class="prettyprint">
     * &lt;-- The default Click *.htm mapping --&gt;
     * &lt;servlet-mapping&gt;
		 *   &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
		 *   &lt;url-pattern&gt;*.htm&lt;/url-pattern&gt;
	   * &lt;/servlet-mapping&gt;
     *
     * &lt;-- Add a mapping to serve all resources under /click directly from
     * the JARs. --&gt;
	   * &lt;servlet-mapping&gt;
		 *   &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
		 *   &lt;url-pattern&gt;/click/*&lt;/url-pattern&gt;
	   * &lt;/servlet-mapping&gt;
     *
     * &lt;-- Add another mapping to serve all resources under /clickclick
     * from the JARs. --&gt;
	   * &lt;servlet-mapping&gt;
		 *   &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
		 *   &lt;url-pattern&gt;/clickclick/*&lt;/url-pattern&gt;
	   * &lt;/servlet-mapping&gt;
     *
     * &lt;-- Add a mapping to serve all resources under /mycorp
     * from the JARs. --&gt;
	   * &lt;servlet-mapping&gt;
		 *   &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
		 *   &lt;url-pattern&gt;/mycorp/*&lt;/url-pattern&gt;
	   * &lt;/servlet-mapping&gt;
     * </pre>
     *
     * @return list of directories that should be cached
     */
    protected List<String> getCacheableDirs() {
       List list = new ArrayList();
       list.add("/click");
       return list;
    }

    // Private Methods --------------------------------------------------------

    private void loadJarResources(String resourceDir) throws IOException {
        if (resourceDir == null) {
            throw new IllegalArgumentException("resource directory cannot be null");
        }

        long startTime = System.currentTimeMillis();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (!resourceDir.startsWith("/")) {
            resourceDir = '/' + resourceDir;
        }

        // Find all jars and directories on the classpath that contains the
        // directory "META-INF/resources/<resourceDir>", and deploy those resources
        String resourceDirectory = "META-INF/resources" + resourceDir;
        Enumeration<URL> en = classLoader.getResources(resourceDirectory);
        while (en.hasMoreElements()) {
            URL url = en.nextElement();
            loadResourcesOnClasspath(url, resourceDirectory);
        }

        if (logService.isTraceEnabled()) {
            logService.trace("loaded files from jars and folders - "
                + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    /**
     * Deploy from the url all resources found under the prefix.
     *
     * @param url the url of the jar or folder which resources to deploy
     * @param resourceDirectory the directory under which resources are found
     * @throws IOException if resources from the url cannot be deployed
     */
    private void loadResourcesOnClasspath(URL url, String resourceDirectory)
        throws IOException {

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

        // Check if path represents a jar
        if (path.indexOf('!') > 0) {
            jarPath = path.substring(0, path.indexOf('!'));

            File jar = new File(jarPath);

            if (jar.exists()) {
                loadFilesInJar(jar, resourceDirectory);

            } else {
                logService.error("Could not load the jar '" + jarPath
                    + "'. Please ensure this file exists in the specified"
                    + " location.");
            }
        } else {
            File dir = new File(path);
            loadFilesInJarDir(dir, resourceDirectory);
        }
    }

    private void loadFilesInJar(File jar, String resourceDirectory)
        throws IOException {

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

                // Guard against loading folders -> META-INF/resources/click/
                if (jarEntry.isDirectory()) {
                    continue;
                }

                // jarEntryName example -> META-INF/resources/click/table.css
                String jarEntryName = jarEntry.getName();

                // Only deploy resources from "META-INF/resources/"
                int pathIndex = jarEntryName.indexOf(resourceDirectory);
                if (pathIndex == 0) {
                    if (logFeedback && logService.isTraceEnabled()) {
                        logService.trace("loaded files from jar -> "
                                         + jar.getCanonicalPath());

                        // Only provide feedback once per jar
                        logFeedback = false;
                    }
                    loadJarFile(jarEntryName, resourceDirectory);
                }
            }
        } finally {
            ClickUtils.close(jarInputStream);
            ClickUtils.close(inputStream);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFilesInJarDir(File dir, String resourceDirectory)
        throws IOException {

        if (dir == null) {
            throw new IllegalArgumentException("Dir cannot be null");
        }

        if (!dir.exists()) {
            logService.trace("No resources deployed from the folder '" + dir.getAbsolutePath()
                + "' as it does not exist.");
            return;
        }

        Iterator files = FileUtils.iterateFiles(dir,
                                                TrueFileFilter.INSTANCE,
                                                TrueFileFilter.INSTANCE);

        boolean logFeedback = true;
        while (files.hasNext()) {
            // file example -> META-INF/resources/click/table.css
            File file = (File) files.next();

            // Guard against loading folders -> META-INF/resources/click/
            if (file.isDirectory()) {
                continue;
            }

            String fileName = file.getCanonicalPath().replace('\\', '/');

            // Only deploy resources from "META-INF/resources/"
            int pathIndex = fileName.indexOf(resourceDirectory);
            if (pathIndex != -1) {
                if (logFeedback && logService.isTraceEnabled()) {
                    logService.trace("loaded files from folder -> "
                        + dir.getAbsolutePath());

                    // Only provide feedback once per dir
                    logFeedback = false;
                }
                fileName = fileName.substring(pathIndex);
                loadJarFile(fileName, resourceDirectory);
            }
        }
    }

    private void loadJarFile(String file, String prefix) throws IOException {
        // Only deploy resources containing the prefix
        int pathIndex = file.indexOf(prefix);
        if (pathIndex == 0) {
            pathIndex += prefix.length();

            // resourceName example -> click/table.css
            String resourceName = file.substring(pathIndex);

            if (resourceName.length() > 0) {
                byte[] resourceBytes = getClasspathResourceData(file);

                if (resourceBytes != null) {
                    resourceCache.put("/" + resourceName, resourceBytes);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadDirResources(ServletContext servletContext, String resourceDir)
        throws IOException {

        if (resourceDir == null) {
            throw new IllegalArgumentException("resource directory cannot be null");
        }

        Set resources = servletContext.getResourcePaths(resourceDir);

        if (resources != null) {
            // Add all resources withtin web application
            for (Iterator i = resources.iterator(); i.hasNext();) {
                String resource = (String) i.next();

                // If resource is a folder, recursively look for resources in
                // that folder
                if (resource.endsWith("/")) {

                    loadDirResources(servletContext, resource);
                } else {

                    if (!configService.isTemplate(resource)) {

                        byte[] resourceData =
                            getServletResourceData(servletContext, resource);

                        if (resourceData != null) {
                            resourceCache.put(resource, resourceData);
                        }
                    }
                }
            }
        }
    }

    /**
     * Load the resource for the given resourcePath from the servlet context.
     *
     * @param servletContext the application servlet context
     * @param resourcePath the path of the resource to load
     * @return the byte array for the given resource path
     * @throws IOException if the resource could not be loaded
     */
    private byte[] getServletResourceData(ServletContext servletContext,
        String resourcePath) throws IOException {

        InputStream inputStream = null;
        try {
            inputStream = servletContext.getResourceAsStream(resourcePath);

            if (inputStream != null) {
                return IOUtils.toByteArray(inputStream);
            } else {
                return null;
            }

        } finally {
            ClickUtils.close(inputStream);
        }
    }

    /**
     * Load the resource for the given resourcePath from the classpath.
     *
     * @param resourcePath the path of the resource to load
     * @return the byte array for the given resource path
     * @throws IOException if the resource could not be loaded
     */
    private byte[] getClasspathResourceData(String resourcePath) throws IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            inputStream = getClass().getResourceAsStream(resourcePath);
        }

        try {

            if (inputStream != null) {
                return IOUtils.toByteArray(inputStream);
            } else {
                return null;
            }

        } finally {
            ClickUtils.close(inputStream);
        }
    }

    /**
     * Render the given resourceData byte array to the response.
     *
     * @param response the response object
     * @param resourceData the resource byte array
     * @throws IOException if the resource data could not be rendered
     */
    private void renderResource(HttpServletResponse response,
        byte[] resourceData) throws IOException {

        OutputStream outputStream = null;
        try {
            response.setContentLength(resourceData.length);

            outputStream = response.getOutputStream();
            outputStream.write(resourceData);
            outputStream.flush();

        } finally {
            ClickUtils.close(outputStream);
        }
    }
}
