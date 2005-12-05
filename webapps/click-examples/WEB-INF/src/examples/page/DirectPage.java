package examples.page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.Page;
import net.sf.click.util.ClickUtils;

/**
 * Provides a example direct <tt>HttpServletResponse</tt> handling.
 *
 * @author Malcolm Edgar
 */
public class DirectPage extends Page {

    private static final String SRC_FILENAME =
        "/WEB-INF/src/examples/page/DirectPage.java";

    /**
     * Render the Java source file as "text/plain".
     *
     * @see Page#onGet()
     */
    public void onGet() {
        HttpServletResponse response = getContext().getResponse();

        response.setContentType("text/plain");
        response.setHeader("Pragma", "no-cache");

        ServletContext context = getContext().getServletContext();

        InputStream inputStream = null;
        try {
            inputStream = context.getResourceAsStream(SRC_FILENAME);

            PrintWriter writer = response.getWriter();

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();

            while (line != null) {
                writer.println(line);
                line = reader.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();

        } finally {
            ClickUtils.close(inputStream);
        }
    }

    /**
     * Return null to specify no further rendering required.
     *
     * @see Page#getPath()
     */
    public String getPath() {
        return null;
    }

}
