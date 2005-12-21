package net.sf.click;

import java.io.IOException;

import javax.servlet.ServletContext;

/**
 * Provide an interface for Deployable classes which can deploy static
 * resources when the Click application is initialized.
 *
 * @author Malcolm Edgar
 */
public interface Deployable {

    /**
     * The on deploy event handler, which provides classes the
     * opportunity to deploy static resources when the Click application is
     * initialized.
     * <p/>
     * Controls which are defined in the <tt>click.xml</tt> application
     * configuration, or the <tt>click-controls.xml</tt> or
     * <tt>extras-controls.xml</tt> files will have the <tt>onDeploy()</tt>
     * method invoked by the <tt>ClickServlet</tt> when the servlet is
     * initialised.
     *
     * @param servletContext the servlet context
     * @throws IOException if a resource could not be deployed
     */
    public void onDeploy(ServletContext servletContext) throws IOException;

}
