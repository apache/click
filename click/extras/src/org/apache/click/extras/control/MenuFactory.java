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
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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
 * TODO documentation.
 */
public class MenuFactory {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    /**
     * The menu configuration filename: &nbsp; "<tt>/WEB-INF/menu.xml</tt>".
     */
    protected static final String DEFAULT_CONFIG_FILE = "/WEB-INF/menu.xml";

    // Class Variables --------------------------------------------------------

    /** The cached root Menu as defined in <tt>menu.xml</tt>. */
    protected static Menu CACHED_ROOT_MENU;

    /** The default Menu XML attributes loaded into menu properties. */
    protected static Set<String> DEFAULT_ATTRIBUTES;

    static {
        DEFAULT_ATTRIBUTES = new HashSet<String>();
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
    * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, creating menu items using the Menu class and the JEE
     * RollAccessController.
     *
     * @see RoleAccessController
     *
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    public Menu getRootMenu() {
        return getRootMenu(Menu.class, new RoleAccessController());
    }

    /**
    * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, creating menu items using the provided Menu class and the JEE
     * RollAccessController.
     *
     * @param menuClass the menu class to create new Menu instances from
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    public Menu getRootMenu(Class<? extends Menu> menuClass) {
        return getRootMenu(new RoleAccessController());
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
        return getRootMenu(Menu.class, accessController);
    }

    /**
    * Return root menu item defined in the WEB-INF/menu.xml or classpath
     * menu.xml, creating menu items using the provided Menu class and the
     * AccessController.
     *
     * @param menuClass the menu class to create new Menu instances from
     * @param accessController the menu access controller
     * @return the root menu item defined in the WEB-INF/menu.xml file or menu.xml
     * in the root classpath
     */
    public Menu getRootMenu(Class<? extends Menu> menuClass, AccessController accessController) {

        Validate.notNull(menuClass, "Null menuClass parameter");
        Validate.notNull(accessController, "Null accessController parameter");

        // If menu is cached return it
        if (CACHED_ROOT_MENU != null) {
            return CACHED_ROOT_MENU;
        }

        try {
            Menu loadedMenu = loadFromMenuXml(menuClass, accessController);

            ServletContext servletContext = Context.getThreadLocalContext().getServletContext();
            ConfigService configService = ClickUtils.getConfigService(servletContext);

            if (configService.isProductionMode() || configService.isProfileMode()) {
                // Cache menu in production modes
                CACHED_ROOT_MENU = loadedMenu;
            }

            return loadedMenu;

        } catch (Exception e) {
            String msg = "Error initializing rootMenu for class: " + menuClass;
            throw new RuntimeException(msg, e);
        }
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Return a copy of the Applications root Menu as defined in the
     * configuration file "<tt>/WEB-INF/menu.xml</tt>", with the Control
     * name <tt>"rootMenu"</tt>.
     * <p/>
     * The returned root menu is always selected.
     *
     * @param menuClass the menu class to instantiate
     * @param accessController the menu access controller
     * @return a copy of the application's root Menu
     * @throws InstantiationException if the menu instance could not be created
     * @throws IllegalAccessException if the menu instance could not be created
     */
    protected Menu loadFromMenuXml(Class<? extends Menu> menuClass,
            AccessController accessController)
            throws InstantiationException, IllegalAccessException {

        Validate.notNull(menuClass, "Null menuClass parameter");
        Validate.notNull(accessController, "Null accessController parameter");

        Context context = Context.getThreadLocalContext();

        Menu rootMenu = menuClass.newInstance();
        rootMenu.setName("rootMenu");
        rootMenu.setAccessController(accessController);

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
                Menu childMenu = buildMenu((Element) node, menuClass, accessController);
                rootMenu.getChildren().add(childMenu);
            }
        }

        return rootMenu;
    }

    /**
     * Build a new Menu from the given menu item XML Element and recurse through
     * all the menu-items children.
     *
     * @param menuElement the menu item XML Element
     * @param menuClass the menu class to instantiate
     * @param accessController the menu access controller
     * @return new Menu instance for the given XML menuElement
     * @throws InstantiationException if the menu instance could not be created
     * @throws IllegalAccessException if the menu instance could not be created
     */
    protected Menu buildMenu(Element menuElement, Class<? extends Menu> menuClass,
            AccessController accessController)
            throws InstantiationException, IllegalAccessException {

        Validate.notNull(menuElement, "Null menuElement parameter");
        Validate.notNull(menuClass, "Null menuClass parameter");
        Validate.notNull(accessController, "Null accessController parameter");

        Menu menu = menuClass.newInstance();

        menu.setName(menuElement.getAttribute("name"));

        menu.setAccessController(accessController);

        menu.setLabel(menuElement.getAttribute("label"));

        menu.setImageSrc(menuElement.getAttribute("imageSrc"));

        menu.setPath(menuElement.getAttribute("path"));

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

        String pagesValue = menuElement.getAttribute("pages");
        if (!StringUtils.isBlank(pagesValue)) {
            StringTokenizer tokenizer = new StringTokenizer(pagesValue, ",");
            while (tokenizer.hasMoreTokens()) {
                String path = tokenizer.nextToken().trim();
                path = (path.startsWith("/")) ? path : "/" + path;
                menu.getPages().add(path);
            }
        }

        String rolesValue = menuElement.getAttribute("roles");
        if (!StringUtils.isBlank(rolesValue)) {
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
                Menu childMenu = buildMenu((Element) node, menuClass, accessController);
                menu.getChildren().add(childMenu);
            }
        }

        return menu;
    }

}
