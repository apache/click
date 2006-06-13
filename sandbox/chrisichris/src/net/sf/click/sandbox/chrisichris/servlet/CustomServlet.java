package net.sf.click.sandbox.chrisichris.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;

import net.sf.click.ClickServlet;
import net.sf.click.Page;
import net.sf.click.util.Format;
import net.sf.click.util.PageImports;
import net.sf.click.util.SessionMap;

/**
 * Serlvet which makes the public fields and getter available in the context.
 * @author Christian
 *
 */
public class CustomServlet extends ClickServlet{

    /**
     * Return a new VelocityContext for the given pages model and Context.
     *
     * @param page the page to create a VelocityContext for
     * @return a new VelocityContext
     */
    protected VelocityContext createVelocityContext(Page page) {

        final VelocityContext parentContext = new VelocityContext(new PropertyMap(page));
        final VelocityContext context = new VelocityContext(page.getModel(),parentContext);

        final HttpServletRequest request = page.getContext().getRequest();

        Object pop = context.put("request", request);
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"request\". The page model object "
                         + pop + " has been replaced with the request object";
            logger.warn(msg);
        }

        pop = context.put("response", page.getContext().getResponse());
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"response\". The page model object "
                         + pop + " has been replaced with the response object";
            logger.warn(msg);
        }

        SessionMap sessionMap = new SessionMap(request.getSession(false));
        pop = context.put("session", sessionMap);
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"session\". The page model object "
                         + pop + " has been replaced with the request "
                         + " session";
            logger.warn(msg);
        }

        pop = context.put("context", request.getContextPath());
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"context\". The page model object "
                         + pop + " has been replaced with the request "
                         + " context path";
            logger.warn(msg);
        }

        Format format = page.getFormat();
        if (format != null) {
           pop = context.put("format", format);
            if (pop != null) {
                String msg = page.getClass().getName() + " on "
                        + page.getPath()
                        + " model contains an object keyed with reserved "
                        + "name \"format\". The page model object " + pop
                        + " has been replaced with the format object";
                logger.warn(msg);
            }
        }

        Object path = page.getPath();
        if (path != null) {
           pop = context.put("path", path);
            if (pop != null) {
                String msg = page.getClass().getName() + " on "
                        + page.getPath()
                        + " model contains an object keyed with reserved "
                        + "name \"path\". The page model object " + pop
                        + " has been replaced with the page path";
                logger.warn(msg);
            }
        }

        pop = context.put("messages", page.getMessages());
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"messages\". The page model object "
                         + pop + " has been replaced with the request "
                         + " messages";
            logger.warn(msg);
        }

        pop = context.put("imports", new PageImports(page));
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"imports\". The page model object "
                         + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }

        return context;
    }
    
}
