/*
 * Copyright 2004-2007 Malcolm A. Edgar
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

package net.sf.click.extras.control.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import javax.servlet.ServletContext;
import net.sf.click.Context;
import net.sf.click.control.Decorator;
import net.sf.click.control.AbstractControl;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a tree control  for displaying hierarchical data.
 *
 * <p/>Below is screenshot of how the tree will render in a browser.
 *
 * <table cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2' src='tree.png' title='Tree'/>
 * </td>
 * </tr>
 * </table>
 *
 * <h3>Tree Example</h3>
 *
 * An example tree usage is provided below (this code was used to produce the screenshot):
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> PlainTreePage <span class="kw">extends</span> BorderPage {
 *
 *     <span class="kw">public</span> PlainTreePage() {
 *         Tree tree = buildTree();
 *         addControl(tree);
 *     }
 *
 *     <span class="kw">public Tree</span> buildTree() {
 *         Tree tree = <span class="kw">new</span> Tree(<span class="st">"tree"</span>);
 *
 *         <span class="cm">//build the tree model.</span>
 *         <span class="cm">//by default the root node is not rendered. This can be changed by calling setRootNodeDisplayed(true);</span>
 *         TreeNode root = new TreeNode("c:");
 *         TreeNode dev = <span class="kw">new</span> TreeNode(<span class="st">"dev"</span>,<span class="st">"1"</span>, root);
 *         <span class="kw">new</span> TreeNode(<span class="st">"java.pdf"</span>, <span class="st">"2"</span>, dev);
 *         <span class="kw">new</span> TreeNode(<span class="st">"ruby.pdf"</span>, <span class="st">"3"</span>, dev);
 *
 *         TreeNode programFiles = <span class="kw">new</span> TreeNode(<span class="st">"program files"</span>, <span class="st">"4"</span>, root);
 *         TreeNode adobe = <span class="kw">new</span> TreeNode(<span class="st">"Adobe"</span>, <span class="st">"5"</span>, programFiles);
 *         adobe.setChildrenSupported(<span class="kw">true</span>); <span class="cm">//this node is a directory not a file, so setChildrenSupported to true</span>
 *
 *         TreeNode download = <span class="kw">new</span> TreeNode(<span class="st">"downloads"</span>,<span class="st">"6"</span>, root);
 *         TreeNode web = <span class="kw">new</span> TreeNode(<span class="st">"web"</span>, <span class="st">"7"</span>, download);
 *         <span class="kw">new</span> TreeNode(<span class="st">"html.pdf"</span>, <span class="st">"8"</span>, web);
 *         <span class="kw">new</span> TreeNode(<span class="st">"css.html"</span>, <span class="st">"9"</span>, web);
 *
 *         TreeNode databases = <span class="kw">new</span> TreeNode(<span class="st">"databases"</span>, <span class="st">"10"</span>, download);
 *         <span class="kw">new</span> TreeNode(<span class="st">"mysql.html"</span>,<span class="st">"11"</span>,databases);
 *         <span class="kw">new</span> TreeNode(<span class="st">"oracle.pdf"</span>,<span class="st">"12"</span>,databases);
 *         <span class="kw">new</span> TreeNode(<span class="st">"postgres"</span>,<span class="st">"13"</span>,databases);
 *
 *         tree.setRootNode(node);
 *         <span class="kw">return</span> tree;
 *     }
 * } </pre>
 *
 * <h4>Tree Style</h4>
 *
 * <p/>The Tree control automatically deploys the tree CSS style sheet
 * (<tt>tree.css</tt>) to the application directory <tt>/click/tree</tt>.
 * To import the style sheet simply reference the
 * {@link net.sf.click.util.PageImports} object. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *    &lt;head&gt;
 *       <span class="blue">$imports</span>
 *    &lt;/head&gt;
 *    &lt;body&gt;
 *       <span class="red">$tree</span>
 *    &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * <p/><strong>Credit</strong> goes to <a href="http://wicket.sourceforge.net">Wicket</a> for the following:
 * <ul>
 *      <li>images/folder-closed.png</li>
 *      <li>images/folder-open.png</li>
 *      <li>images/item.png</li>
 * </ul>
 *
 * @author Bob Schellink
 */
public class Tree extends AbstractControl {

    // -------------------------------------------------------------- Constants

    /** The tree's expand/collapse parameter name: <tt>"expandTreeNode"</tt>. */
    public static final String EXPAND_TREE_NODE_PARAM = "expandTreeNode";

    /** The tree's select/deselect parameter name: <tt>"selectTreeNode"</tt>. */
    public static final String SELECT_TREE_NODE_PARAM = "selectTreeNode";

    /** The tree.css style sheet import link. */
    public static final String TREE_IMPORTS =
            "<link type=\"text/css\" rel=\"stylesheet\" href=\"$/click/tree/tree.css\"></link>\n";

    /**
     * The Tree resource file names.
     */
    protected static final String[] TREE_RESOURCES = {
        "/net/sf/click/extras/control/tree/tree.css"
    };

    /**
     * The Tree image file names.
     */
    protected static final String[] TREE_IMAGES = {
        "/net/sf/click/extras/control/tree/images/folder-open.png",
        "/net/sf/click/extras/control/tree/images/folder-closed.png",
        "/net/sf/click/extras/control/tree/images/item.png",
        "/net/sf/click/extras/control/tree/images/corner.png",
        "/net/sf/click/extras/control/tree/images/minus.png",
        "/net/sf/click/extras/control/tree/images/minus-corner.png",
        "/net/sf/click/extras/control/tree/images/plus.png",
        "/net/sf/click/extras/control/tree/images/plus-corner.png",
        "/net/sf/click/extras/control/tree/images/tjunction.png",
        "/net/sf/click/extras/control/tree/images/vertical-line.png"
    };

    /** default serial version id */
    private static final long serialVersionUID = 1L;


    // ----------------------------------------------------------- Instance Variables

    /** The list of tree controls. */
    protected List controls;

    /** The tree's hierarchical data model. */
    protected TreeNode rootNode;

    /** Callback provider for users to decorate tree nodes. */
    private transient Decorator decorator;

    /** Specifies if the root node should be displayed, or only its children. By default this value is false.*/
    private boolean rootNodeDisplayed = false;

    /** List of subscribed listeners to tree events.*/
    private List listeners = new ArrayList();


    // ----------------------------------------------------------- Public Constructors

    /**
     * Create an Tree control for the given name.
     *
     *<p/>Also sets a id attribute of <tt>"tree"</tt> to qualify the
     * tree control with the <tt>tree.css.</tt>
     *
     * @param name the tree name
     * @throws IllegalArgumentException if the name is null
     */
    public Tree(String name) {
        setName(name);
        setAttribute("id", "tree");
    }

    /**
     * Create a Tree with no name defined.
     *
     **<p/>Also sets a id attribute of <tt>"tree"</tt> to qualify the
     * tree control with the <tt>tree.css.</tt>
     *
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Tree() {
        super();
        setAttribute("id", "tree");
    }


    // ----------------------------------------------------------- Public Getters and Setters

    /**
     * Return the tree's root TreeNode.
     *
     * @return the tree's root TreeNode.
     */
    public TreeNode getRootNode() {
        //Calculate the root node dynamically by finding the node where parent == null.
        //If user creates a new root node by adding the current root node as a child,
        //this method will return wrong results
        if(rootNode == null) {
            return null;
        }
        while((rootNode.getParent()) != null) {
            rootNode = rootNode.getParent();
        }
        return rootNode;
    }

    /**
     * Return if tree has a root node.
     *
     * @return boolean indicating if the tree's root has been set.
     */
    public boolean hasRootNode() {
        return getRootNode() != null;
    }

    /**
     * Return if the tree's root node should be displayed or not.
     *
     * @return if root node should be displayed
     */
    public boolean isRootNodeDisplayed() {
        return rootNodeDisplayed;
    }

    /**
     * Sets whether the tree's root node should be displayed or not.
     *
     * @param rootNodeDisplayed true if the root node should be displayed, false otherwise
     */
    public void setRootNodeDisplayed(boolean rootNodeDisplayed) {
        this.rootNodeDisplayed = rootNodeDisplayed;
    }

    /**
     * Set the tree's root TreeNode.
     *
     * <p/>As a side effect this method will also set the root node to expanded,
     * because that is the most sensible default value.
     *
     * @param rootNode node will be set as the root
     */
    public void setRootNode(TreeNode rootNode) {
        if(rootNode == null)
            return;
        this.rootNode = rootNode;

        //if root node should not be displayed, it is expanded
        //by default. sensible default otherwise user must explicitly set the root
        //node to expanded each time.
        if(!isRootNodeDisplayed())
            this.rootNode.setExpanded(true);
    }

    /**
     * Get the tree's decorator.
     *
     * @return the tree's decorator.
     */
    public Decorator getDecorator() {
        return decorator;
    }

    /**
     * Set the tree's decorator which enables a interception point for users to render
     * the tree nodes.
     *
     * @param decorator the tree's decorator
     */
    public void setDecorator(Decorator decorator) {
        this.decorator = decorator;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet file:
     * <tt>click/tree/tree.css</tt>.
     *
     * @return the HTML head import statements for the control stylesheet
     */
    public String getHtmlImports() {
        String path = getContext().getRequest().getContextPath();
        StringBuffer buffer = new StringBuffer(100);
        buffer.append(StringUtils.replace(TREE_IMPORTS, "$", path));
        return buffer.toString();
    }


    // ----------------------------------------------------------- Public Behavior

    /**
     * This method binds the users request of expanded and collapsed nodes to the tree's nodes.
     * The behavior of this method is to swap the value of the expanded state.
     * Thus expanded nodes will be collapsed and collapsed nodes will be expanded.
     */
    public void bindExpandOrCollapseValues() {
        String[] nodeIds = getRequestValues(EXPAND_TREE_NODE_PARAM);
        if(nodeIds == null || nodeIds.length <= 0)
            return;
        expandOrCollapse(nodeIds);
    }

    /**
     * This method binds the users request of selected nodes to the tree's nodes.
     *
     * <p/>The behavior of this method is to swap the value of the selected state.
     * Thus selected nodes will be deselected and deselected nodes will be selected.
     */
    public void bindSelectOrDeselectValues() {
        String[] nodeIds = getRequestValues(SELECT_TREE_NODE_PARAM);
        if(nodeIds == null || nodeIds.length <= 0)
            return;
        selectOrDeselect(nodeIds);
    }

    /**
     * Expands all nodes with matching id's in the specified collection and inform any listeners of the change.
     *
     * @param id identifier of the node to be expanded.
     */
    public void expand(String id) {
        if(id == null)
            return;
        setExpandState(id, true);
    }

    /**
     * Collapses all nodes with matching id's in the specified collection and inform any listeners of the change.
     *
     * @param id identifier of node to be expanded.
     */
    public void collapse(String id) {
        if(id == null)
            return;
        setExpandState(id, false);
    }

    /**
     * Expand all the nodes of the tree and inform any listeners of the change.
     */
    public void expandAll() {
        for(Iterator it = iterator(); it.hasNext();) {
            TreeNode node = (TreeNode) it.next();
            boolean oldValue = node.isExpanded();
            node.setExpanded(true);
            fireNodeExpanded(node, oldValue);
        }
    }

    /**
     * Collapse all the nodes of the tree and inform any listeners of the change
     */
    public void collapseAll() {
        for(Iterator it = iterator(); it.hasNext();) {
            TreeNode node = (TreeNode) it.next();
            boolean oldValue = node.isExpanded();
            node.setExpanded(false);
            fireNodeCollapsed(node, oldValue);
        }
    }

    /**
     * Select all nodes with matching id's in the specified collection and inform any listeners of the change.
     *
     * @param id identifier of node to be selected.
     */
    public void select(String id) {
        if(id == null)
            return;
        setSelectState(id, true);
    }

    /**
     * Deselect all nodes with matching id's in the specified collection and inform any listeners of the change.
     *
     * @param id id of node to be deselected.
     */
    public void deselect(String id) {
        if(id == null)
            return;
        setSelectState(id, false);
    }

    /**
     * Select all the nodes of the tree and inform any listeners of the change.
     */
    public void selectAll() {
        for(Iterator it = iterator(); it.hasNext();) {
            TreeNode node = (TreeNode) it.next();
            boolean oldValue = node.isSelected();
            node.setSelected(true);
            fireNodeSelected(node, oldValue);
        }
    }

    /**
     * Deselect all the nodes of the tree and inform any listeners of the change.
     */
    public void deselectAll() {
        for(Iterator it = iterator(); it.hasNext();) {
            TreeNode node = (TreeNode) it.next();
            boolean oldValue = node.isSelected();
            node.setSelected(false);
            fireNodeDeselected(node, oldValue);
        }
    }

    /**
     * Returns all the nodes that were expanded.
     *
     * @param includeInvisibleNodes indicator if only invisibile nodes should be included.
     * @return list of currently expanded nodes.
     */
    public List getExpandedNodes(boolean includeInvisibleNodes) {
        List currentlyExpanded = new ArrayList();
        for(Iterator it = iterator(); it.hasNext();) {
            TreeNode node = (TreeNode)it.next();
            if(node.isExpanded())
                if (includeInvisibleNodes || isVisible(node))
                    currentlyExpanded.add(node);
        }
        return currentlyExpanded;
    }

    /**
     * Returns all the nodes that were selected.
     *
     * @param includeInvisibleNodes indicates if invisibile nodes should be included.
     * @return list of currently selected nodes.
     */
    public List getSelectedNodes(boolean includeInvisibleNodes) {
        List currentlySelected = new ArrayList();
        for(Iterator it = iterator(); it.hasNext();) {
            TreeNode node = (TreeNode)it.next();
            if(node.isSelected())
                if (includeInvisibleNodes || isVisible(node))
                    currentlySelected.add(node);
        }
        return currentlySelected;
    }

    /**
     * Deploy all files defined in the constants <tt>{@link #TREE_RESOURCES}</tt> and
     * <tt>{@link #TREE_IMAGES}</tt> to the <tt>click/tree</tt> web
     * directory when the application is initialized.
     *
     * @param servletContext the servlet context
     * @see net.sf.click.Control#onDeploy(ServletContext)
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFiles(servletContext,
                TREE_RESOURCES,
                "click/tree");
        ClickUtils.deployFiles(servletContext,
                TREE_IMAGES,
                "click/tree/images");
    }

    /**
     * TreeNode callback interface.
     */
    interface Callback {
        public void callback(final TreeNode node);
    }

    /**
     * Returns an iterator over all the nodes.
     *
     * @return iterator over all elements in the tree
     */
    public Iterator iterator() {
        return iterator(getRootNode());
    }

    /**
     * Returns an iterator over all nodes starting from the specified node. If null
     * is specified, root node is used instead.
     *
     * @param node starting point of nodes to iterator over
     * @return iterator over all nodes starting form the specified node
     */
    public Iterator iterator(TreeNode node) {
        if(node == null)
            node = getRootNode();
        return new BreadthTreeIterator(node);
    }

    /**
     * Finds and returns the first node that matches the id.
     *
     * @param id identifier of the node to find
     * @return TreeNode the first node matching the id.
     * @throws IllegalArgumentException if argument is null.
     */
    public TreeNode find(String id) {
        if( id == null)
            throw new IllegalArgumentException("Argument cannot be null.");
        return find(getRootNode(), id);
    }

    /**
     * Processes user request to change state of the tree.
     * This implementation processes any expand/collapse and select/deselect
     * changes as requested.
     *
     * @see net.sf.click.Control#onProcess()
     * @see #bindExpandOrCollapseValues()
     * @see #bindSelectOrDeselectValues()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        bindExpandOrCollapseValues();
        bindSelectOrDeselectValues();
        return true;
    }

    /**
     * Adds the listener to start receiving tree events.
     *
     * @param listener to add to start receiving tree events.
     */
    public void addListener(TreeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the listener to stop receiving tree events.
     *
     * @param listener to be removed to stop receiving tree events.
     */
    public void removeListener(TreeListener listener){
        listeners.remove(listener);
    }


    // ----------------------------------------------------------- Default Rendering

    /**
     * Return a HTML rendered Tree string of all the tree's nodes.
     *
     * <p/>Note: by default the tree's root node will not be rendered.
     * However this behavior can be changed by calling {@link #setRootNodeDisplayed(boolean) }
     * with true.
     *
     * @see java.lang.Object#toString()
     * @return a HTML rendered Tree string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(256);
        buffer.append("<div id=\"");
        buffer.append(getId());
        buffer.append("\">\n");

        if(isRootNodeDisplayed()){
            TreeNode temp = new TreeNode();

            //careful not to use the method temp.add(), because that will
            //set temp as the new root node of the tree
            temp.getMutableChildren().add(getRootNode());
            renderTree(buffer, temp, 0);
        } else {
            renderTree(buffer, getRootNode(), 0);
        }

        buffer.elementEnd("div");
        buffer.append("\n");
        return buffer.toString();
    }

    /**
     * Render the children of the specified tree node as html markup and append the output to the specified buffer.
     *
     * <p/><strong>Note:</strong> only the children of the specified  tree node will be renderered not the
     * treeNode itself. This method is recursive, so the node's children and their children will be rendered and so on.
     *
     * @param buffer string buffer containing the markup
     * @param treeNode specified node who's children will be rendered
     * @param indentation current level of the treeNode. The indentation increases each
     * time the depth of the tree increments.
     *
     * @see #setRootNodeDisplayed(boolean)
     */
    protected void renderTree(HtmlStringBuffer buffer, TreeNode treeNode, int indentation) {
        indentation++;

        buffer.elementStart("ul");

        buffer.append(" class=\"menu");
        buffer.append(Integer.toString(indentation));
        buffer.append("\">\n");

        Iterator it = treeNode.getChildren().iterator();
        while(it.hasNext()) {
            TreeNode child = (TreeNode) it.next();
            renderTreeNodeStart(buffer, child, indentation);
            renderTreeNode(buffer,child,indentation);
            if(child.getChildren().size() > 0 && child.isExpanded()) {
                renderTree(buffer, child, indentation);
            }
            renderTreeRowEnd(buffer, child, indentation);
        }
        buffer.append("</ul>\n");
    }

    /**
     * Interception point to render html before the tree node is rendered.
     *
     * @param buffer string buffer containing the markup
     * @param treeNode specified node to render
     * @param indentation current level of the treeNode
     */
    protected void renderTreeNodeStart(HtmlStringBuffer buffer, TreeNode treeNode, int indentation) {
        buffer.append("<li><span");
        StringBuffer sb = new StringBuffer("");
        sb.append(getExpandClass(treeNode));
        buffer.appendAttribute("class", sb.toString());
        buffer.appendAttribute("style", "display:block;");
        buffer.closeTag();

        //Render the node's expand/collapse functionality.
        //This includes adding a css class for the current expand/collapse state.
        //In the tree.css file, the css classes are mapped to icons by default.
        if(treeNode.getChildren().size() > 0) {
            renderExpandAndCollapseAction(buffer, treeNode);
        } else {
            buffer.append("<span class=\"spacer\"></span>");
        }
        buffer.append("\n");
    }

    /**
     * Interception point to render html after the tree node was rendered.
     *
     * @param buffer string buffer containing the markup
     * @param treeNode specified node to render
     * @param indentation current level of the treeNode
     */
    protected void renderTreeRowEnd(HtmlStringBuffer buffer, TreeNode treeNode, int indentation) {
        buffer.append("</span></li>\n");
    }

    /**
     * Render the expand and collapse action of the tree.
     *
     * <p/>Default implementation creates a hyperlink that users can click on
     * to expand or collapse the nodes.
     *
     * @param buffer string buffer containing the markup
     * @param treeNode treeNode to render
     */
    protected void renderExpandAndCollapseAction(HtmlStringBuffer buffer, TreeNode treeNode) {
        buffer.elementStart("a");
        StringBuffer tmpBuf = new StringBuffer(getContext().getRequest().getRequestURI());
        tmpBuf.append("?").append(EXPAND_TREE_NODE_PARAM).append("=").append(treeNode.getId());
        buffer.appendAttribute("href", tmpBuf.toString());
        buffer.appendAttribute("class","spacer");
        buffer.closeTag();
        buffer.elementEnd("a");
        buffer.append("\n");
    }

    /**
     * Render the specified treeNode.
     *
     * <p/>If a decorator was specified using {@link #setDecorator(Decorator) }, this method will
     * render using the decorator instead.
     *
     * @param buffer string buffer containing the markup
     * @param treeNode treeNode to render
     * @param indentation current level of the treeNode
     */
    protected void renderTreeNode(HtmlStringBuffer buffer, TreeNode treeNode, int indentation) {
        if(getDecorator() != null) {
            Object value = getDecorator().render(treeNode, getContext());
            if (value != null) {
                buffer.append(value);
            }
            return;
        }

        //TODO IE HACK. IE7 displays the tree nodes properly alligned when rendered inside a table.  Without the code below
        //the icons and node values does not align correctly.
        //Firefox and Opera displays nicely without this hack. There might be a better way to fix this ;-)
        boolean isIE= isIE(context);
        if(isIE)
            buffer.append("<table style=\"line-height:1.3em;margin:0;padding:0;display:inline\" " +
                    "border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");

        //render the icon to display
        buffer.append("<span class=\"");
        buffer.append(getIconClass(treeNode));
        buffer.append("\">");

        //TODO IE HACK. With a empty span <span></span> IE does not render the icons. Putting a '&nbsp;' in the span
        //seemed to work. Perhaps there is a better workaround.
        buffer.append("&nbsp;");
        buffer.append("</span>");

        if(isIE)
            buffer.append("</td><td>");

        buffer.elementStart("span");
        if(treeNode.isSelected())
            buffer.appendAttribute("class","selected");
        else
            buffer.appendAttribute("class","unselected");
        buffer.closeTag();

        //renders the node value
        renderValue(buffer, treeNode);
        buffer.elementEnd("span");

        if(isIE)
            buffer.append("</td></tr></table>");
    }

    /**
     * Render the node's value.
     *
     * <p/>Subclasses should override this method to change the rendering of the node's value.
     * By default the value will be rendered as a hyperlink, passing its <em>id</em> to the server.
     *
     * @param buffer string buffer containing the markup
     * @param treeNode treeNode to render
     */
    protected void renderValue(HtmlStringBuffer buffer, TreeNode treeNode) {
        buffer.elementStart("a");
        StringBuffer tmpBuf = new StringBuffer(getContext().getRequest().getRequestURI());
        tmpBuf.append("?").append(SELECT_TREE_NODE_PARAM).append("=").append(treeNode.getId());
        buffer.appendAttribute("href", tmpBuf.toString());
        buffer.closeTag();
        if(treeNode.getValue() != null)
            buffer.append(treeNode.getValue());
        buffer.elementEnd("a");
        buffer.append("\n");
    }

    /**
     * Query the specified treeNode and check which css class to apply.
     *
     * <p/>Possible classes are expanded, collapsed, leaf, expandedLastNode,
     * collapsedLastNode and leafLastNode
     *
     * @param treeNode the tree node to check for css class
     * @return string specific css class to apply
     */
    protected String getExpandClass(TreeNode treeNode) {
        StringBuffer buffer = new StringBuffer();
        if(treeNode.getChildren().size() > 0 && treeNode.isExpanded()) {
                buffer.append("expanded");
        } else if (treeNode.getChildren().size() > 0) {
            buffer.append("collapsed");
        } else {
            buffer.append("leaf");
        }
        if(treeNode.isLastChild())
            buffer.append("LastNode");
        return buffer.toString();
    }

    /**
     * Query the specified treeNode and check which css class to apply for the icons.
     *
     * <p/>Possible classes are expandedIcon, collapsedIcon and leafIcon.
     *
     * @param treeNode the tree node to check for css class
     * @return string specific css class to apply
     */
    protected String getIconClass(TreeNode treeNode) {
        StringBuffer buffer = new StringBuffer();
        if(treeNode.isExpanded() && treeNode.hasChildren())
                return "expandedIcon";
        else if(!treeNode.isExpanded() && treeNode.hasChildren() || treeNode.isChildrenSupported())
            return "collapsedIcon";
        else
            return "leafIcon";
    }

    /**
     * Queries specified context if the user is using Internet Explorer
     *
     * @param context the Page request Context
     * @return true if the request came from IE, false otherwise
     */
    protected boolean isIE(Context context) {
        String useragent = context.getRequest().getHeader("User-Agent");
        if(useragent == null)
            return false;
        String user = useragent.toLowerCase();
        if(user.indexOf("msie") != -1) {
            return true;
        }
        return false;
    }


    // ----------------------------------------------------------- Protected observer behavior

    /**
     * Notifies all listeners currently registered with the tree, about any expand events.
     *
     * @param node specific the TreeNode that was expanded
     */
    protected void fireNodeExpanded(TreeNode node, boolean oldValue) {
        for(Iterator it = listeners.iterator(); it.hasNext();) {
            TreeListener listener = (TreeListener) it.next();
            listener.nodeExpanded(this, node, getContext(), oldValue);
        }
    }

    /**
     * Notifies all listeners currently registered with the tree, about any collapse events.
     *
     * @param node specific the TreeNode that was collapsed
     */
    protected void fireNodeCollapsed(TreeNode node, boolean oldValue) {
        for(Iterator it = listeners.iterator(); it.hasNext();) {
            TreeListener listener = (TreeListener) it.next();
            listener.nodeCollapsed(this, node, getContext(), oldValue);
        }
    }

    /**
     * Notifies all listeners currently registered with the tree, about any selection events.
     *
     * @param node specific the TreeNode that was selected
     */
    protected void fireNodeSelected(TreeNode node, boolean oldValue) {
        for(Iterator it = listeners.iterator(); it.hasNext();) {
            TreeListener listener = (TreeListener) it.next();
            listener.nodeSelected(this, node, getContext(), oldValue);
        }
    }

    /**
     * Notifies all listeners currently registered with the tree, about any deselection events.
     *
     * @param node specific the TreeNode that was deselected
     */
    protected void fireNodeDeselected(TreeNode node, boolean oldValue) {
        for(Iterator it = listeners.iterator(); it.hasNext();) {
            TreeListener listener = (TreeListener) it.next();
            listener.nodeDeselected(this, node, getContext(), oldValue);
        }
    }


    // ----------------------------------------------------------- Protected behavior

    /**
     * Sets the TreeNode expand state to the new value.
     *
     * @param node specifies the TreeNode which expand state will be set
     * @param newValue specifies the new expand state
     */
    protected void setExpandState(TreeNode node, boolean newValue) {
        boolean oldValue = node.isExpanded();
        node.setExpanded(newValue);
        if(newValue)
            fireNodeExpanded(node, oldValue);
        else
            fireNodeCollapsed(node, oldValue);
    }

    /**
     * Swaps the expand state of all TreeNodes with specified ids.
     * Thus if a node's expand state is currently 'true', calling expandOrCollapse
     * will set the expand state to 'false' and vice versa.
     *
     * @param ids array of node ids
     */
    protected void expandOrCollapse(String[] ids) {
        processNodes(ids,new Callback() {
            public void callback(TreeNode node) {
                setExpandState(node, !node.isExpanded());
            }
        });
    }

    /**
     * Sets the expand state of the TreeNode with specified id to the new value.
     *
     * @param id specifies the id of a TreeNode which expand state will be set
     * @param newValue specifies the new expand state
     */
    protected void setExpandState(final String id, final boolean newValue) {
        TreeNode node = find(id);
        if(node == null)
            return;
        setExpandState(node, newValue);
    }

    /**
     * Sets the TreeNode expand state of each node in the specified collection to the new value.
     *
     * @param nodes specifies the collection of a TreeNodes which expand states will be set
     * @param newValue specifies the new expand state
     */
    protected void setExpandState(final Collection nodes, final boolean newValue) {
        processNodes(nodes,new Callback() {
            public void callback(TreeNode node) {
                setExpandState(node, newValue);
            }
        });
    }

    /**
     * Sets the TreeNode select state to the new value.
     *
     * @param node specifies the TreeNode which select state will be set
     * @param newValue specifies the new select state
     */
    protected void setSelectState(TreeNode node, boolean newValue) {
        boolean oldValue = node.isSelected();
        node.setSelected(newValue);
        if(newValue)
            fireNodeSelected(node, oldValue);
        else
            fireNodeDeselected(node, oldValue);
    }

    /**
     * Swaps the select state of all TreeNodes with specified ids to the new value.
     * Thus if a node's select state is currently 'true', calling selectOrDeselect
     * will set the select state to 'false' and vice versa.
     *
     * @param ids array of node ids
     */
    protected void selectOrDeselect(String[] ids) {
        processNodes(ids,new Callback() {
            public void callback(TreeNode node) {
                setSelectState(node, !node.isSelected());
            }
        });
    }

    /**
     * Sets the select state of the TreeNode with specified id to the new value.
     *
     * @param id specifies the id of a TreeNode which select state will be set
     * @param newValue specifies the new select state
     */
    protected void setSelectState(final String id, final boolean newValue) {
        TreeNode node = find(id);
        if(node == null)
            return;
        setSelectState(node, newValue);
    }

    /**
     * Sets the TreeNode select state of each node in the specified collection to the new value.
     *
     * @param nodes specifies the collection of a TreeNodes which select states will be set
     * @param newValue specifies the new select state
     */
    protected void setSelectState(final Collection nodes, final boolean newValue) {
        processNodes(nodes,new Callback() {
            public void callback(TreeNode node) {
                setSelectState(node, newValue);
            }
        });
    }

    /**
     * Provides callback functionality for all the specified nodes.
     *
     * @param ids the array of nodes to process
     * @param callback object on which callbacks are made
     */
    protected void processNodes(String[] ids, Callback callback) {
        if(ids == null)
            return;
        for(int i = 0, n = ids.length; i < n; i++) {
            String id = ids[i];
            if(id == null || id.trim().equals(""))
                continue;
            TreeNode node = find(id);
            if(node == null)
                continue;
            callback.callback(node);
        }
    }

    /**
     * Provides callback functionality for all the specified nodes.
     *
     * @param nodes the collection of nodes to process
     * @param callback object on which callbacks are made
     */
    protected void processNodes(Collection nodes, Callback callback) {
        if(nodes == null)
            return;
        for(Iterator it = nodes.iterator(); it.hasNext(); ) {
            TreeNode node = (TreeNode) it.next();
            callback.callback(node);
        }
    }

    /**
     * Finds and returns the first node that matches the id, starting the search
     * from the specified node.
     *
     * @param node specifies at which node the search must start from
     * @param id specifies the id of the TreeNode to find
     * @return TreeNode the first node matching the id or null if no match was found.
     */
    protected TreeNode find(TreeNode node, String id) {
        for(Iterator it = iterator(node);it.hasNext();) {
            TreeNode result = (TreeNode) it.next();
            if(result.getId().equals(id))
                return result;
        }
        return null;
    }

    /**
     * Returns the parameter in {@link javax.servlet.http.HttpServletRequest} for the specified param.
     *
     * @param param specifies the parameter to return
     * @return param the specified parameter or a empty string <span class="st">""</span> if not found
     */
    protected String getRequestValue(String param) {
        String result = getContext().getRequestParameter(param);

        if (result != null) {
            return result.trim();
        } else {
            return "";
        }
    }

    /**
     * Returns all parameters in {@link javax.servlet.http.HttpServletRequest} for the specified param.
     *
     * @return param all matching parameters or null if no parameter was found
     */
    protected String[] getRequestValues(String param) {
        String[] resultArray = getContext().getRequest().getParameterValues(param);
        return resultArray;
    }


    //------------------------------------------------------------Inner classes

    /**
     * Iterate over all the nodes in the tree in a breadth first manner.
     *
     * <p/>Thus in a tree with the following nodes (top to bottom):
     * <pre class="codeHtml">
     *                           <span class="red">root</span>
     *       <span class="blue">node1</span>                            <span class="blue">node2</span>
     *node1.1  node1.2          node2.1  node2.2
     * </pre>
     *
     * </p>the iterator will return the nodes in the following order:
     * <pre class="codeHtml">
     *      <span class="red">root</span>
     *      <span class="blue">node1</span>
     *      <span class="blue">node2</span>
     *      node1.1
     *      node1.2
     *      node2.1
     *      node2.2
     * </pre>
     */
    static class BreadthTreeIterator implements Iterator {

        /**queue for storing node's */
        private List queue = new ArrayList();

        /** indicator to iterate collapsed node's */
        private boolean iterateCollapsedNodes = true;

        /**
         * Creates a iterator and adds the specified node to the queue.
         * The specified node will be set as the root of the traversal.
         *
         * @param node node will be set as the root of the traversal.
         */
        public BreadthTreeIterator(TreeNode node) {
            if(node == null)
                throw new IllegalArgumentException("Node cannot be null");
            queue.add(node);
        }

        /**
         * Creates a iterator and adds the specified node to the queue.
         * The specified node will be set as the root of the traversal.
         *
         * @param node node will be set as the root of the traversal.
         * @param iterateCollapsedNodes indicator to iteratte collapsed node's
         */
        public BreadthTreeIterator(TreeNode node, boolean iterateCollapsedNodes) {
            if(node == null)
                throw new IllegalArgumentException("Node cannot be null");
            queue.add(node);
            this.iterateCollapsedNodes = iterateCollapsedNodes;
        }

        /**
         * Returns true if there are more nodes, false otherwise.
         *
         * @return boolean true if there are more nodes, false otherwise.
         */
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        /**
         * Returns the next node in the iteration.
         *
         * @return the next node in the iteration.
         * @exception NoSuchElementException iteration has no more node.
         */
        public Object next() {
            try {
                TreeNode node = (TreeNode) queue.remove(queue.size() -1); //remove from the end of queue
                if(node.hasChildren()) {
                    if(iterateCollapsedNodes || node.isExpanded()) {
                        push(node.getChildren());
                    }
                }
                return node;
            } catch(IndexOutOfBoundsException e) {
                throw new NoSuchElementException("There is  no more node's to iterate");
            }
        }

        /**
         * Remove operation is not supported.
         *
         * @exception UnsupportedOperationException <tt>remove</tt> operation is not supported by this Iterator.
         */
        public void remove() {
            throw new UnsupportedOperationException("remove operation is not supported.");
        }

        /**
         * Pushes the specified list of node's to push on the beginning of the queue.
         *
         * @param children list of node's to push on the beginning of the queue
         */
        private void push(List children) {
            for(Iterator it = children.iterator(); it.hasNext();) {
                queue.add(0, it.next()); //add to the beginning of queue
            }
        }
    }


    // ----------------------------------------------------------- Private behavior

    /**
     * Returns whether the specified node is visible. The semantics of visible in this
     * context indicates whether the node is currently displayed on the screen.
     * This means all parent nodes must be expanded for the node to be visible.
     *
     * @param node TreeNode's visibility to check
     * @return boolean true if the node's parent is visible, false otherwise
     */
    private boolean isVisible(TreeNode node) {
        while(!node.isRoot()) {
            if(!node.getParent().isExpanded())
                return false;
            node = node.getParent();
        }
        return true;
    }

    /**
     * Returns an array of all the nodes in the hierarchy, starting from the specified node up to and including the root node.
     *
     * <p/>The specified node will be at the start of the array and the root node will be at the end of the array.
     * Thus array[0] will return the specified node, while array[n - 1] where n is the size of the array, will return
     * the root node.
     *
     * @return list of all nodes from the specified node to the root node
     */
    private TreeNode[] getPathToRoot(TreeNode treeNode) {
        TreeNode[] nodes = new TreeNode[] {treeNode};
        while(treeNode.getParent() != null) {
            int length = nodes.length;
            System.arraycopy(nodes, 0, nodes = new TreeNode[length + 1], 0, length);
            nodes[length] = treeNode = treeNode.getParent();
        }
        return nodes;
    }
}
