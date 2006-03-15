/*
 * Copyright 2003-2004 The Apache Software Foundation.
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

package net.sf.click.sandbox.chrisichris.resource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.ServletContext;

import net.sf.click.util.ClickUtils;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * Resource loader that works like a
 * {@link org.apache.velocity.tools.view.servlet.WebappLoader} but can also read
 * parts of a template if the template name is suffixed with '?'+partName. This
 * is useful ie for AJAX requests where parts of the template should be in the
 * template but can be rendered separately.
 * <p>
 * This class is a functional full replacement of Click's default webapp resource loader. 
 * To use this class add to the /WEB-INF/velocity.properties the following line:
 * </p>
 * <pre>
 * webapp.resource.loader.class=net.sf.click.sandbox.chrisichris.resource.WebappPartialLoader
 * </pre>
 * <p>
 * This way the ResourceLoader gets configured automatically by click along the 
 * mode specified in click.xml. 
 * <p>
 * The start of a template part is marked by ##PT{partName} and the end is
 * marked by ##PT{/partName}. Where partName is the name of the template-part. 
 * (PT stands for partial template).
 * If the template path is ended with ? + partname ie
 * (/template/foo.htm?fooPart) than this resource loader will return the text
 * between ##PT{fooPart} and ##PT{/fooPart} as the template for the path. 
 * </p>
 * <p>
 * The
 * part-names must be unique within a template. Part-names can be nested and mixed
 * as wanted. All this ResourceLoader does is take the String between the start
 * and the end tag for the name (wheter there are start and/or endtags in between or not). 
 * Especially this ResourceLoader does not care about proper nesting of start and
 * end tags. I think I have mentioned already: PT names have to be unique within one template.
 * </p>
 * <p>
 * For example if the following template is at the path '/template/foo.htm':
 * </p>
 * 
 * <pre>
 * 
 *  [file: /template/foo.htm]
 *  
 *  &lt;html&gt;
 *  &lt;head&gt;&lt;/head&gt;
 *  &lt;body&gt;
 *     Some template code
 *     ##PT{updateDyn}
 *       show some result
 *       #if(false)
 *         ##PT{update}
 *           updateString
 *         ##PT{/update}
 *       #end
 *     ##PT{/updateDyn}
 *  &lt;/body&gt;
 *  &lt;/html&gt;   
 *  
 * </pre>
 * 
 * <p>
 * The path '/template/foo.htm' will return the full template as above.
 * </p>
 * <p>
 * The path '/template/foo.htm?updateDyn' will return the following template.
 * </p>
 * 
 * <pre>
 *       show some result
 *       #if(false)
 *         ##PT{update}
 *           updateString
 *         ##PT{/update}
 *       #end
 * </pre>
 * 
 * <p>
 * The path '/template/foo.htm?update' will return the following template.
 * </p>
 * 
 * <pre>
 * updateString
 * </pre>
 * 
 * <p>
 * The code is a modified version of
 * org.apache.velocity.tools.view.servlet.WebappLoader, which is under the
 * Apache Lincense as this code is.
 * </p>
 * 
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @author <a href="mailto:nathan@esha.com">Nathan Bubna</a>
 * @author <a href="mailto:claude@savoirweb.com">Claude Brisson</a>
 * @author Christian Essl
 * @version $Id$
 */

public class WebappPartialLoader extends ResourceLoader {

    public final static char NAME_SEP = '?';

    /** The root paths for templates (relative to webapp's root). */
    protected String[] paths = null;

    protected HashMap templatePaths = null;

    protected ServletContext servletContext = null;

    protected String encoding;

    /**
     * This is abstract in the base class, so we need it. <br>
     * NOTE: this expects that the ServletContext has already been placed in the
     * runtime's application attributes under its full class name (i.e.
     * "javax.servlet.ServletContext").
     * 
     * @param configuration
     *            the {@link ExtendedProperties} associated with this resource
     *            loader.
     */
    public void init(ExtendedProperties configuration) {
        log.info("WebappPartialLoader : initialization starting.");

        /* get configured paths */
        paths = configuration.getStringArray("path");
        if (paths == null || paths.length == 0) {
            paths = new String[1];
            paths[0] = "/";
        } else {
            /* make sure the paths end with a '/' */
            for (int i = 0; i < paths.length; i++) {
                if (!paths[i].endsWith("/")) {
                    paths[i] += '/';
                }
                log.info("WebappPartialLoader : added template path - '"
                        + paths[i] + "'");
            }
        }

        /* get the ServletContext */
        Object obj = rsvc.getApplicationAttribute(ServletContext.class
                .getName());
        if (obj instanceof ServletContext) {
            servletContext = (ServletContext) obj;
        } else {
            log
                    .error("WebappPartialLoader : unable to retrieve ServletContext");
        }

        /* init the template paths map */
        templatePaths = new HashMap();

        // the encoding
        encoding = rsvc.getString(RuntimeConstants.INPUT_ENCODING,
                RuntimeConstants.ENCODING_DEFAULT);

        log.info("WebappPartialLoader : initialization complete.");
    }

    /**
     * Get an InputStream so that the Runtime can build a template with it.
     * 
     * @param name
     *            name of template to get
     * @return InputStream containing the template
     * @throws ResourceNotFoundException
     *             if template not found in classpath.
     */
    public synchronized InputStream getResourceStream(String nameIn)
            throws ResourceNotFoundException {
        InputStream result = null;

        if (nameIn == null || nameIn.length() == 0) {
            throw new ResourceNotFoundException(
                    "WebappPartialLoader : No template name provided");
        }

        /*
         * since the paths always ends in '/', make sure the name never starts
         * with one
         */
        while (nameIn.startsWith("/")) {
            nameIn = nameIn.substring(1);
        }

        // make the normal fileName
        int cut = nameIn.lastIndexOf(NAME_SEP);
        final String name;
        final String partName;
        if (cut == -1) {
            name = nameIn;
            partName = null;
        } else {
            name = nameIn.substring(0, cut);
            partName = nameIn.substring(cut + 1);
        }

        // load the InputStream for the name
        Exception exception = null;
        for (int i = 0; i < paths.length; i++) {
            try {
                result = servletContext.getResourceAsStream(paths[i] + name);

                /* save the path and exit the loop if we found the template */
                if (result != null) {
                    templatePaths.put(name, paths[i]);
                    break;
                }
            } catch (Exception e) {
                /* only save the first one for later throwing */
                if (exception == null) {
                    exception = e;
                }
            }
        }

        /* if we never found the template */
        if (result == null) {
            String msg;
            if (exception == null) {
                msg = "WebappLoader : Resource '" + name + "' not found.";
            } else {
                msg = exception.getMessage();
            }
            /* convert to a general Velocity ResourceNotFoundException */
            throw new ResourceNotFoundException(msg);
        }

        // if we have no part name just return the stream
        if (partName == null) {
            return result;
        } else {
            try {
                InputStream ret = Tokenizer.find(result, partName,
                        this.encoding);
                if (ret == null) {
                    throw new ResourceNotFoundException(
                            "WebappPartialLoader: Resouce " + nameIn
                                    + " not found.");
                }
                return ret;
            } catch (IOException e) {
                ResourceNotFoundException ex = new ResourceNotFoundException(
                        "WebappPartialLoader: Resource " + name + " exception "
                                + e.getMessage());
                ex.initCause(e);
                throw ex;
            }
        }
    }

    /**
     * Checks to see if a resource has been deleted, moved or modified.
     * 
     * @param resource
     *            Resource The resource to check for modification
     * @return boolean True if the resource has been modified
     */
    public boolean isSourceModified(Resource resource) {
        String rootPath = servletContext.getRealPath("/");
        if (rootPath == null) {
            /*
             * rootPath is null if the servlet container cannot translate the
             * virtual path to a real path for any reason (such as when the
             * content is being made available from a .war archive)
             */
            return false;
        }

        /* first, try getting the previously found file */
        String fileName = Tokenizer.getFileName(resource.getName());
        String savedPath = (String) templatePaths.get(fileName);
        File cachedFile = new File(rootPath + savedPath, fileName);
        if (!cachedFile.exists()) {
            /* then the source has been moved and/or deleted */
            return true;
        }

        /*
         * check to see if the file can now be found elsewhere before it is
         * found in the previously saved path
         */
        File currentFile = null;
        for (int i = 0; i < paths.length; i++) {
            currentFile = new File(rootPath + paths[i], fileName);
            if (currentFile.canRead()) {
                /*
                 * stop at the first resource found (just like in
                 * getResourceStream())
                 */
                break;
            }
        }

        /* if the current is the cached and it is readable */
        if (cachedFile.equals(currentFile) && cachedFile.canRead()) {
            /* then (and only then) do we compare the last modified values */
            return (cachedFile.lastModified() != resource.getLastModified());
        } else {
            /*
             * we found a new file for the resource or the resource is no longer
             * readable.
             */
            return true;
        }
    }

    /**
     * Checks to see when a resource was last modified
     * 
     * @param resource
     *            Resource the resource to check
     * @return long The time when the resource was last modified or 0 if the
     *         file can't be read
     */
    public long getLastModified(Resource resource) {
        String rootPath = servletContext.getRealPath("/");
        if (rootPath == null) {
            /*
             * rootPath is null if the servlet container cannot translate the
             * virtual path to a real path for any reason (such as when the
             * content is being made available from a .war archive)
             */
            return 0;
        }

        String fileName = Tokenizer.getFileName(resource.getName());
        String path = (String) templatePaths.get(fileName);
        File file = new File(rootPath + path, fileName);
        if (file.canRead()) {
            return file.lastModified();
        } else {
            return 0;
        }
    }

    public static class Tokenizer {

        public static final String TAG_START = "##PT{";

        public static final String TAG_END = "##PT{/";

        public static final char TAG_END_CHAR = '}';

        final Reader in;

        public Tokenizer(Reader in) {
            this.in = in;
        }

        public static InputStream find(InputStream baseIs, String partName,
                String encoding) throws IOException {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        baseIs, encoding));
                Tokenizer tk = new Tokenizer(in);
                String value = tk.find(partName);
                if (value == null) {
                    value = " Could not find Template part [" + partName + "]";
                }
                byte[] bytes = value.getBytes(encoding);
                InputStream ret = new ByteArrayInputStream(bytes);
                return ret;
            } finally {
                ClickUtils.close(baseIs);
            }

        }

        public String find(String name) throws IOException {
            // TODO: This can obviously be made faster
            // until this is settled
            // I think it is ok because in production this
            // is cached and it is easier to debug/modifie

            String startTag = TAG_START + name + TAG_END_CHAR;
            String endTag = TAG_END + name + TAG_END_CHAR;

            StringBuffer stB = new StringBuffer();
            while (true) {
                int ic = in.read();
                if (ic == -1) {
                    break;
                }
                char c = (char) ic;
                stB.append(c);
            }

            String fullTemplate = stB.toString();
            int startIndex = fullTemplate.indexOf(startTag);
            if (startIndex == -1) {
                return "No part-template start for name {" + name + "}";
            }
            startIndex += startTag.length();

            int endIndex = fullTemplate.indexOf(endTag, startIndex);
            if (endIndex == -1) {
                return "No part-template end for name {" + name + "}";
            }

            String template = fullTemplate.substring(startIndex, endIndex);
            return template;

        }

        public static boolean isValidName(String name) {
            if (name == null || name.length() == 0) {
                return false;
            }

            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != '_' && c != '-') {
                    return false;
                }
            }
            return true;
        }

        public static String getFileName(String name) {
            int cut = name.lastIndexOf(NAME_SEP);
            if (cut == -1) {
                return name;
            }
            name = name.substring(0, cut);
            return name;
        }

    }
}
