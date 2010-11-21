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
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.apache.click.Context;
import org.apache.click.extras.security.AccessController;
import org.apache.click.extras.security.RoleAccessController;
import org.apache.click.service.ConfigService;
import org.apache.click.util.ClickUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a Menu factory for creating application menus from configuration
 * files.
 * <p/>
 * Menu factory provides a variety of <tt>getRootMenu()</tt> methods for
 * loading the menus. The default {@link #getRootMenu()} method creates menus
 * from the configuration file <tt>/WEB-INF/menu.xml</tt>, or the classpath
 * resource <tt>/menu.xml</tt> if <tt>WEB-INF/menu.xml</tt> was not resolved.
 * <p/>
 * Below is an example <tt>menu.xml</tt> configuration file:
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
 * You can also specify an alternative configuration file name to load your
 * menus from. Just use one of the <tt>getRootMenu</tt> methods that accept a
 * configuration file name, for example {@link #getRootMenu(java.lang.String, java.lang.String)
 * getRootMenu(name, fileName)}.
 *
 * <h3><a name="examples"></a>MenuFactory Examples</h3>
 *
 * Below is an example of a MenuFactory being used to set the rootMenu on a
 * border page. Typically a border page will define a page template which
 * contain the surrounding page chrome including the header and the application
 * menu. Application page classes will subclass the BorderPage an inherit
 * the application rootMenu.
 *
 * <pre class="prettyprint">
 * public abstract class BorderPage extends Page {
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
 * <h3><a name="stateful-pages"></a>Stateful pages</h3>
 * Please note if you use stateful pages that are serialized, you probably
 * won't want your application menu being serialized to disk or across a cluster
 * with your page as well. In these scenarios please follow the pattern below.
 *
 * <pre class="prettyprint">
 * public abstract class BorderPage extends Page {
 *
 *     // Note the transient keyword
 *     private transient Menu rootMenu;
 *
 *     &#64;Override
 *     public void onInit() {
 *         super.onInit();
 *
 *         MenuFactory menuFactory = new MenuFactory();
 *         rootMenu = menuFactory.getRootMenu();
 *         addControl(rootMenu);
 *     }
 *
 *     &#64;Override
 *     public void onDestroy() {
 *         if (rootMenu != null) {
 *             removeControl(rootMenu);
 *         }
 *
 *         super.onDestroy();
 *     }
 *
 * } </pre>
 *
 * <h3><a name="caching"></a>Caching</h3>
 * Loading Menus using {@link #getRootMenu()} will automatically cache the
 * menus for improved performance (technically the menus are only cached when
 * Click is in <tt>production</tt> or <tt>profile</tt> mode).
 * <p/>
 * If you want to manage Menu caching yourself, use one of the
 * {@link #getRootMenu(boolean) getRootMenu} methods that accepts a boolean
 * controlling whether or not the menus are cached.
 * <p/>
 * A common use case for caching menus yourself is when you need to customize
 * the menus based on the logged in user. For this scenario you would load the
 * Menus using {@link #getRootMenu(boolean) getRootMenu(false)}, customize the
 * menus according to the user profile, and cache the menus in the HttpSession.
 *
 * @see Menu
 */
public class MenuFactory implements Serializable {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    /**
     * The default root menu name: &nbsp; "<tt>rootMenu</tt>".
     */
    public final static String DEFAULT_ROOT_MENU_NAME = "rootMenu";

    /**
     * The menu configuration filename: &nbsp; "<tt>menu.xml</tt>".
     */
    protected static final String DEFAULT_CONFIG_FILE = "menu.xml";

    // Class Variables --------------------------------------------------------

    /** The default Menu XML attributes loaded into menu properties. */
    protected final static Set<String> DEFAULT_ATTRIBUTES = new HashSet<String>();

    /** The menu cache. */
    protected static final Map<String, Menu> MENU_CACHE = new ConcurrentHashMap<String, Menu>();

    static {
        DEFAULT_ATTRIBUTES.add("name");
        DEFAULT_ATTRIBUTES.add("label");
        DEFAULT_ATTRIBUTES.add("path");
        DEFAULT_ATTRIBUTES.add("target");
        DEFAULT_ATTRIBUTES.add("title");
        DEFAULT_ATTRIBUTES.add("imageSrc");
        DEFAULT_ATTRIBUTES.add("external");
        DEFAULT_ATTRIBUTES.add("separator");
        DEFAULT_ATTRIBUTES.add("roles");
        DEFAULT_ATTRIBUTES.add("pages");
    }

    // Public Methods ---------------------------------------------------------

    /**
    * Return cached root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, creating menu items using the Menu class and the JEE
     * RoleAccessController.
     *
     * @see RoleAccessController
     *
     * @return the cached root menu item defined in the WEB-INF/menu.xml file
     * or menu.xml in the root classpath
     */
    public Menu getRootMenu() {
        return getRootMenu(DEFAULT_ROOT_MENU_NAME, DEFAULT_CONFIG_FILE);
    }

    /**
     * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, creating menu items using the provided menu class and the JEE
     * RoleAccessController.
     *
     * @param menuClass the menu class to create new Menu instances from
     * @return the cached root menu item defined in the WEB-INF/menu.xml file
     * or menu.xml in the root classpath
     */
    public Menu getRootMenu(Class<? extends Menu> menuClass) {
        return getRootMenu(DEFAULT_ROOT_MENU_NAME, DEFAULT_CONFIG_FILE,
            new RoleAccessController(), true, menuClass);
    }

    /**
     * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, creating menu items using the Menu class and the provided
     * AccessController.
     *
     * @param accessController the menu access controller
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    public Menu getRootMenu(AccessController accessController) {
        return getRootMenu(DEFAULT_ROOT_MENU_NAME, DEFAULT_CONFIG_FILE,
            accessController, true, null);
    }

    /**
     * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, creating menu items using the Menu class and the JEE
     * RoleAccessController. The cached option specifies whether the loaded
     * menus will be cached or not.
     *
     * @param cached return the cached menu if in production or profile mode,
     * otherwise create and return a new root menu instance
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    public Menu getRootMenu(boolean cached) {
        return getRootMenu(DEFAULT_ROOT_MENU_NAME, DEFAULT_CONFIG_FILE,
            new RoleAccessController(), cached, null);
    }

    /**
     * Return root menu item defined by the given name and fileName under
     * WEB-INF or the classpath, creating menu items using the Menu class and
     * the JEE RoleAccessController.
     *
     * @param name the name of the root menu
     * @param fileName the fileName defining the menu definitions
     * @return the root menu item defined by the fileName under WEB-INF or the
     * classpath
     */
    public Menu getRootMenu(String name, String fileName) {
        return getRootMenu(name, fileName, new RoleAccessController(),
            true, null);
    }

    /**
     * Return root menu item defined by the given name and fileName under WEB-INF
     * or the classpath, creating menu items using the provided menu class and
     * AccessController. The cached option specifies whether the loaded
     * menus will be cached or not.
     * <p/>
     * Example usage:
     * <pre class="prettyprint">
     * public void onInit() {
     *     MenuFactory factory = new MenuFactory();
     *     String menuName = "mymenu";
     *     String fileName = "mymenu.xml";
     *     AccessController accessController = new RoleAccessController();
     *     boolean cached = true;
     *
     *     factory.getRootMenu(menuName, fileName, accessController, cached, MyMenu.class);
     * } </pre>
     *
     * @param name the name of the root menu
     * @param fileName the fileName defining the menu definitions
     * @param accessController the menu access controller
     * @param cached return the cached menu if in production or profile mode,
     * otherwise create and return a new root menu instance
     * @param menuClass the menu class to create new Menu instances from
     * @return the root menu item defined by the fileName under WEB-INF or the
     * classpath
     */
    public Menu getRootMenu(String name, String fileName,
        AccessController accessController, boolean cached,
        Class<? extends Menu> menuClass) {

        Validate.notNull(name, "Null name parameter");
        Validate.notNull(fileName, "Null fileName parameter");
        Validate.notNull(accessController, "Null accessController parameter");

        if (cached) {

            Menu cachedMenu = retrieveRootMenu(name);
            if (cachedMenu != null) {
                return cachedMenu;
            }
        }

        Menu rootMenu = loadFromMenuXml(name, fileName, accessController, menuClass);

        // Retrieve headElements to guard against race conditions when initializing
        // menus from multiple threads. CLK-713
        rootMenu.getHeadElements();

        ServletContext servletContext = Context.getThreadLocalContext().getServletContext();
        ConfigService configService = ClickUtils.getConfigService(servletContext);

        if (cached) {

            if (configService.isProductionMode() || configService.isProfileMode()) {
                // Cache menu in production modes
                cacheRootMenu(rootMenu);
            }
        }

        return rootMenu;
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Build a new Menu from the given menu item XML Element and recurse through
     * all the menu-items children. If the menuClass is specified, menus will
     * be created of that type, otherwise an instance of {@link Menu} will be
     * created.
     *
     * @param menuElement the menu item XML Element
     * @param accessController the menu access controller
     * @param menuClass the menu class to instantiate
     * @return new Menu instance for the given XML menuElement
     */
    protected Menu buildMenu(Element menuElement, AccessController accessController,
        Class<? extends Menu> menuClass) {

        Validate.notNull(menuElement, "Null menuElement parameter");
        Validate.notNull(accessController, "Null accessController parameter");

        Menu menu = null;
        if (menuClass == null) {
            menu = new Menu();
        } else {
            menu = createMenu(menuClass);
        }

        menu.setAccessController(accessController);

        String nameAtr = menuElement.getAttribute("name");
        if (StringUtils.isNotBlank(nameAtr)) {
            menu.setName(nameAtr);
        }

        String labelAtr = menuElement.getAttribute("label");
        if (StringUtils.isNotBlank(labelAtr)) {
            menu.setLabel(labelAtr);
        }

        String imageSrcAtr = menuElement.getAttribute("imageSrc");
        if (StringUtils.isNotBlank(imageSrcAtr)) {
            menu.setImageSrc(imageSrcAtr);
        }

        String pathAtr = menuElement.getAttribute("path");
        if (StringUtils.isNotBlank(pathAtr)) {
            menu.setPath(pathAtr);
        }

        String titleAtr = menuElement.getAttribute("title");
        if (StringUtils.isNotBlank(titleAtr)) {
            menu.setTitle(titleAtr);
        }

        String targetAtr = menuElement.getAttribute("target");
        if (StringUtils.isNotBlank(targetAtr)) {
            menu.setTarget(targetAtr);
        }

        String externalAtr = menuElement.getAttribute("external");
        if ("true".equalsIgnoreCase(externalAtr)) {
            menu.setExternal(true);
        }

        String separatorAtr = menuElement.getAttribute("separator");
        if ("true".equalsIgnoreCase(separatorAtr)) {
            menu.setSeparator(true);
        }

        /*
        String visibilityAtr = menuElement.getAttribute("visible");
        if ("false".equalsIgnoreCase(visibilityAtr)) {
            menu.setVisible(false);
        }

        String enablingAtr = menuElement.getAttribute("enabled");
        if ("false".equalsIgnoreCase(enablingAtr)) {
            menu.setEnabled(false);
        }*/

        String pagesValue = menuElement.getAttribute("pages");
        if (StringUtils.isNotBlank(pagesValue)) {
            StringTokenizer tokenizer = new StringTokenizer(pagesValue, ",");
            while (tokenizer.hasMoreTokens()) {
                String path = tokenizer.nextToken().trim();
                path = (path.startsWith("/")) ? path : "/" + path;
                menu.getPages().add(path);
            }
        }

        String rolesValue = menuElement.getAttribute("roles");
        if (StringUtils.isNotBlank(rolesValue)) {
            StringTokenizer tokenizer = new StringTokenizer(rolesValue, ",");
            while (tokenizer.hasMoreTokens()) {
                menu.getRoles().add(tokenizer.nextToken().trim());
            }
        }

        // Load other attributes
        NamedNodeMap attributeNodeMap = menuElement.getAttributes();
        for (int i = 0; i < attributeNodeMap.getLength(); i++) {
            Node attribute = attributeNodeMap.item(i);
            String name = attribute.getNodeName();
            if (!DEFAULT_ATTRIBUTES.contains(name)) {
                String value = attribute.getNodeValue();
                menu.getAttributes().put(name, value);
            }
        }

        NodeList childElements = menuElement.getChildNodes();
        for (int i = 0, size = childElements.getLength(); i < size; i++) {
            Node node = childElements.item(i);
            if (node instanceof Element) {
                Menu childMenu = buildMenu((Element) node, accessController, menuClass);
                menu.add(childMenu);
            }
        }
        return menu;
    }

    /**
     * Create a new menu instance of the given menu class.
     *
     * @param menuClass the menu class to instantiate
     * @return a new menu instance of the given menu class
     */
    protected Menu createMenu(Class<? extends Menu> menuClass) {
        try {
            return menuClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred create new menu of type "
                + menuClass);
        }
    }

    /**
     * Return a copy of the Applications root Menu as defined by the
     * configuration file.
     * <p/>
     * If the fileName starts with a '/' character it is assumed to be an
     * absolute path and Click will attempt to load the file from the Servlet
     * context path and if not found from the classpath.
     * <p/>
     * If the fileName does not start with a '/' character it is assumed to be
     * a relative path and Click will load the file from the Servlet context
     * by <tt>prefixing</tt> the fileName with '/WEB-INF'. If not found the
     * file will be loaded from the classpath.
     * <p/>
     * The returned root menu is always selected.
     *
     * @param name the name of the root menu
     * @param fileName the configuration fileName defining the menu definitions
     * @param accessController the menu access controller
     * @param menuClass the menu class to instantiate
     * @return a copy of the application's root Menu
     */
    protected Menu loadFromMenuXml(String name, String fileName,
        AccessController accessController, Class<? extends Menu> menuClass) {

        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (fileName == null) {
            throw new IllegalArgumentException("Null fileName parameter");
        }

        if (accessController == null) {
            throw new IllegalArgumentException("Null accessController parameter");
        }

        String webinfFileName = null;
        boolean absolute = fileName.startsWith("/");
        if (!absolute) {
            fileName = '/' + fileName;
            webinfFileName = "/WEB-INF" + fileName;
        }

        Context context = Context.getThreadLocalContext();

        Menu menu = null;
        if (menuClass == null) {
            menu = new Menu();
        } else {
            menu = createMenu(menuClass);
        }

        menu.setName(name);
        menu.setAccessController(accessController);

        ServletContext servletContext = context.getServletContext();
        InputStream inputStream = null;

        if (absolute) {
            inputStream =
                servletContext.getResourceAsStream(fileName);
        } else {
            inputStream =
                servletContext.getResourceAsStream(webinfFileName);
        }

        if (inputStream == null) {
            if (absolute) {
                inputStream = ClickUtils.getResourceAsStream(fileName, MenuFactory.class);
                if (inputStream == null) {
                    String msg =
                        "could not find configuration file:" + fileName
                        + " on classpath";
                    throw new RuntimeException(msg);
                }
            } else {
                inputStream = ClickUtils.getResourceAsStream(fileName, MenuFactory.class);
                if (inputStream == null) {
                    String msg =
                        "could not find configuration file:" + webinfFileName
                        + " or " + fileName + " on classpath";
                    throw new RuntimeException(msg);
                }
            }
        }

        Document document = ClickUtils.buildDocument(inputStream);

        Element rootElm = document.getDocumentElement();

        NodeList list = rootElm.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Menu childMenu = buildMenu((Element) node, accessController, menuClass);
                menu.add(childMenu);
            }
        }

        return menu;
    }

    /**
     * Return the map containing menus cached by name.
     *
     * @return the map containing menus cached by name
     */
    protected Map<String, Menu> getMenuCache() {
        return MENU_CACHE;
    }

    /**
     * Return the cached root menu from the
     * {@link #getMenuCache() menu cache}.
     *
     * @param name the name of the root menu to retrieve
     * @return the cache root menu from the menu cache
     */
    protected Menu retrieveRootMenu(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        return getMenuCache().get(name);
    }

    /**
     * Cache the given menu in the {@link #getMenuCache() menu cache}.
     *
     * @param menu the menu to store in the cache
     */
    protected void cacheRootMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Null menu parameter");
        }

        if (menu.getName() == null) {
            throw new IllegalArgumentException("Menu name cannot be null");
        }

        getMenuCache().put(menu.getName(), menu);
    }
}
