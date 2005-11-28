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
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a hierarchical Menu control. Application menus can be defined
 * using a <tt>/WEB-INF/menu.xml</tt> configuration file.
 * <p/>
 * To access the root Menu item of the configured menus simply use the
 * constructor:
 * <pre class="codeJava">
 * <span class="kw">public class</span> MenuPage <span class="kw">extends</span> Page {
 *
 *     <span class="kw">public</span> MenuPage() {
 *         addControl(<span class="kw">new</span> Menu(<span class="st">"rootMenu"</span>);
 *     }
 * } </pre>
 *
 * The Menu control does not render itself, as the potential variations are
 * endless. You will need use a Velocity macro or Velocity code in your page to
 * render the Menu hierarchy.
 *
 * <pre class="codeHtml">
 * <span class="red">#</span>writeMenu(<span class="red">$</span><span class="st">rootMenu</span>) </pre>
 *
 * An example <tt>/WEB-INF/menu.xml</tt> configuration file is provided below:
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;menu&gt;
 *    &lt;menu label="A Menu" path="menu/menu-a1.htm"&gt;
 *       &lt;menu label="A 1 Menu" path="menu/menu-a1.htm" title="Menu item A1"/&gt;
 *       &lt;menu label="A 2 Menu" path="menu/menu-a2.htm" title="Menu item A2"/&gt;
 *    &lt;/menu&gt;
 *    &lt;menu label="B Menu" path="menu/menu-b1.htm"&gt;
 *       &lt;menu label="B 1 Menu" path="menu/menu-b1.htm" title="Menu item B1"/&gt;
 *       &lt;menu label="B 2 Menu" path="menu/menu-b2.htm" title="Menu item B2"/&gt;
 *    &lt;/menu&gt;
 * &lt;/menu&gt; </pre>
 *
 * The Menu config file DTD is provided below:
 *
 * <pre class="codeConfig">
 * &lt;!-- The Menu (menu.xml) Document Type Definition. --&gt;
 * &lt;!ELEMENT <span class="red">menu</span> (<span class="blue">menu</span>*)&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="blue">label</span> CDATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="blue">path</span> CATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="blue">title</span> CATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="blue">roles</span> CATA #IMPLIED&gt;
 *     &lt;!ATTLIST <span class="red">menu</span> <span class="blue">pages</span> CATA #IMPLIED&gt; </pre>
 *
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Menu implements Control, Serializable {

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

    /** The request context. */
    protected Context context;

    /** The menu display label. */
    protected String label;

    /** The menu control name. */
    protected String name;

    /**
     * The list of valid page paths. If any of these page paths match the
     * current request then the Menu item will be selected.
     */
    protected List pages = new ArrayList();

    /** The menu path. */
    protected String path;

    /** The list of valid role names. */
    protected List roles = new ArrayList();

    /** The menu is selected flag. */
    protected boolean selected;

    /** The tooltip title attribute. */
    protected String title = "";

    /** The is root menu item flag. */
    protected boolean isRootMenu;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new root Menu instance.
     */
    public Menu() {
        isRootMenu = true;
    }

    /**
     * Create a new root Menu instance with the given name.
     *
     * @param name the name of the menu
     */
    public Menu(String name) {
        setName(name);
        isRootMenu = true;
    }

    /**
     * Create a Menu from the given menu-item XML Element.
     *
     * @param menuElement the menu-item XML Element.
     */
    protected Menu(Element menuElement) {
        if (menuElement == null) {
            throw new IllegalArgumentException("Null menuElement parameter");
        }

        setLabel(menuElement.getAttribute("label"));
        setPath(menuElement.getAttribute("path"));
        String titleAtr = menuElement.getAttribute("title");
        if (StringUtils.isNotBlank(titleAtr)) {
            setTitle(titleAtr);
        }

        String pagesValue = menuElement.getAttribute("pages");
        if (!StringUtils.isBlank(pagesValue)) {
            StringTokenizer tokenizer = new StringTokenizer(pagesValue, ",");
            while (tokenizer.hasMoreTokens()) {
                getPages().add(tokenizer.nextToken().trim());
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
                getChildren().add(new Menu((Element) node));
            }
        }
    }

    /**
     * Create a new Menu from the given menu. Provides a deep copy constructor.
     *
     * @param menu the menu to copy
     */
    protected Menu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Null menu parameter");
        }
        setLabel(menu.getLabel());
        setName(menu.getName());
        setPath(menu.getPath());
        setTitle(menu.getTitle());
        setRoles(menu.getRoles());
        setPages(menu.getPages());
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
     * @see Control#getContext()
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set the request context. If the Menu item is the root menu item,
     * this method will load the root Menu's children elements.
     *
     * @see Control#setContext(Context)
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;

        if (isRootMenu()) {
            Menu rootMenu = getRootMenu(context);
            setLabel(rootMenu.getLabel());
            setPath(rootMenu.getPath());
            setTitle(rootMenu.getTitle());
            setRoles(rootMenu.getRoles());
            setPages(rootMenu.getPages());
            setSelected(rootMenu.isSelected());
            children.addAll(rootMenu.getChildren());
        }
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
     * Return true if the Menu is root menu time.
     *
     * @return true if the Menu is root menu time
     */
    public boolean isRootMenu() {
        return isRootMenu;
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
     * This method returns null.
     *
     * @see Control#getId()
     */
    public String getId() {
        return null;
    }

    /**
     * @see Control#getName()
     */
    public String getName() {
        return name;
    }


    /**
     * @see Control#setName(String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns null.
     *
     * @see Control#getParentMessages()
     */
    public Map getParentMessages() {
        return null;
    }

    /**
     * This method performs no processing and returns true.
     *
     * @see Control#onProcess()
     */
    public boolean onProcess() {
        return true;
    }

    /**
     * This method does nothing.
     *
     * @see Control#setListener(Object, String)
     */
    public void setListener(Object listener, String method) {
    }

    /**
     * This method does nothing.
     *
     * @see Control#setParentMessages(Map)
     */
    public void setParentMessages(Map messages) {
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return getClass().getName() +
            "[label=" + getLabel() +
            ",name=" + name +
            ",path=" + getPath() +
            ",title=" + getTitle() +
            ",isRootMenu=" + isRootMenu +
            ",selected=" + isSelected() +
            ",pages=" + pages +
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
     * configuration file "<tt>/WEB-INF/menu.xml</tt>", with the Control
     * name <tt>"rootMenu"</tt>.
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
                Menu menu = new Menu("rootMenu");

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
     * <p/>
     * If any of these Menu items pages paths match the current request then
     * the Menu item will be selected.
     *
     * @param context the request context
     */
    public void select(Context context) {
        final String pageToView = context.getResourcePath();

        if (pages.contains(pageToView)) {
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
            menu.select(context);
            if (menu.isSelected()) {
                selected = true;
            }
        }
    }
}
