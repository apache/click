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
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.click.Context;
import org.apache.click.control.AbstractControl;
import org.apache.click.element.CssImport;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
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
 * <h3><a name="configuration"></a>Configuration</h3>
 *
 * Application menus are normally defined using a configuration file
 * (<tt>menu.xml</tt> by default) located under the <tt>/WEB-INF</tt> directory
 * or the root classpath. An example Menu configuration file is provided below.
 *
 * <pre class="prettyprint">
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
 * Use a {@link MenuFactory} to load the Menu items and include the root menu
 * item in your page:
 *
 * <pre class="prettyprint">
 * public class BorderPage extends Page {
 *
 *     private Menu rootMenu;
 *
 *     public BorderPage() {
 *         MenuFactory menuFactory = new MenuFactory();
 *         rootMenu = menuFactory.getRootMenu();
 *         addControl(rootMenu);
 *     }
 *
 *     &#64;Override
 *     public String getTemplate() {
 *         return "/border-template.htm";
 *     }
 *
 * } </pre>
 *
 * <h3><a name="programmatic"></a>Programmatically defined menus</h3>
 *
 * It is also possible to create Menus programmatically, for example:
 *
 * <pre class="prettyprint">
 * public class BorderPage extends Page {
 *
 *     private static class Menu rootMenu;
 *
 *     public BorderPage() {
 *
 *         if (rootMenu == null) {
 *             rootMenu = new MenuBuilder().buildMenu();
 *         }
 *
 *         addControl(rootMenu);
 *     }
 * } </pre>
 *
 * <pre class="prettyprint">
 * public class MenuBuilder() {
 *
 *     public Menu buildMenu() {
 *
 *         Menu rootMenu = new Menu("rootMenu");
 *         rootMenu.add(createMenu("Home", "home.htm"));
 *
 *         Menu customerMenu = createMenu("Home", "home.htm");
 *         rootMenu.add(customerMenu);
 *
 *         customerMenu.add(createMenu("Search Customers", "search-customers.htm"));
 *         customerMenu.add(createMenu("Edit Customer", "edit-customer.htm"));
 *
 *         ...
 *
 *         return rootMenu;
 *     }
 *
 *     private Menu createMenu(String label, String path) {
 *         Menu menu = new Menu();
 *         menu.setLabel(label);
 *         menu.setPath(path);
 *         menu.setTitle(label);
 *         return menu;
 *     }
 * }</pre>
 *
 * <h3><a name="rendering"></a>Rendering</h3>
 *
 * To render the configured Menu hierarchy you can reference the root menu by
 * its name in the Velocity template. For example:
 * <pre class="codeHtml">
 * <span class="st">$rootMenu</span> </pre>
 *
 * The hierarchical Menu structure is rendered as an HTML list: &lt;ul&gt;.
 * <p/>
 *
 * Alternatively, you can render the menu using a Velocity #macro or Velocity
 * code in your template. For example:
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
 * This example uses role path based security to only display the menu items
 * the user is authorized to see. If you are not using this security feature in
 * your application you should remove the macro {@link #isUserInRoles()} checks so
 * the menu items will be rendered.
 * <p/>
 * Note individual menu items will render themselves as simple anchor tags using
 * their {@link #toString()} method. For more fine grain control you should
 * extend your Velocity macro to render individual menu items.
 *
 * <h3><a name="security"></a>Security</h3>
 *
 * Menus support role based security via the {@link #isUserInRoles()}
 * method. When creating secure menus define the valid roles in the menu items.
 * For example:
 *
 * <pre class="prettyprint">
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
 * The underlying implementation of isUserInRoles() method is provided by an
 * {@link AccessController} interface. The default AccessController is provided
 * by the {@link RoleAccessController} which uses the JEE container is user in
 * role facility. By providing your own AccessController you can have menu
 * access control using other security frameworks such as Spring
 * Security (Acegi) or Apache Shiro.
 *
 * <h3><a name="config-dtd"></a>Menu Configuration DTD</h3>
 *
 * The Menu config file DTD is provided below:
 *
 * <pre class="codeConfig">
 * &lt;!-- The Menu (menu.xml) Document Type Definition. --&gt;
 * &lt;!ELEMENT <span class="red">menu</span> (<span class="st">menu</span>*)&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">id</span> ID #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="st">name</span> CDATA #IMPLIED&gt;
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
 * The Menu DTD is also published online at
 * <a href="http://click.apache.org/dtds/menu-2.2.dtd">http://click.apache.org/dtds/menu-2.2.dtd</a>.
 *
 * <h3><a name="message-resources"></a>Message Resources and Internationalization (i18n)</h3>
 *
 * Menus automatically pick up localized messages where applicable. Please see
 * the following methods on how to customize these messages:
 * <ul>
 * <li>{@link #getLabel()}</li>
 * <li>{@link #getTitle()}</li>
 * </ul>
 *
 * <h3><a name="resources"></a>CSS and JavaScript resources</h3>
 *
 * The Menu control makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/menu.css</tt></li>
 * <li><tt>click/extras-control.js</tt></li>
 * </ul>
 *
 * To import these Menu files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 *
 * @see org.apache.click.extras.security.AccessController
 */
public class Menu extends AbstractControl {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    /**
     * The menu configuration filename: &nbsp; "<tt>/WEB-INF/menu.xml</tt>".
     */
    protected static final String DEFAULT_CONFIG_FILE = "/WEB-INF/menu.xml";

    // Class Variables --------------------------------------------------------

    /** The cached root Menu as defined in <tt>menu.xml</tt>. */
    protected static Menu rootMenu;

    // Instance Variables -----------------------------------------------------

    /** The menu security access controller. */
    protected transient AccessController accessController;

    /** The list of submenu items. */
    protected List<Menu> children;

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
    protected List<String> pages = new ArrayList<String>();

    /** The menu path. */
    protected String path;

    /** The list of valid role names. */
    protected List<String> roles;

    /** The menu separator flag. */
    protected boolean separator;

    /** The target attribute. */
    protected String target = "";

    /** The tooltip title attribute. */
    protected String title;

    // Constructors -----------------------------------------------------------

    /**
     * Create a new Menu instance.
     * <p/>
     * Please ensure you have defined a menu {@link #accessController} if the
     * menu's {@link #isUserInRoles()} method is going to be called.
     *
     * @see #Menu(java.lang.String)
     */
    public Menu() {
    }

    /**
     * Create a new Menu instance with the given name.
     * <p/>
     * Please ensure you have defined a menu {@link #accessController} if the
     * menu's {@link #isUserInRoles()} method is going to be called. For example:
     *
     * <pre class="prettyprint">
     * public class BorderPage extends Page {
     *
     *     ...
     *
     *     public void defineMenus() {
     *
     *         // Define an accessController
     *         AccessController accessController = new RoleAccessController();
     *
     *         // Retrieve some user roles
     *         List roles = securityService.getRoles();
     *
     *         Menu menu = new Menu("root");
     *         menu.setAccessController(accessController);
     *         menu.setRoles(roles);
     *
     *         Menu subMenu = new Menu("products");
     *         subMenu.setLabel("Products");
     *         subMenu.setAccessController(accessController);
     *         subMenu.setRoles(roles);
     *
     *         menu.add(subMenu);
     *
     *         ...
     *     }
     * } </pre>
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
     *
     * @deprecated use
     * {@link MenuFactory#buildMenu(org.w3c.dom.Element, org.apache.click.extras.security.AccessController, java.lang.Class)}
     * instead
     */
    @Deprecated
    protected Menu(Element menuElement, AccessController accessController) {
        if (menuElement == null) {
            throw new IllegalArgumentException("Null menuElement parameter");
        }
        if (accessController == null) {
            throw new IllegalArgumentException("Null accessController parameter");
        }

        setAccessController(accessController);

        String nameAtr = menuElement.getAttribute("name");
        if (StringUtils.isNotBlank(nameAtr)) {
            setName(nameAtr);
        }

        String labelAtr = menuElement.getAttribute("label");
        if (StringUtils.isNotBlank(labelAtr)) {
            setLabel(labelAtr);
        }

        String imageSrcAtr = menuElement.getAttribute("imageSrc");
        if (StringUtils.isNotBlank(imageSrcAtr)) {
            setImageSrc(imageSrcAtr);
        }

        String pathAtr = menuElement.getAttribute("path");
        if (StringUtils.isNotBlank(pathAtr)) {
            setPath(pathAtr);
        }

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
        if (StringUtils.isNotBlank(pagesValue)) {
            StringTokenizer tokenizer = new StringTokenizer(pagesValue, ",");
            while (tokenizer.hasMoreTokens()) {
                String path = tokenizer.nextToken().trim();
                path = (path.startsWith("/")) ? path : "/" + path;
                getPages().add(path);
            }
        }

        String rolesValue = menuElement.getAttribute("roles");
        if (StringUtils.isNotBlank(rolesValue)) {
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
                add(childMenu);
            }
        }
    }

    // Constructor Methods ----------------------------------------------------

    /**
     * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, and which uses JEE Role Based Access Control (RoleAccessController).
     *
     * @see RoleAccessController
     *
     * @deprecated use {@link MenuFactory#getRootMenu()} instead
     *
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    @Deprecated
    public static Menu getRootMenu() {
        return getRootMenu(new RoleAccessController());
    }

    /**
     * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, and which uses the provided AccessController.
     *
     * @deprecated use
     * {@link MenuFactory#getRootMenu(org.apache.click.extras.security.AccessController)}
     * instead
     *
     * @param accessController the menu access controller
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    @Deprecated
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

    // Public Attributes ------------------------------------------------------

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
     * Return true if the menu contains any child submenus.
     *
     * @return true if the menu contains any child submenus
     */
    public boolean hasChildren() {
        if (children == null || children.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Return list of of submenu items.
     *
     * @return the list of submenu items
     */
    public List<Menu> getChildren() {
        if (children == null) {
            children = new ArrayList<Menu>();
        }
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
     * Return the menu item display label.
     * <p/>
     * If the label value is null, this method will attempt to find a
     * localized label message in the parent messages of the root menu using the
     * key:
     *
     * <blockquote>
     * <tt>getName() + ".label"</tt>
     * </blockquote>
     *
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * If a value is still not found, the Menu name will be converted
     * into a label using the method: {@link ClickUtils#toLabel(String)}
     * <p/>
     * For example given the properties file <tt>src/click-page.properties</tt>:
     *
     * <pre class="codeConfig">
     * <span class="st">customers</span>.label=<span class="red">Customers</span>
     * <span class="st">customers</span>.title=<span class="red">Find a specific customer</span> </pre>
     *
     * The menu.xml (<b>note</b> that no label attribute is present):
     * <pre class="prettyprint">
     * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
     * &lt;menu&gt;
     *    &lt;menu name="customers" path="customers.htm" roles="view-customers"/&gt;
     *
     *    ...
     * &lt;/menu&gt; </pre>
     *
     * Will render the Menu label and title properties as:
     *
     * <pre class="codeHtml">
     * &lt;li&gt;&lt;a title="<span class="red">Find a specific customer</span>" ... &gt;<span class="red">Customers</span>&lt;/a&gt;&lt;/li&gt; </pre>
     *
     * When a label value is not set, or defined in any properties files, then
     * its value will be created from the Menu name.
     * <p/>
     * For example given the <tt>menu.xml</tt> file:
     *
     * <pre class="prettyprint">
     * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
     * &lt;menu&gt;
     *    &lt;menu name="product" path="product.htm" roles="view-product"/&gt;
     *
     *    ...
     * &lt;/menu&gt; </pre>
     *
     * Will render the Menu label as:
     *
     * <pre class="codeHtml">
     * &lt;li&gt;&lt;a ... &gt;<span class="red">Product</span>&lt;/a&gt;&lt;/li&gt; </pre>
     *
     * @return the display label of the Menu item
     */
    public String getLabel() {
        // Return cached label, if set
        if (label != null) {
            return label;
        }

        String localName = getName();

        if (localName != null) {
            Menu root = findRootMenu();

            // Use root menu messages to lookup the label
            String i18nLabel = root.getMessage(localName + ".label");

            if (i18nLabel == null) {
                i18nLabel = ClickUtils.toLabel(localName);
            }

            // NOTE: don't cache the i18nLabel, since menus are often cached
            // statically
            return i18nLabel;
        }
        return null;
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
    public List<String> getPages() {
        return pages;
    }

    /**
     * Set the list of valid Page paths.  If any of these page paths match the
     * current request then the Menu item will be selected.
     *
     * @param pages the list of valid Page paths
     */
    public void setPages(List<String> pages) {
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
     * Return true if the menu has roles defined, false otherwise.
     *
     * @return true if the menu has roles defined, false otherwise
     */
    public boolean hasRoles() {
        return roles != null && !roles.isEmpty();
    }

    /**
     * Return the list of roles for the Menu item.
     *
     * @return the list of roles for the Menu item
     */
    public List<String> getRoles() {
        if (roles == null) {
            roles = new ArrayList<String>();
        }
        return roles;
    }

    /**
     * Set the list of valid roles for the Menu item.
     *
     * @param roles the list of valid roles for the Menu item
     */
    public void setRoles(List<String> roles) {
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
            String localPath = getPath();
            if (localPath != null) {
                localPath = localPath.startsWith("/") ? localPath : "/" + localPath;
                selected = localPath.equals(pageToView);
            } else {
                selected = false;
            }
        }

        for (int i = 0, size = getChildren().size(); i < size; i++) {
            Menu menu = getChildren().get(i);
            if (menu.isSelected()) {
                selected = true;
            }
        }

        return selected;
    }

    /**
     * Return the selected child menu, or null if no child menu is selected.
     *
     * @return the selected child menu
     */
    public Menu getSelectedChild() {
        if (isSelected()) {
            for (int i = 0, size = getChildren().size(); i < size; i++) {
                Menu menu = getChildren().get(i);
                if (menu.isSelected()) {
                    return menu;
                }
            }
        }
        return null;
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
     * This method internally uses the
     * {@link org.apache.click.extras.security.AccessController#hasAccess(javax.servlet.http.HttpServletRequest, java.lang.String) AccessController#hasAccess(HttpServletRequest request, String roleName)}
     * method where the rolenames are derived from the {@link #getRoles()} property.
     * <p/>
     * If no {@link #getRoles() roles} are defined, the AccessController are invoked
     * with a <tt>null</tt> argument to determine whether access is permitted to
     * menus without roles.
     *
     * @return true if the user is in one of the menu roles, or false otherwise
     * @throws IllegalStateException if the menu accessController is not defined
     */
    public boolean isUserInRoles() {
        if (getAccessController() == null) {
            String msg = "Menu accessController has not been defined";
            throw new IllegalStateException(msg);
        }

        HttpServletRequest request = getContext().getRequest();

        if (hasRoles()) {
            for (int i = 0, size = getRoles().size(); i < size; i++) {
                String rolename = getRoles().get(i);
                if (getAccessController().hasAccess(request, rolename)) {
                    return true;
                }
            }
        } else {
            // Check access for menus without roles. CLK-724
            return getAccessController().hasAccess(request, null);
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
         for (int i = 0, size = getChildren().size(); i < size; i++) {
            Menu child = getChildren().get(i);
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
     * Return the 'title' attribute of the Menu item, or null if not defined.
     * <p/>
     * If the title value is null, this method will attempt to find a
     * localized title message in the parent messages of the root menu using the
     * key:
     *
     * <blockquote>
     * <tt>getName() + ".title"</tt>
     * </blockquote>
     *
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key. If still
     * not found the title will be left as null and will not be rendered.
     * <p/>
     * For example given the properties file <tt>src/click-page.properties</tt>:
     *
     * <pre class="codeConfig">
     * <span class="st">customers</span>.label=<span class="red">Customers</span>
     * <span class="st">customers</span>.title=<span class="red">Find a specific customer</span> </pre>
     *
     * The menu.xml (<b>note</b> that no title attribute is present):
     * <pre class="prettyprint">
     * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
     * &lt;menu&gt;
     *    &lt;menu name="customers" path="customers.htm" roles="view-customers"/&gt;
     *
     *    ...
     * &lt;/menu&gt; </pre>
     *
     * Will render the Menu label and title properties as:
     *
     * <pre class="codeHtml">
     * &lt;li&gt;&lt;a title="<span class="red">Find a specific customer</span>" ... &gt;<span class="red">Customers</span>&lt;/a&gt;&lt;/li&gt; </pre>
     *
     * @return the 'title' attribute of the Menu item
     */
    public String getTitle() {
        // Return cached title if set
        if (title != null) {
            return title;
        }

        String localName = getName();

        if (localName != null) {
            // Use root menu messages to lookup the title
            Menu root = findRootMenu();

            // NOTE: don't cache the i18nTitle, since menus are often cached
            // statically
            return root.getMessage(localName + ".title");
        }

        return null;
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
        String localPath = getPath();
        if (isExternal()) {
            return localPath;
        }

        if ("#".equals(localPath)) {
            return getContext().getResponse().encodeURL(localPath);

        } else {
            Context context = getContext();
            if (localPath == null) {
                // Guard against rendering "null" in the href
                localPath = "";
            }
            StringBuilder sb = new StringBuilder();
            String contextPath = context.getRequest().getContextPath();
            sb.append(contextPath);
            if (localPath.length() > 0 && localPath.charAt(0) != '/') {
                sb.append('/');
            }
            sb.append(localPath);
            return context.getResponse().encodeURL(sb.toString());
        }
    }

    /**
     * Return the Menu HEAD elements to be included in the page.
     * The following resources are returned:
     *
     * <ul>
     * <li><tt>click/menu.css</tt></li>
     * <li><tt>click/control.js</tt></li>
     * <li><tt>click/menu-fix-ie6.js</tt> (fixes IE6 menu burnthrough and hover issues)</li>
     * </ul>
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<org.apache.click.element.Element> getHeadElements() {
        String id = getId();
        if (id == null) {
            throw new IllegalStateException("Menu name is not set");
        }

        if (headElements == null) {
            headElements = super.getHeadElements();

            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            CssImport cssImport = new CssImport("/click/menu.css", versionIndicator);
            headElements.add(cssImport);

            JsImport jsImport = new JsImport("/click/control.js", versionIndicator);
            headElements.add(jsImport);

            jsImport = new JsImport("/click/menu-fix-ie6.js", versionIndicator);
            jsImport.setConditionalComment(JsImport.IF_LESS_THAN_IE7);
            headElements.add(jsImport);

            JsScript script = new JsScript();
            script.setId(id + "-js-setup");

            // Script must be executed as soon as browser dom is ready
            script.setExecuteOnDomReady(true);
            script.setConditionalComment(JsImport.IF_LESS_THAN_IE7);

            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append(" if(typeof Click != 'undefined' && typeof Click.menu != 'undefined') {\n");
            buffer.append("   if(typeof Click.menu.fixHiddenMenu != 'undefined') {\n");
            buffer.append("     Click.menu.fixHiddenMenu(\"").append(id).append("\");\n");
            buffer.append("     Click.menu.fixHover(\"").append(id).append("\");\n");
            buffer.append("   }\n");
            buffer.append(" }\n");
            script.setContent(buffer.toString());
            headElements.add(script);
        }

        return headElements;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Add the given menu as a submenu. The menu will also be set as the parent
     * of the submenu.
     *
     * @param menu the submenu to add
     * @return the menu that was added
     */
    public Menu add(Menu menu) {
        getChildren().add(menu);
        menu.setParent(this);
        return menu;
    }

    /**
     * Return true if this menu contains the given menu, false otherwise.
     * <p/>
     * To test if the given menu is contained, this method will test against
     * both the menu object reference as well as the menu name.
     *
     * @return true if this menu contains the given menu, false otherwise
     */
    public boolean contains(Menu menu) {
        if (hasChildren()) {
            for (Menu child : getChildren()) {

                // Test against object reference
                if (child == menu) {
                    return true;
                }

                // Test against menu name
                String childName = child.getName();
                String menuName = menu.getName();
                if (childName != null && menuName != null) {
                    if (childName.equals(menuName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Find the root menu, or null if no root menu can be found.
     *
     * @return the root menu, or null if no root menu can be found.
     */
    public Menu findRootMenu() {
        Menu root = this;
        Object parentMenu = root.getParent();
        while (parentMenu instanceof Menu) {
            root = (Menu) parentMenu;
            parentMenu = root.getParent();
        }
        return root;
    }

    /**
     * Return true if this is the root menu, false otherwise.
     *
     * @return true if this menu is the root menu, false otherwise
     */
    public boolean isRoot() {
        return !(getParent() instanceof Menu);
    }

    /**
     * This sets the parent to be null.
     *
     * @see org.apache.click.Control#onDestroy()
     */
    @Override
    public void onDestroy() {
        setParent(null);
    }

    /**
     * Render an HTML representation of the Menu.
     * <p/>
     * If <tt>this</tt> menu instance is the root menu
     * ({@link #isRoot()} returns true), the menu and all its submenus
     * (recursively), will be rendered by delegating rendering to the method
     * {@link #renderRootMenu(org.apache.click.util.HtmlStringBuffer) renderRootMenu}.
     * The menu structure will be rendered as an HTML List consisting of &lt;ul&gt;
     * and &lt;li&gt; elements.
     * <p/>
     * If <tt>this</tt> menu instance is <tt>not</tt> the root menu, this menu
     * will be rendered by delegating rendering to the method
     * {@link #renderMenuLink(org.apache.click.util.HtmlStringBuffer, org.apache.click.extras.control.Menu)}.
     * The menu will be rendered as a link: &lt;a&gt;.
     * <p/>
     * By having two render modes one can render the entire menu
     * automatically, or render each menu item manually using a Velocity macro.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        if (isRoot()) {

            renderRootMenu(buffer);
        } else {

            if (isSeparator()) {
                renderSeparator(buffer, this);
            } else {
                renderMenuLink(buffer, this);
            }
        }
    }

    /**
     * Return an HTML representation of the menu.
     *
     * @see #render(org.apache.click.util.HtmlStringBuffer)
     *
     * @return an HTML anchor tag representation of the menu
     */
    @Override
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        render(buffer);
        return buffer.toString();
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Render an HTML representation of the root menu.
     *
     * @param buffer the buffer to render to
     */
    protected void renderRootMenu(HtmlStringBuffer buffer) {
        buffer.elementStart("div");
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("class", "menustyle");
        buffer.closeTag();
        buffer.append("\n");

        int depth = 0;
        renderMenuList(buffer, this, depth);
        buffer.elementEnd("div");
    }

    /**
     * Render an html representation of the menu list (&lt;ul&gt;) structure.
     * <p/>
     * <b>Please note</b>: the method
     * {@link #canRender(org.apache.click.extras.control.Menu, int) canRender(menu)}
     * controls whether menu items are rendered or not. If <tt>canRender</tt>
     * returns true, the menu item is rendered, otherwise it is skipped.
     *
     * @see #canRender(org.apache.click.extras.control.Menu, int)
     *
     * @param buffer the buffer to render to
     * @param menu the menu that is currently rendered
     * @param depth the current depth in the menu hierarchy
     */
    protected void renderMenuList(HtmlStringBuffer buffer, Menu menu, int depth) {
        buffer.elementStart("ul");
        renderMenuListAttributes(buffer, menu, depth);
        buffer.closeTag();
        buffer.append("\n");

        for (Menu child : menu.getChildren()) {

            if (canRender(child, depth)) {

                buffer.elementStart("li");
                renderMenuListItemAttributes(buffer, child, depth);
                buffer.closeTag();

                if (child.isSeparator()) {
                    renderSeparator(buffer, child);
                } else {
                    renderMenuLink(buffer, child);
                }

                if (child.hasChildren()) {
                    buffer.append("\n");
                    renderMenuList(buffer, child, depth + 1);
                }
                buffer.elementEnd("li");
                buffer.append("\n");
            }
        }

        buffer.elementEnd("ul");
        buffer.append("\n");
    }

    /**
     * Return true if the given menu can be rendered, false otherwise.
     * <p/>
     * If the menu {@link #hasRoles() has roles} defined, this method will return
     * true if the user is in one of the menu roles, false otherwise. This method
     * delegates to {@link #isUserInRoles()} if the menu has roles defined.
     * <p/>
     * If the menu has no roles defined, this method returns true.
     *
     * @param menu the menu that should be rendered or not
     * @param depth the current depth in the menu hierarchy
     * @return true if the menu can be rendered, false otherwise
     */
    protected boolean canRender(Menu menu, int depth) {
        // TODO add and check visible property
        return menu.isUserInRoles();
    }

    /**
     * Render the attributes of the menu list (&gt;ul&lt;).
     *
     * @param buffer the buffer to render to
     * @param menu the menu being rendered
     * @param depth the current depth in the menu hierarchy
     */
    protected void renderMenuListAttributes(HtmlStringBuffer buffer, Menu menu,
        int depth) {

        if (depth == 0) {
            buffer.appendAttribute("class", "menubar");
        } else {
            buffer.appendAttribute("class", "submenu");
        }
    }

    /**
     * Render the attributes of the menu list item (&gt;li&lt;).
     *
     * @param buffer the buffer to render to
     * @param menu the menu being rendered
     * @param depth the current depth in the menu hierarchy
     */
    protected void renderMenuListItemAttributes(HtmlStringBuffer buffer, Menu menu,
        int depth) {

        if (depth == 0) {
            buffer.append(" class=\"menuitem topitem");
        } else {
            buffer.append(" class=\"menuitem");
        }
        if (menu.hasChildren()) {
            buffer.append(" has-submenu");
        }
        buffer.append("\"");
    }

    /**
     * Render an HTML link (&lt;a&gt;) representation of the given menu.
     * <p/>
     * If the menu item is selected the anchor tag will be rendered with
     * class="selected" attribute.
     *
     * @param buffer the buffer to render to
     * @param menu the menu to render
     */
    protected void renderMenuLink(HtmlStringBuffer buffer, Menu menu) {
        buffer.elementStart("a");

        String id = menu.getAttribute("id");
        if (id != null) {
            buffer.appendAttribute("id", id);
        }

        if (menu.getName() != null) {
            buffer.appendAttribute("name", menu.getName());
        }

        menu.renderMenuHref(buffer);

        if (menu.getTarget() != null && menu.getTarget().length() > 0) {
            buffer.appendAttribute("target", menu.getTarget());
        }

        String menuTitle = menu.getTitle();
        if (menuTitle != null && menuTitle.length() > 0) {
            buffer.appendAttributeEscaped("title", menuTitle);
        }

        if (menu.isSelected()) {
            buffer.appendAttribute("class", "selected");
        }

        // TODO need to re-add visible and enabled properties
        if (menu.hasAttributes()) {
            buffer.appendAttributes(menu.getAttributes());
        }

        buffer.closeTag();

        String menuLabel = menu.getLabel();

        if (StringUtils.isNotBlank(menu.getImageSrc())) {
            buffer.elementStart("img");
            buffer.appendAttribute("border", "0");
            buffer.appendAttribute("class", "link");

            if (menuTitle != null) {
                buffer.appendAttributeEscaped("alt", menuTitle);
            } else {
                buffer.appendAttributeEscaped("alt", menuLabel);
            }

            String src = menu.getImageSrc();
            if (StringUtils.isNotBlank(src)) {
                if (src.charAt(0) == '/') {
                    src = getContext().getRequest().getContextPath() + src;
                }
                buffer.appendAttribute("src", src);
            }

            buffer.elementEnd();

            if (menuLabel != null) {
                buffer.append(menuLabel);
            }

        } else {
            if (menuLabel != null) {
                buffer.append(menuLabel);
            }
        }

        buffer.elementEnd("a");
    }

    /**
     * Render an HTML representation of the menu as a separator.
     *
     * @param buffer the buffer to render to
     * @param menu the menu to render as a separator
     */
    protected void renderSeparator(HtmlStringBuffer buffer, Menu menu) {
        buffer.append("<hr/>");
    }

    /**
     * Render the menu <tt>"href"</tt> attribute. This method can be overridden
     * to render dynamic <tt>"href"</tt> parameters, for example:
     *
     * <pre class="prettyprint">
     * public class MyPage extends BorderPage {
     *
     *     public MyPage() {
     *         Menu rootMenu = new MenuFactory().getRootMenu();
     *
     *         final String contextPath = getContext().getRequest().getContextPath();
     *
     *         Menu menu = new Menu() {
     *             &#64;Override
     *             protected void renderMenuHref(HtmlStringBuffer buffer) {
     *                 buffer.appendAttribute("href", contextPath + "/my-page.htm?customer=" + getCustomerId());
     *             }
     *         });
     *
     *         menu.setName("customer");
     *         menu.setLabel("Customer Lookup");
     *
     *         // Guard against adding child menu more than once
     *         if (!rootMenu.contains(menu)) {
     *             rootMenu.add(menu);
     *         }
     *     }
     * } </pre>
     *
     * @param buffer the buffer to render the href attribute to
     */
    protected void renderMenuHref(HtmlStringBuffer buffer) {
        String href = getHref();
        buffer.appendAttribute("href", href);

        if ("#".equals(href)) {
            // If hyperlink does not return false, clicking on it will
            // scroll to the top of the page.
            buffer.appendAttribute("onclick", "return false;");
        }
    }

    /**
     * Return a copy of the Applications root Menu as defined in the
     * configuration file "<tt>/WEB-INF/menu.xml</tt>", with the Control
     * name <tt>"rootMenu"</tt>.
     * <p/>
     * The returned root menu is always selected.
     *
     * @deprecated use
     * {@link MenuFactory#loadFromMenuXml(java.lang.String, java.lang.String, org.apache.click.extras.security.AccessController, java.lang.Class)}
     * instead
     *
     * @param accessController the menu access controller
     * @return a copy of the application's root Menu
     */
    @Deprecated
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
            if (inputStream == null) {
                String msg =
                    "could not find configuration file:" + DEFAULT_CONFIG_FILE
                    + " or menu.xml on classpath";
                throw new RuntimeException(msg);
            }
        }

        Document document = ClickUtils.buildDocument(inputStream);

        Element rootElm = document.getDocumentElement();

        NodeList list = rootElm.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Menu childMenu = new Menu((Element) node, accessController);
                menu.add(childMenu);
            }
        }

        return menu;
    }

}
