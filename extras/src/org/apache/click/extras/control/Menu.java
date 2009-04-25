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
package org.apache.click.extras.control;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.click.Context;
import org.apache.click.control.AbstractControl;
import org.apache.click.extras.security.AccessController;
import org.apache.click.extras.security.RoleAccessController;
import org.apache.click.service.ConfigService;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a hierarchical Menu control.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2'src='menu.png' title='Menu'/>
 * </td>
 * </tr>
 * </table>
 *
 * Application menus are defined using a <tt>/WEB-INF</tt> configuration
 * file located under the <tt>/WEB-INF</tt> directory or the root classpath.
 * An example Menu configuration files is provided below.
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;menu&gt;
 *    &lt;menu label="Home" path="user/home.htm" roles="tomcat, role1"/&gt;
 *    &lt;menu label="User" path="user/home.htm" roles="tomcat, role1"&gt;
 *        &lt;menu label="User Page 1" path="user/user-1.htm" roles="tomcat, role1"/&gt;
 *        &lt;menu label="User Page 2" path="user/user-2.htm" roles="tomcat, role1"/&gt;
 *    &lt;/menu&gt;
 *    &lt;menu label="Admin" path="admin/admin-1.htm" roles="role1"&gt;
 *        &lt;menu label="Admin Page 1" path="admin/admin-1.htm" roles="tomcat, role1"/&gt;
 *        &lt;menu label="Admin Page 2" path="admin/admin-2.htm" roles="tomcat, role1"/&gt;
 *    &lt;/menu&gt;
 * &lt;/menu&gt; </pre>
 *
 * To include the root menu item in your page, simply use the default Menu constructor:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> MenuPage <span class="kw">extends</span> Page {
 *
 *     <span class="kw">public</span> Menu rootMenu = Menu.getRootMenu();
 *
 *     <span class="kw">public</span> ActionLink logoutLink = <span class="kw">new</span> ActionLink(<span class="kw">this</span>, <span class="st">"onLogoutClick"</span>);
 *
 * } </pre>
 *
 * To render the configured Menu hierarchy you will need to use a Velocity
 * #macro or Velocity code in your page. For example:
 *
 * <pre class="codeHtml">
 * <span class="red">#</span>writeMenu(<span class="st">$rootMenu</span>) </pre>
 *
 * An example menu Velocity macro is provided below:
 *
 * <pre class="codeHtml">
 * <span class="red">#macro</span>( writeMenu <span class="st">$rootMenu</span> )
 *
 * &lt;table id="menuTable" border="0" width="100%" cellspacing="0" cellpadding="0" style="margin-top: 2px;"&gt;
 *  &lt;tr&gt;
 *   &lt;td&gt;
 *
 * &lt;div id="searchbar"&gt;
 * &lt;div class="menustyle" id="menu"&gt;
 *   &lt;ul class="menubar" id="dmenu"&gt;
 *     <span class="red">#foreach</span> (<span class="st">$topMenu</span> <span class="red">in</span> <span class="st">$rootMenu.children</span>)
 *       <span class="red">#if</span> (<span class="st">$topMenu.isUserInRoles</span>() || <span class="st">$topMenu.isUserInChildMenuRoles</span>())
 *         <span class="red">#if</span> (<span class="st">$topMenu.children.empty</span>)
 *           &lt;li class="topitem"&gt;<span class="st">$topMenu</span>&lt;/li&gt;
 *         <span class="red">#else</span>
 *           &lt;li class="topitem"&gt;<span class="st">$topMenu</span>
 *             &lt;ul class="submenu"
 *             <span class="red">#foreach</span> (<span class="st">$subMenu</span> <span class="red">in</span> <span class="st">$topMenu.children</span>)
 *               <span class="red">#if</span> (<span class="st">$subMenu.isUserInRoles</span>())
 *                 &gt;&lt;li&gt;<span class="st">$subMenu</span>&lt;/li
 *               <span class="red">#end</span>
 *             <span class="red">#end</span>
 *             &gt;&lt;/ul&gt;
 *           &lt;/li&gt;
 *         <span class="red">#end</span>
 *       <span class="red">#end</span>
 *     <span class="red">#end</span>
 *     <span class="red">#if</span> (<span class="st">$request.remoteUser</span>)
 *         &lt;li class="topitem"&gt;&lt;a href="<span class="st">$logoutLink.href</span>"&gt;Logout&lt;/a&gt;&lt;/li&gt;
 *     <span class="red">#end</span>
 *   &lt;/ul&gt;
 *  &lt;/div&gt;
 * &lt;/div&gt;
 *
 *   &lt;/td&gt;
 *  &lt;/tr&gt;
 * &lt;/table&gt;
 *
 * <span class="red">#end</span> </pre>
 *
 * This example uses J2EE role path based security to only display the menu items
 * the user is authorized to see. If you not using this security feature in your
 * application you should remove the macro {@link #isUserInRoles()} checks so
 * the menu items will be rendered.
 * <p/>
 * Note individual menu items will render themselves as simple anchor tags using
 * their {@link #toString()} method. For more fine grain control you should
 * extend your Velocity macro to render individual menu items.
 *
 * <h3>Security</h3>
 *
 * Menus support J2EE role based security via the {@link #isUserInRoles()}
 * method. When creating secure menus define the valid roles in the menu items.
 * For example:
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;menu&gt;
 *    &lt;menu label="Home" path="user/home.htm" roles="user,admin"&gt;
 *       &lt;menu label="Home" path="user/home.htm" roles="user,admin"/&gt;
 *       &lt;menu label="Search" path="user/search.htm" roles="user,admin"/&gt;
 *    &lt;/menu&gt;
 *    &lt;menu label="Admin" path="admin/admin.htm"&gt;
 *       &lt;menu label="Home" path="admin/admin.htm" roles="admin"/&gt;
 *    &lt;/menu&gt;
 * &lt;/menu&gt; </pre>
 *
 * <h3>Menu Configuration DTD</h3>
 *
 * The Menu config file DTD is provided below:
 *
 * <pre class="codeConfig">
 * &lt;!-- The Menu (menu.xml) Document Type Definition. --&gt;
 * &lt;!ELEMENT <span class="red">menu</span> (<span class="st">menu</span>*)&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">label</span> CDATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">path</span> CDATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">target</span> CDATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">title</span> CDATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">imageSrc</span> CDATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">external</span> (true|false) "false"&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">separator</span> (true|false) "false"&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">roles</span> CDATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">pages</span> CDATA #IMPLIED&gt; </pre>
 *
 * @author Malcolm Edgar
 */
public class Menu extends AbstractControl {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /**
     * The menu configuration filename: &nbsp; "<tt>/WEB-INF/menu.xml</tt>".
     */
    protected static final String DEFAULT_CONFIG_FILE = "/WEB-INF/menu.xml";

    /** The HTML imports statements. */
    public static final String HTML_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/menu{1}.css\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/control{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/extras-control{1}.js\"></script>\n"
        + "<script type=\"text/javascript\">addLoadEvent(function() '{ initMenu();' });</script>\n";

    // -------------------------------------------------------- Class Variables

    /** The cached root Menu as defined in <tt>menu.xml</tt>. */
    protected static Menu rootMenu;

    // ----------------------------------------------------- Instance Variables

    /** The menu security access controller. */
    protected AccessController accessController;

    /** The list of submenu items. */
    protected List children = new ArrayList();

    /**
     * The menu path is to an external page flag, by default this value is false.
     */
    protected boolean external;

    /**
     * The image src path attribute.  If the image src is defined then a
     * <tt>&lt;img/&gt;</tt> element will rendered inside the link when
     * using the Menu {@link #toString()} method.
     * <p/>
     * If the image src value is prefixed with '/' then the request context path
     * will be prefixed to the src value when rendered by the control.
     */
    protected String imageSrc;

    /** The menu display label. */
    protected String label;

    /**
     * The list of valid page paths. If any of these page paths match the
     * current request then the Menu item will be selected.
     */
    protected List pages = new ArrayList();

    /** The menu path. */
    protected String path;

    /** The list of valid role names. */
    protected List roles = new ArrayList();

    /** The menu separator flag. */
    protected boolean separator;

    /** The target attribute. */
    protected String target = "";

    /** The tooltip title attribute. */
    protected String title = "";

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new Menu instance.
     */
    public Menu() {
    }

    /**
     * Create a new Menu instance with the given name.
     *
     * @param name the name of the menu
     */
    public Menu(String name) {
        setName(name);
    }

    /**
     * Create a Menu from the given menu-item XML Element.
     *
     * @param menuElement the menu-item XML Element
     * @param accessController the menu access controller
     */
    protected Menu(Element menuElement, AccessController accessController) {
        if (menuElement == null) {
            throw new IllegalArgumentException("Null menuElement parameter");
        }
        if (accessController == null) {
            throw new IllegalArgumentException("Null accessController parameter");
        }

        setAccessController(accessController);

        setLabel(menuElement.getAttribute("label"));

        setImageSrc(menuElement.getAttribute("imageSrc"));

        setPath(menuElement.getAttribute("path"));

        String titleAtr = menuElement.getAttribute("title");
        if (StringUtils.isNotBlank(titleAtr)) {
            setTitle(titleAtr);
        }

        String targetAtr = menuElement.getAttribute("target");
        if (StringUtils.isNotBlank(targetAtr)) {
            setTarget(targetAtr);
        }

        String externalAtr = menuElement.getAttribute("external");
        if ("true".equalsIgnoreCase(externalAtr)) {
            setExternal(true);
        }

        String separatorAtr = menuElement.getAttribute("separator");
        if ("true".equalsIgnoreCase(separatorAtr)) {
            setSeparator(true);
        }

        String pagesValue = menuElement.getAttribute("pages");
        if (!StringUtils.isBlank(pagesValue)) {
            StringTokenizer tokenizer = new StringTokenizer(pagesValue, ",");
            while (tokenizer.hasMoreTokens()) {
                String path = tokenizer.nextToken().trim();
                path = (path.startsWith("/")) ? path : "/" + path;
                getPages().add(path);
            }
        }

        String rolesValue = menuElement.getAttribute("roles");
        if (!StringUtils.isBlank(rolesValue)) {
            StringTokenizer tokenizer = new StringTokenizer(rolesValue, ",");
            while (tokenizer.hasMoreTokens()) {
                getRoles().add(tokenizer.nextToken().trim());
            }
        }

        NodeList childElements = menuElement.getChildNodes();
        for (int i = 0, size = childElements.getLength(); i < size; i++) {
            Node node = childElements.item(i);
            if (node instanceof Element) {
                Menu childMenu = new Menu((Element) node, accessController);
                getChildren().add(childMenu);
            }
        }
    }

    // ---------------------------------------------------- Constructor Methods

    /**
     * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, and which uses JEE Role Based Access Control (RBAController).
     *
     * @see RoleAccessController
     *
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    public static Menu getRootMenu() {
        return getRootMenu(new RoleAccessController());
    }

    /**
     * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, and which uses the provided AccessController.
     *
     * @param accessController the menu access controller
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    public static Menu getRootMenu(AccessController accessController) {
        if (accessController == null) {
            throw new IllegalArgumentException("Null accessController parameter");
        }

        // If menu is cached return it
        if (rootMenu != null) {
            return rootMenu;
        }

        Menu loadedMenu = loadRootMenu(accessController);

        ServletContext servletContext = Context.getThreadLocalContext().getServletContext();
        ConfigService configService = ClickUtils.getConfigService(servletContext);

        if (configService.isProductionMode() || configService.isProfileMode()) {
            // Cache menu in production modes
            rootMenu = loadedMenu;
        }

        return loadedMenu;
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the menu access controller.
     *
     * @return the menu access controller
     */
    public AccessController getAccessController() {
        return accessController;
    }

    /**
     * Set the menu access controller.
     *
     * @param accessController the menu access controller
     */
    public void setAccessController(AccessController accessController) {
        this.accessController = accessController;
    }

    /**
     * Return list of of submenu items.
     *
     * @return the list of submenu items
     */
    public List getChildren() {
        return children;
    }

    /**
     * Return true if the menu path refers to an external resource.
     *
     * @return true if the menu path refers to an external resource
     */
    public boolean isExternal() {
        return external;
    }

    /**
     * Set whether the menu path refers to an external resource.
     *
     * @param value the flag as to whether the menu path refers to an external resource
     */
    public void setExternal(boolean value) {
        external = value;
    }

    /**
     * Return the image src path attribute. If the image src is defined then a
     * <tt>&lt;img/&gt;</tt> element will rendered inside the link when
     * using the Menu {@link #toString()} method.
     * <p/>
     * If the src value is prefixed with '/' then the request context path will
     * be prefixed to the src value when rendered by the control.
     *
     * @return the image src path attribute
     */
    public String getImageSrc() {
        return imageSrc;
    }

    /**
     * Set the image src path attribute. If the src value is prefixed with
     * '/' then the request context path will be prefixed to the src value when
     * rendered by the control.
     *
     * @param src the image src path attribute
     */
    public void setImageSrc(String src) {
        this.imageSrc = src;
    }

    /**
     * Return the label of the Menu item.
     *
     * @return the label of the Menu item
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label of the Menu item.
     *
     * @param label the label of the Menu item
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Return the list of valid Page paths for the Menu item. If any of these
     * page paths match the current request then the Menu item will be selected.
     *
     * @return the list of valid Page paths
     */
    public List getPages() {
        return pages;
    }

    /**
     * Set the list of valid Page paths.  If any of these page paths match the
     * current request then the Menu item will be selected.
     *
     * @param pages the list of valid Page paths
     */
    public void setPages(List pages) {
        this.pages = pages;
    }

    /**
     * Return the path of the Menu item.
     *
     * @return the path of the Menu item
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the path of the Menu item.
     *
     * @param path the path of the Menu item
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Return the list of valid roles for the Menu item.
     *
     * @return the list of valid roles for the Menu item
     */
    public List getRoles() {
        return roles;
    }

    /**
     * Set the list of valid roles for the Menu item.
     *
     * @param roles the list of valid roles for the Menu item
     */
    public void setRoles(List roles) {
        this.roles = roles;
    }

    /**
     * Return true if the Menu item is selected.
     *
     * @return true if the Menu item is selected
     */
    public boolean isSelected() {
        if (this == rootMenu) {
            return true;
        }

        final String pageToView = getContext().getResourcePath();

        boolean selected = false;

        if (getPages().contains(pageToView)) {
            selected = true;

        } else {
            String path = getPath();
            if (path != null) {
                path = path.startsWith("/") ? path : "/" + path;
                selected = path.equals(pageToView);
            } else {
                selected = false;
            }
        }

        for (int i = 0; i < getChildren().size(); i++) {
            Menu menu = (Menu) getChildren().get(i);
            if (menu.isSelected()) {
                selected = true;
            }
        }

        return selected;
    }

    /**
     * Return true if the Menu item is a separator.
     *
     * @return true if the Menu item is a separator
     */
    public boolean isSeparator() {
        return separator;
    }

    /**
     * Set whether the Menu item is a separator.
     *
     * @param separator the flag indicating whether the Menu item is a separator
     */
    public void setSeparator(boolean separator) {
        this.separator = separator;
    }

    /**
     * Return true if the user is in one of the menu roles, or if any child
     * menus have the user in one of their menu roles. Otherwise the method will
     * return false.
     * <p/>
     * This method internally uses the <tt>HttpServletRequest</tt> function <tt>isUserInRole(rolename)</tt>,
     * where the rolenames are derived from the {@link #getRoles()} property.
     *
     * @return true if the user is in one of the menu roles, or false otherwise
     */
    public boolean isUserInRoles() {
        HttpServletRequest request = getContext().getRequest();

        for (Iterator i = getRoles().iterator(); i.hasNext();) {
            String rolename = (String) i.next();
            if (getAccessController().hasAccess(request, rolename)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return true if any child menus have the user in one of their menu roles.
     * Otherwise the method will return false.
     * <p/>
     * This method internally uses the <tt>HttpServletRequest</tt> function <tt>isUserInRole(rolename)</tt>,
     * where the rolenames are derived from the {@link #getRoles()} property.
     *
     * @return true if the user is in one of the child menu roles, or false otherwise
     */
    public boolean isUserInChildMenuRoles() {
        for (Iterator i = getChildren().iterator(); i.hasNext();) {
            Menu child = (Menu) i.next();
            if (child.isUserInRoles()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return the target attribute of the Menu item.
     *
     * @return the target attribute of the Menu item
     */
    public String getTarget() {
        return target;
    }

    /**
     * Set the target attribute of the Menu item.
     *
     * @param target the target attribute of the Menu item
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Return the title attribute of the Menu item.
     *
     * @return the title attribute of the Menu item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title attribute of the Menu item.
     *
     * @param title the title attribute of the Menu item
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet
     * (<tt>click/menu.css</tt>) and JavaScript (<tt>click/extras-control.js</tt>) files.
     *
     * @see org.apache.click.Control#getHtmlImports()
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        return ClickUtils.createHtmlImport(HTML_IMPORTS, getContext());
    }

    /**
     * Return the menu anchor HREF attribute. If the menu is referring
     * to an external path, this method will simply return the path,
     * otherwise it will return the menu path prefixed with the
     * request context path.
     * <p/>
     * If the path refers to a  hash "#" symbol, this method will return
     * a "#". It is useful to assign a "#" to the path of a menu item
     * containing children, because most modern browsers will not submit
     * the page if clicked on.
     *
     * @return the menu anchor HREF attribute
     */
    public String getHref() {
        if (isExternal()) {
            return getPath();

        } else if ("#".equals(getPath())) {
            return getContext().getResponse().encodeURL(getPath());

        } else {
            Context context = getContext();
            return context.getResponse().encodeURL(context.getRequest().getContextPath() + "/" + getPath());
        }
    }

    /**
     * This method returns null.
     *
     * @see org.apache.click.Control#getId()
     *
     * @return null
     */
    public String getId() {
        return null;
    }

    /**
     * This method does nothing.
     *
     * @see org.apache.click.Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Deploy the <tt>menu.css</tt> and <tt>menu.js</tt> files to the
     * <tt>click</tt> web directory when the application is initialized.
     *
     * @see org.apache.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        String[] files = new String[] {
                "/org/apache/click/extras/control/menu.css",
                "/org/apache/click/extras/control/extras-control.js"
            };

        ClickUtils.deployFiles(servletContext, files, "click");
    }

    /**
     * This sets the parent to be null.
     *
     * @see org.apache.click.Control#onDestroy()
     */
    public void onDestroy() {
        setParent(null);
    }

    /**
     * Render the HTML representation of the Menu.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {

        if (isSeparator()) {
            buffer.append("<hr/>");

        } else {
            buffer.elementStart("a");

            String href = getHref();
            buffer.appendAttribute("href", href);

            if ("#".equals(href)) {
                //If hyperlink does not return false here, clicking on it will scroll
                //to the top of the page.
                buffer.appendAttribute("onclick", "return false;");
            }

            if (getTarget() != null && getTarget().length() > 0) {
                buffer.appendAttribute("target", getTarget());
            }

            if (getTitle() != null && getTitle().length() > 0) {
                buffer.appendAttributeEscaped("title", getTitle());
            }

            buffer.closeTag();

            if (StringUtils.isNotBlank(getImageSrc())) {
                buffer.elementStart("img");
                buffer.appendAttribute("border", "0");
                buffer.appendAttribute("class", "link");

                if (getTitle() != null) {
                    buffer.appendAttributeEscaped("alt", getTitle());
                } else {
                    buffer.appendAttributeEscaped("alt", getLabel());
                }

                String src = getImageSrc();
                if (StringUtils.isNotBlank(src)) {
                    if (src.charAt(0) == '/') {
                        src = getContext().getRequest().getContextPath() + src;
                    }
                    buffer.appendAttribute("src", src);
                }

                buffer.elementEnd();

                if (getLabel() != null) {
                    buffer.append(getLabel());
                }

            } else {
                buffer.append(getLabel());
            }

            buffer.elementEnd("a");
        }
    }

    /**
     * Return an HTML anchor tag representation of the menu item. If the menu is
     * a separator this method will return a HR tag &lt;hr/&gt;.
     * <p/>
     * Note for more fine grained rendering control you should use a Velocity
     * #macro to render the menu item.
     *
     * @see Object#toString()
     *
     * @return an HTML anchor tag representation of the menu item
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        render(buffer);
        return buffer.toString();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the selected child menu, or null if no child menu is selected.
     *
     * @return the selected child menu
     */
    public Menu getSelectedChild() {
        if (isSelected()) {
            for (Iterator i = getChildren().iterator(); i.hasNext();) {
                Menu menu = (Menu) i.next();
                if (menu.isSelected()) {
                    return menu;
                }
            }
        }
        return null;
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return a copy of the Applications root Menu as defined in the
     * configuration file "<tt>/WEB-INF/menu.xml</tt>", with the Control
     * name <tt>"rootMenu"</tt>.
     * <p/>
     * The returned root menu is always selected.
     *
     * @param accessController the menu access controller
     * @return a copy of the application's root Menu
     */
    protected static Menu loadRootMenu(AccessController accessController) {
        if (accessController == null) {
            throw new IllegalArgumentException("Null accessController parameter");
        }

        Context context = Context.getThreadLocalContext();

        Menu menu = new Menu("rootMenu");
        menu.setAccessController(accessController);

        ServletContext servletContext = context.getServletContext();
        InputStream inputStream =
            servletContext.getResourceAsStream(DEFAULT_CONFIG_FILE);

        if (inputStream == null) {
            inputStream = ClickUtils.getResourceAsStream("/menu.xml", Menu.class);
            String msg =
                "could not find configuration file:" + DEFAULT_CONFIG_FILE
                + " or menu.xml on classpath";
            throw new RuntimeException(msg);
        }

        Document document = ClickUtils.buildDocument(inputStream);

        Element rootElm = document.getDocumentElement();

        NodeList list = rootElm.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Menu childMenu = new Menu((Element) node, accessController);
                menu.getChildren().add(childMenu);
            }
        }

        return menu;
    }

}
