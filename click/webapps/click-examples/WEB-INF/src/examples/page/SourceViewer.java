package examples.page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.click.Page;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a Java source code, HTML and XML examples rendering page.
 *
 * @author Malcolm Edgar
 */
public class SourceViewer extends Page {

    private static final String COLOR = "navy";

    private static final String[] JAVA_KEYWORDS = { "package", "import",
            "class", "public", "protected", "private", "extends", "implements",
            "return", "if", "while", "for", "do", "else", "try",
            "catch", "throws", "throw", "static", "final", "break", "continue" };

    private static final String[] HTML_KEYWORDS = { "html", "head", "style",
            "script", "title", "link", "body", "h1", "h2", "h3", "h4", "h5",
            "h6", "p", "hr", "br", "span", "table", "tr", "th", "td", "a", "b",
            "i", "u", "ul", "ol", "li" };

    private static final String[] VELOCITY_KEYWORDS = { "#if", "#if(", "#else",
            "#else(", "#elseif", "#elseif(", "#end", "#set", "#set(",
            "#include", "#include(", "#parse", "#parse(", "#stop", "#macro",
            "#macro(", "#foreach", "#foreach("};

    private boolean isJava = false;

    private boolean isXml = false;

    private boolean isHtml = false;

    /**
     * @see Page#onGet()
     */
    public void onGet() {
        HttpServletRequest request = getContext().getRequest();

        String filename = request.getParameter("filename");

        if (filename != null) {
            loadFilename(filename);

        } else {
            addModel("error", "filename not defined");
        }
    }

    private void loadFilename(String filename) {
        ServletContext context = getContext().getServletContext();

        InputStream in = null;
        try {
            in = context.getResourceAsStream(filename);

            if (in != null) {

                loadResource(in, filename);

            } else {
                addModel("error", "File " + filename + " not found");
            }

        } catch (IOException e) {
            addModel("error", "Could not read " + filename);

        } finally {
            ClickUtils.close(in);
        }
    }

    private void loadResource(InputStream inputStream, String name)
            throws IOException {

        isJava = name.endsWith(".java");
        isXml = name.endsWith(".xml");
        isHtml = name.endsWith(".htm");
        if (!isHtml) {
            isHtml = name.endsWith(".html");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));

        StringBuffer buffer = new StringBuffer();

        String line = reader.readLine();

        while (line != null) {
            buffer.append(getEncodedLine(line, name));
            buffer.append("\n");
            line = reader.readLine();
        }

        addModel("source", buffer.toString());

        addModel("name", name);
    }

    private String getEncodedLine(String line, String name) {

        if (isJava) {
            line = ClickUtils.toHtmlEncodeNoBreaks(line);

            for (int i = 0; i < JAVA_KEYWORDS.length; i++) {
                String keyword = JAVA_KEYWORDS[i];
                line = renderJavaKeywords(line, keyword);
            }

        } else if (isHtml) {
            line = ClickUtils.toHtmlEncodeNoBreaks(line);

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

        } else {
            line = ClickUtils.toHtmlEncodeNoBreaks(line);
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

    private String renderHtmlToken(String token) {
        return "<font color=\"navy\">" + token + "</font>";
    }

    private String renderVelocityToken(String token) {
        return "<font color=\"red\">" + token + "</font>";
    }

    private String renderJavaToken(String token) {
        return "<font color=\"navy\"><b>" + token + "</b></font>";
    }
}
