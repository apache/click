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
package org.apache.click.examples.page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.click.Page;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a Java source code, HTML and XML examples rendering page.
 */
public class SourceViewer extends BorderPage {

    private static final long serialVersionUID = 1L;

    private static final String[] JAVA_KEYWORDS = { "package", "import",
            "class", "public", "protected", "private", "extends", "implements",
            "return", "if", "while", "for", "do", "else", "try", "new", "void",
            "catch", "throws", "throw", "static", "final", "break", "continue",
            "super", "finally", "true", "false", "true;", "false;", "null",
            "boolean", "int", "char", "long", "float", "double", "short",
            "super", "this," };

    private static final String[] JAVASCRIPT_KEYWORDS = { "break", "continue",
            "do", "else", "for", "if", "return", "while", "auto", "case",
            "char", "const", "default", "double", "enum", "extern", "float",
            "goto", "int", "long", "register", "short", "signed", "sizeof",
            "static", "struct", "switch", "typedef", "union", "unsigned",
            "void", "volatile", "catch", "class", "delete", "false", "import",
            "new", "operator", "private", "protected", "public", "this",
            "throw", "true", "try", "debugger", "eval", "export", "function",
            "get", "null", "set", "undefined", "var", "with", "Infinity",
            "NaN" };

    private static final String[] HTML_KEYWORDS = { "html", "head", "style",
            "script", "title", "link", "body", "h1", "h2", "h3", "h4", "h5",
            "h6", "p", "hr", "br", "span", "table", "tr", "th", "td", "a", "b",
            "i", "u", "ul", "ol", "li", "form", "div", "input", "fieldset",
            "pre", "tt", "ajax-response", "response", "%@", "%@taglib",
            "jsp:include", "c:forEach", "c:choose", "c:when", "c:otherwise",
            "fmt:formatNumber", "fmt:formatDate", "center" };

    private static final String[] XML_KEYWORDS = { "click-app", "pages",
            "page", "excludes", "headers", "header", "format", "mode", "type",
            "filter-name", "filter-class", "filter-mapping", "filter",
            "web-app", "display-name", "description", "servlet-mapping",
            "servlet-name", "servlet-class", "init-param", "param-name",
            "param-value", "servlet", "load-on-startup", "security-constraint",
            "web-resource-collection", "auth-constraint", "role-name",
            "login-config", "auth-method", "realm-name", "security-role",
            "url-pattern", "welcome-file-list", "welcome-file", "Context",
            "ResourceLink", "menu", "?xml", "controls", "control",
            "listener-class", "listener", "beans",  "bean", "context-param",
            "context:component-scan", "property", "constructor-arg",
            "list", "value", "ref", "beans:beans", "beans:bean", "http",
            "intercept-url", "form-login", "logout", "beans:property",
            "beans:list", "beans:ref", "authentication-provider",
            "page-interceptor", "property-service" };

    private static final String[] VELOCITY_KEYWORDS = { "#if", "#if(",
            "#elseif", "#elseif(", "#else", "#else(", "#end", "#set", "#set(",
            "#include", "#include(", "#parse", "#parse(", "#stop", "#macro",
            "#macro(", "#foreach", "#foreach(", "##", "#*", "*#", "#" };

    private boolean isJava = false;

    private boolean isJavaScript = false;

    private boolean javaMultilineComment = false;

    private boolean isXml = false;

    private boolean isHtml = false;

    /**
     * @see Page#onGet()
     */
    @Override
    public void onGet() {
        HttpServletRequest request = getContext().getRequest();

        String filename = request.getParameter("filename");

        if (filename != null) {
            loadFilename(filename);

            getModel().put("title", "Source Viewer : " + filename);

        } else {
            addModel("error", "filename not defined");
        }
    }

    private void loadFilename(String filename) {
        ServletContext context = getContext().getServletContext();

        // Orion server requires '/' prefix to find resources
        String resourceFilename =
            (filename.charAt(0) != '/') ? "/" + filename : filename;

        InputStream in = null;
        try {
            in = context.getResourceAsStream(resourceFilename);

            if (in == null && filename.endsWith(".htm")) {
                resourceFilename =
                    resourceFilename.substring(0, resourceFilename.length() - 4)
                    + ".jsp";

                in = context.getResourceAsStream(resourceFilename);
            }

            if (in != null) {

                loadResource(in, filename);

            } else {
                addModel("error", "File " + resourceFilename + " not found");
            }

        } catch (IOException e) {
            addModel("error", "Could not read " + resourceFilename);

        } finally {
            ClickUtils.close(in);
        }
    }

    private void loadResource(InputStream inputStream, String name)
            throws IOException {

        isJava = name.endsWith(".java");
        isJavaScript = name.endsWith(".js");
        isXml = name.endsWith(".xml");
        isHtml = name.endsWith(".htm");
        if (!isHtml) {
            isHtml = name.endsWith(".html");
        }
        if (!isHtml) {
            isHtml = name.endsWith(".vm");
        }
        if (!isHtml) {
            isHtml = name.endsWith(".jsp");
        }

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(inputStream));

        HtmlStringBuffer buffer = new HtmlStringBuffer();

        // Filter out the license from displaying
        skipLicense(reader, buffer);

        String line = reader.readLine();

        while (line != null) {
            buffer.append(getEncodedLine(line));
            buffer.append("\n");
            line = reader.readLine();
        }

        addModel("source", buffer.toString());

        addModel("name", name);
    }

    private void skipLicense(BufferedReader reader, HtmlStringBuffer buffer)
        throws IOException {

        // Mark reader so we can undo the read in case no license is found
        reader.mark(500);
        String line = reader.readLine();
        if (line == null) {
            return;
        }

        line = line.trim();

        // Check for license start tokens
        if (line.startsWith("/*") || line.startsWith("<!--")) {

            line = reader.readLine();
            while (line != null) {
                line = line.trim();

                // Check for license end tokens
                if (line.startsWith("*/") || line.startsWith("-->")) {
                    // Move the reader past the end of license token
                    line = reader.readLine();
                    break;
                }

                line = reader.readLine();
            }
        } else {
            // undo the read
            reader.reset();
            line = null;
        }

        // Gobble whitespace
        while (line != null) {
            if (!"".equals(line.trim())) {
                // If the line is not empty, write it to buffer and break loop
                buffer.append(getEncodedLine(line));
                buffer.append("\n");
                break;
            }
            line = reader.readLine();
        }
    }

    private String getEncodedLine(String line) {

        if (isJava) {
            line = ClickUtils.escapeHtml(line);

            // Check if line is part of multiline comment
            if (isMultilineComment(line)) {
                line = renderComment(line);
            } else {
                // Check if line contains singleline comment
                String comment = "";
                if (hasComment(line)) {
                    comment = getComment(line);
                    comment = renderComment(comment);
                    line = removeComment(line);
                }

                if (StringUtils.isNotBlank(line)) {
                    for (int i = 0; i < JAVA_KEYWORDS.length; i++) {
                        String keyword = JAVA_KEYWORDS[i];
                        line = renderJavaKeywords(line, keyword);
                    }
                }
                line = line + comment;
            }

        } else if (isJavaScript) {
            line = ClickUtils.escapeHtml(line);

            // Check if line is part of multiline comment
            if (isMultilineComment(line)) {
                line = renderComment(line);
            } else {
                // Check if line contains singleline comment
                String comment = "";
                if (hasComment(line)) {
                    comment = getComment(line);
                    comment = renderComment(comment);
                    line = removeComment(line);
                }

                if (StringUtils.isNotBlank(line)) {
                    for (int i = 0; i < JAVASCRIPT_KEYWORDS.length; i++) {
                        String keyword = JAVASCRIPT_KEYWORDS[i];
                        // Reuse renderJavaKeywords method
                        line = renderJavaKeywords(line, keyword);
                    }
                }
                line = line + comment;
            }

        } else if (isHtml) {
            line = ClickUtils.escapeHtml(line);

            for (int i = 0; i < HTML_KEYWORDS.length; i++) {
                String keyword = HTML_KEYWORDS[i];
                line = renderHtmlKeywords(line, keyword);
            }

            for (int i = 0; i < VELOCITY_KEYWORDS.length; i++) {
                String keyword = VELOCITY_KEYWORDS[i];
                line = renderVelocityKeywords(line, keyword);
            }

            String renderedDollar = "<font color=\"red\">$</font>";

            line = StringUtils.replace(line, "$", renderedDollar);

        } else if (isXml) {
            line = ClickUtils.escapeHtml(line);

            for (int i = 0; i < XML_KEYWORDS.length; i++) {
                String keyword = XML_KEYWORDS[i];
                line = renderXmlKeywords(line, keyword);
            }

        } else {
            line = ClickUtils.escapeHtml(line);
        }

        return line;
    }

    private String renderJavaKeywords(String line, String token) {
        String markupToken = renderJavaToken(token);

        line = StringUtils.replace
            (line, " " + token + " ", " " + markupToken + " ");

        if (line.startsWith(token)) {
            line = markupToken + line.substring(token.length());
        }

        if (line.endsWith(token)) {
            line = line.substring(0, line.length() - token.length())
                    + markupToken;
        }

        return line;
    }

    private boolean hasComment(String line) {
        return line.indexOf("//") != -1;
    }

    private String removeComment(String line) {
        int lineCommentStart = line.indexOf("//");
        if (lineCommentStart != -1) {
            line = line.substring(0, lineCommentStart);
        }
        return line;
    }

    private String getComment(String line) {
        int lineCommentStart = line.indexOf("//");
        if (lineCommentStart != -1) {
            return line.substring(lineCommentStart);
        }
        return "";
    }

    private boolean isMultilineComment(String line) {
        boolean isComment = false;
        line = line.trim();
        if (line.startsWith("/*")) {
            isComment = true;
            javaMultilineComment = true;
        }
        if (line.endsWith("*/")) {
            isComment = true;
            javaMultilineComment = false;
        }
        if (javaMultilineComment && line.startsWith("*")) {
            isComment = true;
        }
        return isComment;
    }

    private String renderVelocityKeywords(String line, String token) {
        String markupToken = renderVelocityToken(token);

        line = StringUtils.replace
            (line, " " + token + " ", " " + markupToken + " ");

        if (line.startsWith(token)) {
            line = markupToken + line.substring(token.length());
        }

        if (line.endsWith(token)) {
            line = line.substring(0, line.length() - token.length())
                    + markupToken;
        }

        return line;
    }

    private String renderHtmlKeywords(String line, String token) {

        String markupToken = "&lt;" + token + "&gt;";
        String renderedToken = "&lt;" + renderHtmlToken(token) + "&gt;";
        line = StringUtils.replace(line, markupToken, renderedToken);

        markupToken = "&lt;" + token + "/&gt;";
        renderedToken = "&lt;" + renderHtmlToken(token) + "/&gt;";
        line = StringUtils.replace(line, markupToken, renderedToken);

        markupToken = "&lt;/" + token + "&gt;";
        renderedToken = "&lt;/" + renderHtmlToken(token) + "&gt;";
        line = StringUtils.replace(line, markupToken, renderedToken);

        markupToken = "&lt;" + token + " ";
        renderedToken = "&lt;" + renderHtmlToken(token) + " ";
        line = StringUtils.replace(line, markupToken, renderedToken);

        return line;
    }

    private String renderXmlKeywords(String line, String token) {

        String markupToken = "&lt;" + token + "&gt;";
        String renderedToken = "&lt;" + renderXmlToken(token) + "&gt;";
        line = StringUtils.replace(line, markupToken, renderedToken);

        markupToken = "&lt;" + token + "/&gt;";
        renderedToken = "&lt;" + renderXmlToken(token) + "/&gt;";
        line = StringUtils.replace(line, markupToken, renderedToken);

        markupToken = "&lt;/" + token + "&gt;";
        renderedToken = "&lt;/" + renderXmlToken(token) + "&gt;";
        line = StringUtils.replace(line, markupToken, renderedToken);

        markupToken = "&lt;" + token + " ";
        renderedToken = "&lt;" + renderXmlToken(token) + " ";
        line = StringUtils.replace(line, markupToken, renderedToken);

        return line;
    }

    private String renderHtmlToken(String token) {
        return "<font color=\"#00029F\">" + token + "</font>";
    }

    private String renderXmlToken(String token) {
        return "<font color=\"#00029F\">" + token + "</font>";
    }

    private String renderVelocityToken(String token) {
        return "<font color=\"red\">" + token + "</font>";
    }

    private String renderJavaToken(String token) {
        return "<font color=\"#7f0055\"><b>" + token + "</b></font>";
    }

    private String renderComment(String comment) {
        return "<font color=\"#3F7F5F\">" + comment + "</font>";
    }
}
