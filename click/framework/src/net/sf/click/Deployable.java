package net.sf.click;

import java.io.IOException;

import javax.servlet.ServletContext;

/**
 * Provide an interface for deploying of static web resources when Click
 * starts up.
 * <p/>
 * For example a custom TextField control could <tt>custom.js</tt> file to the
 * click directory:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> CustomField <span class="kw">extends</span> TextField {
 *
 *     <span class="kw">public void</span> onDeploy(ServletContext servletContext) <span class="kw">throws</span> IOException {
 *         ClickUtils.deployFile
 *             (servletContext, <span class="st">"/com/mycorp/control/custom.js"</span>, <span class="st">"click"</span>);
 *     }
 *
 *     ..
 * } </pre>
 *
 * This JavaScript file can then be included by the fields parent Form by
 * overridding the {@link net.sf.click.control.Field#getHtmlImports()} method:
 * For example:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> CustomField <span class="kw">extends</span> TextField {
 *     ..
 *
 *     <span class="kw">protected static final</span> String HTML_IMPORT =
 *         <span class="st">"&lt;script type=\"text/javascript\" src=\"{0}/click/custom.js\"&gt;&lt;/script&gt;\n"</span>;
 *
 *     <span class="kw">public</span> String getHtmlImports() {
 *         String[] args = { getContext().getRequest().getContextPath() };
 *         <span class="kw">return</span> MessageFormat.format(HTML_IMPORTS, args);
 *     }
 * } </pre>
 *
 * To enable a deployable class to be deployed on startup it must be
 * specified in your applications <tt>WEB-INF/click.xml</tt> file. For example:
 * <p/>
 *
 * <pre class="codeConfig">
 * &lt;click-app&gt;
 *   &lt;pages package="com.mycorp.page" automapping="true"/&gt;
 *
 *   &lt;deployables&gt;
 *     &lt;deployable classname=<span class="st">"com.mycorp.control.CustomField"</span>/&gt;
 *   &lt;/deployables&gt;
 * &lt;/click-app&gt; </pre>
 *
 * When the Click applicatin starts up it will deploy any deployable elements
 * defined in the following files:
 * <ul>
 *  <li><tt>/click-deployables.xml</tt>
 *  <li><tt>/extras-deployables.xml</tt>
 *  <li><tt>WEB-INF/click.xml</tt>
 * </ul>
 *
 * @author Malcolm Edgar
 */
public interface Deployable {

    /**
     * The on deploy event handler, which provides classes the
     * opportunity to deploy static resources when the Click application is
     * initialized.
     *
     * @param servletContext the servlet context
     * @throws IOException if a resource could not be deployed
     */
    public void onDeploy(ServletContext servletContext) throws IOException;

}
