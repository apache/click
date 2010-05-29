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
package org.apache.click;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.click.util.ClickUtils;

/**
 * Partial encapsulates a fragment of an HTTP response. A Partial can be used
 * to stream back a String or byte array to the browser.
 *
 * <h3>Usage</h3>
 * A Partial is often used to stream back html, json, xml, plain text and
 * byte array e.g. jpg, gif, png, pdf and excel documents.
 *
 * <h3>Ajax</h3>
 * TODO
 *
 * <h3>Page Action</h3>
 * TODO
 *
 * <h3>Example</h3>
 * TODO
 */
public class Partial {

    // -------------------------------------------------------------- Constants

    /** The plain text content type constant <tt>text/plain</tt>. */
    public static final String TEXT = "text/plain";

    /** The html content type constant <tt>text/html</tt>. */
    public static final String HTML = "text/html";

    /** The The xhtml content type constant <tt>application/xhtml+xml</tt>. */
    public static final String XHTML = "application/xhtml+xml";

    /** The json content type constant <tt>text/json</tt>. */
    public static final String JSON = "text/json";

    /** The javascript content type constant <tt>text/javascript</tt>. */
    public static final String JAVASCRIPT = "text/javascript";

    /** The xml content type constant <tt>text/xml</tt>. */
    public static final String XML = "text/xml";

    /** The Partial writer buffer size. */
    private static final int WRITER_BUFFER_SIZE = 256;

    // -------------------------------------------------------- Variables

    /** The content to render. */
    private Object content;

    /** The servlet response reader. */
    private Reader reader;

    /** The servlet response input stream. */
    private InputStream inputStream;

    /** The response content type. */
    private String contentType;

    /** The resposne character encoding. */
    private String characterEncoding;

    /** The response headers. */
    private Map headers;

    /** Indicates whether the Partial should be cached by browser. */
    private boolean cachePartial = false;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the Partial for the given reader and content type.
     *
     * @param reader the reader which characters must be streamed back to the
     * client
     * @param contentType the response content type
     */
    public Partial(Reader reader, String contentType) {
        this.reader = reader;
        this.contentType = contentType;
    }

    /**
     * Construct the Partial for the given inputStream and content type.
     *
     * @param inputStream the input stream to stream back to the client
     * @param contentType the response content type
     */
    public Partial(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    /**
     * Construct the Partial for the given content and content type.
     * <p/>
     * At rendering time the partial invokes the Object's <tt>toString()</tt>
     * method and streams the resulting <tt>String</tt> back to the client.
     *
     * @param content the content to stream back to the client
     * @param contentType the response content type
     */
    public Partial(Object content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    /**
     * Construct the Partial for the given content. The
     * <tt>{@link javax.servlet.http.HttpServletResponse#setContentType(java.lang.String) response content type}</tt>
     * will default to {@link #TEXT}.
     * <p/>
     * At rendering time the partial invokes the Object's <tt>toString()</tt>
     * method and streams the resulting <tt>String</tt> back to the client.
     *
     * @param content the content to stream back to the client
     */
    public Partial(Object content) {
        this.content = content;
        this.contentType = TEXT;
    }

    /**
     * Construct a new empty Partial. The
     * <tt>{@link javax.servlet.http.HttpServletResponse#setContentType(java.lang.String) response content type}</tt>
     * will default to {@link #TEXT}.
     *
     */
    public Partial() {
        this.contentType = TEXT;
    }

    // ---------------------------------------------------------- Public Methds

    /**
     * Indicates whether the partial should be cached by the clients browser
     * or not, defaults to false.
     * <p/>
     * If false, Click will set the following headers to prevent browsers
     * from caching the result:
     * <pre class="prettyprint">
     * response.setHeader("Pragma", "no-cache");
     * response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
     * response.setDateHeader("Expires", new Date(1L).getTime());
     * </pre>
     *
     * @param cachePartial indicates whether the partial should be cached
     * by clients browser or not
     */
    public void setCachePartial(boolean cachePartial) {
        this.cachePartial = cachePartial;
    }

    /**
     * Return the partial character encoding.
     *
     * @return the partial character encoding.
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * Set the partial character encoding.
     *
     * @param characterEncoding the partial character encoding
     */
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    /**
     * Set the partial response content type.
     *
     * @param contentType the partial response content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Return the partial content type.
     *
     * @return the response content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Return the map of response header values.
     *
     * @return the map of response header values
     */
    public Map getHeaders() {
        if (headers == null) {
            return new HashMap();
        }
        return headers;
    }

    /**
     * Set the content to stream back to the client.
     *
     * @param content the content to stream back to the client
     */
    public void setContent(Object content) {
        this.content = content;
    }

    /**
     * Return the content to stream back to the client.
     *
     * @return the content to stream back to the client
     */
    public Object getContent() {
        return content;
    }

    /**
     * Set the content to stream back to the client.
     *
     * @param inputStream the inputStream to stream back to the client
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Return the inputStream to stream back to the client.
     *
     * @return the inputStream to stream back to the client
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Set the reader which characters are streamed back to the client.
     *
     * @param reader the reader which characters are streamed back to the client.
     */
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    /**
     * Return the reader which characters are streamed back to the client.
     *
     * @return the reader which characters are streamed back to the client.
     */
    public Reader getReader() {
        return reader;
    }

    /**
     * Process the partial with the given context.
     *
     * @param context the request context to use
     */
    public final void render(Context context) {
        prepare(context);
        render(context.getRequest(), context.getResponse());
    }

    /**
     * Render the partial to the specified response.
     *
     * @param request the page servlet request
     * @param response the page servlet response
     */
    protected void render(HttpServletRequest request, HttpServletResponse response) {

        try {
            if (content != null) {
                this.reader = new StringReader(content.toString());
            }

            if (reader != null) {
                PrintWriter writer = response.getWriter();
                char[] buffer = new char[WRITER_BUFFER_SIZE];
                int len = 0;
                while (-1 != (len = reader.read(buffer))) {
                    writer.write(buffer, 0, len);
                }

            } else if (inputStream != null) {
                byte[] buffer = new byte[WRITER_BUFFER_SIZE];
                int len = 0;
                OutputStream outputStream = response.getOutputStream();
                while (-1 != (len = inputStream.read(buffer))) {
                    outputStream.write(buffer, 0, len);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            ClickUtils.close(inputStream);
            ClickUtils.close(reader);
        }
    }

    // -------------------------------------------------------- Private Methods

    private void applyHeaders(HttpServletResponse response) {

        if (!cachePartial) {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
            response.setDateHeader("Expires", new Date(1L).getTime());
        }

        if (headers != null) {
            setResponseHeaders(response, getHeaders());
        }
    }

    private void setResponseHeaders(HttpServletResponse response, Map headers) {

        for (Iterator i = headers.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String name = entry.getKey().toString();
            Object value = entry.getValue();

            if (value instanceof String) {
                String strValue = (String) value;
                if (!strValue.equalsIgnoreCase("Content-Encoding")) {
                    response.setHeader(name, strValue);
                }

            } else if (value instanceof Date) {
                long time = ((Date) value).getTime();
                response.setDateHeader(name, time);

            } else {
                int intValue = ((Integer) value).intValue();
                response.setIntHeader(name, intValue);
            }
        }
    }

    private void prepare(Context context) {
        HttpServletResponse response = context.getResponse();
        applyHeaders(response);

        if (getCharacterEncoding() == null) {

            // Fallback to request character encoding
            if (context.getRequest().getCharacterEncoding() != null) {
                response.setContentType(getContentType() + "; charset="
                    + context.getRequest().getCharacterEncoding());
            } else {
                response.setContentType(getContentType());
            }

        } else {
            response.setContentType(getContentType() + "; charset=" + getCharacterEncoding());
        }
    }
}
