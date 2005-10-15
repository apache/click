/*
 * Copyright 2004-2005 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.extras.menu;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.click.Context;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides hierarchical Menu component. Application menus can be defined
 * using a <tt>/WEB-INF/menu.xml</tt> configuration file.
 *
 * <pre class="codeJava">
 * <span class="kw">public void</span> onInit() {
 *     Menu rootMenu = Menu.getRootMenu(getContext());
 *     addModel(<span class="st">"rootMenu"</span>, rootMenu);
 * } </pre>
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Menu implements Serializable {

    private static final long serialVersionUID = 5820272228903777866L;

    private static final Object loadLock = new Object();

    /**
     * The menu configuration filename: &nbsp; "<tt>/WEB-INF/menu.xml</tt>"
     */
    protected static final String CONFIG_FILE = "/WEB-INF/menu.xml";

    /** The cached root Menu as defined in <tt>menu.xml</tt> */
    protected static Menu rootMenu;

    // ----------------------------------------------------- Instance Variables

    /** The list of submenu items. */
    protected List children = new ArrayList();

    /** The menu display label. */
    protected String label;

    /** The menu path. */
    protected String path;

    /** The list of valid role names. */
    protected List roles = new ArrayList();

    /** The menu is selected flag. */
    protected boolean selected;

    /** The tooltip title attribute. */
    protected String title = "";

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Menu instance.
     */
    public Menu() {
    }

    /**
     * Create a Menu from the given menu-item XML Element.
     *
     * @param menuElement the menu-item XML Element.
     */
    public Menu(Element menuElement) {
        if (menuElement == null) {
            throw new IllegalArgumentException("Null menuElement parameter");
        }

        setLabel(menuElement.getAttribute("label"));
        setPath(menuElement.getAttribute("path"));
        String titleAtr = menuElement.getAttribute("title");
        if (StringUtils.isNotBlank(titleAtr)) {
            setTitle(titleAtr);
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
                getChildren().add(new Menu((Element) node));
            }
        }
    }

    /**
     * Create a new Menu from the given menu. Provides a deep copy constructor.
     *
     * @param menu the menu to copy
     */
    public Menu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Null menu parameter");
        }
        setLabel(menu.getLabel());
        setPath(menu.getPath());
        setTitle(menu.getTitle());
        setRoles(menu.getRoles());
        setSelected(menu.isSelected());

        for (int i = 0; i < menu.getChildren().size(); i++) {
            Menu menuChild = (Menu) menu.getChildren().get(i);
            getChildren().add(new Menu(menuChild));
        }
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return list of of submenu items.
     *
     * @return the list of submenu items
     */
    public List getChildren() {
        return children;
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
        return selected;
    }

    /**
     * Set the selected status of the Menu item.
     *
     * @param selected the selected status of the Menu item.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
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
     * @see Object#toString()
     */
    public String toString() {
        return getClass().getName() +
            "[label=" + getLabel() +
            ",path=" + getPath() +
            ",title=" + getTitle() +
            ",selected=" + isSelected() +
            ",roles=" + roles +
            ",children=" + children +
            "]";
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

    /**
     * Return a copy of the Appliations root Menu as defined in the
     * configuration file "<tt>/WEB-INF/menu.xml</tt>".
     * <p/>
     * The returned root menu is always selected.
     *
     * @param context the request context
     * @return a copy of the application's root Menu
     */
    public static synchronized Menu getRootMenu(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }

        synchronized(loadLock) {
            if (rootMenu == null) {
                Menu menu = new Menu();

                InputStream inputStream =
                    context.getServletContext().getResourceAsStream(CONFIG_FILE);

                if (inputStream == null) {
                    String msg = "could not find configuration file:" + CONFIG_FILE;
                    throw new RuntimeException(msg);
                }

                Document document = ClickUtils.buildDocument(inputStream);

                Element rootElm = document.getDocumentElement();

                NodeList list = rootElm.getChildNodes();

                for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (node instanceof Element) {
                        menu.getChildren().add(new Menu((Element) node));
                    }
                }

                rootMenu = menu;
            }
        }

        Menu menu = new Menu(rootMenu);

        menu.select(context);
        menu.setSelected(true);

        return menu;
    }

    /**
     * Set the selected status of the menu and its children depending upon
     * the current context's path. If the path equals the menus path the
     * menu will be selected.
     *
     * @param context the request context
     */
    public void select(Context context) {
        String path = getPath();
        if (path != null) {
            path = path.startsWith("/") ? path : "/" + path;
            selected = path.equals(context.getResourcePath());
        } else {
            selected = false;
        }

        for (int i = 0; i < getChildren().size(); i++) {
            Menu menu = (Menu) getChildren().get(i);
            menu.select(context);
            if (menu.isSelected()) {
                selected = true;
            }
        }
    }

}
