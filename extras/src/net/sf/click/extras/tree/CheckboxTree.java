/*
 * Copyright 2007-2008 Malcolm A. Edgar
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
package net.sf.click.extras.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.control.Decorator;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Implementation of a tree control that provides checkboxes to enable selection
 * of nodes. This implementation assumes the tree is wrapped inside a html form.
 * Each time the form is submitted, all checkbox values are processed by this control.
 * <p/>
 * Below is screenshot of how the tree will render in a browser.
 *
 * <table cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2' src='checkbox-tree.png' title='Tree'/>
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
 *     <span class="kw">protected</span> Submit okSubmit;
 *     <span class="kw">protected</span> Submit cancelSubmit;
 *     <span class="kw">protected</span> Form form;
 *
 *     <span class="kw">public</span> PlainTreePage() {
 *         Tree tree = buildTree();
 *         addControl(tree);
 *         form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 *         addControl(form);
 *         okSubmit = <span class="kw">new</span> Submit(<span class="st">"okSubmit"</span>, <span class="st">"Select"</span>, <span class="kw">this</span>, <span class="st">"onSubmitClick"</span>);
 *         cancelSubmit = <span class="kw">new</span> Submit(<span class="st">"cancelSubmit"</span>, <span class="st">"Cancel"</span>,  <span class="kw">this</span>, <span class="st">"onCancelClick"</span>);
 *         form.add(okSubmit);
 *         form.add(cancelSubmit);
 *     }
 *
 *     <span class="kw">public Tree</span> buildTree() {
 *         Tree tree = <span class="kw">new</span> CheckboxTree(<span class="st">"tree"</span>);
 *
 *         <span class="cm">// Build the tree model, by default the root node is not rendered.</span>
 *         <span class="cm">// This can be changed by calling setRootNodeDisplayed(true);</span>
 *         TreeNode root = new TreeNode("c:");
 *         TreeNode dev = <span class="kw">new</span> TreeNode(<span class="st">"dev"</span>,<span class="st">"1"</span>, root);
 *         <span class="kw">new</span> TreeNode(<span class="st">"java.pdf"</span>, <span class="st">"2"</span>, dev);
 *         <span class="kw">new</span> TreeNode(<span class="st">"ruby.pdf"</span>, <span class="st">"3"</span>, dev);
 *
 *         TreeNode programFiles = <span class="kw">new</span> TreeNode(<span class="st">"program files"</span>, <span class="st">"4"</span>, root);
 *         TreeNode adobe = <span class="kw">new</span> TreeNode(<span class="st">"Adobe"</span>, <span class="st">"5"</span>, programFiles);
 *         <span class="cm">// This node is a directory not a file, so setChildrenSupported to true.</span>
 *         adobe.setChildrenSupported(<span class="kw">true</span>);
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
 *         tree.setRootNode(root);
 *         <span class="kw">return</span> tree;
 *     }
 * } </pre>
 *
 * @see Tree
 *
 * @author Bob Schellink
 */
public class CheckboxTree extends Tree {

    // -------------------------------------------------------------- Constants

    /** Client side javascript import. This extends on the functions available in {@link Tree} */
    public static final String HTML_IMPORTS =
            "<script type=\"text/javascript\" src=\"{0}/click/tree/checkbox-tree_{1}.js\"></script>\n";

    /** The Tree resource file names. */
    protected static final String[] TREE_RESOURCES = {
        "/net/sf/click/extras/tree/checkbox-tree.js"
    };

    /** default serial version id. */
    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------- Public Constructors

    /**
     * Create an Tree control for the given name.
     *
     * @param name the tree name
     * @throws IllegalArgumentException if the name is null
     */
    public CheckboxTree(String name) {
        super(name);
        setDecorator(new DecoratorFactory().createDecorator());
    }

    /**
     * Create a Tree with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public CheckboxTree() {
        setDecorator(new DecoratorFactory().createDecorator());
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the HTML head import statements for the CSS stylesheet file:
     * <tt>click/tree/checkbox-tree.js</tt>.
     * <p/>
     * This method calls super.getHtmlImports() to retrieve any imports defined in the
     * super class.
     *
     * @return the HTML head import statements for the control stylesheet
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(256);
        if (isJavascriptEnabled()) {
            buffer.append(ClickUtils.createHtmlImport(HTML_IMPORTS, getContext()));
        }
        buffer.append(super.getHtmlImports());
        return buffer.toString();
    }

    /**
     * Deploy all files defined in the constant <tt>{@link #TREE_RESOURCES}</tt>
     * to the <tt>click/tree</tt> web directory when the application is initialized.
     * <p/>
     * This method calls super.onDeploy() to copy any files defined in the
     * super class.
     *
     * @param servletContext the servlet context
     * @see net.sf.click.Control#onDeploy(ServletContext)
     */
    public void onDeploy(ServletContext servletContext) {
        super.onDeploy(servletContext);
        ClickUtils.deployFiles(servletContext,
                               TREE_RESOURCES,
                               "click/tree",
                               true);
    }

    /**
     * This method binds the users request of selected nodes to the tree's nodes.
     * <p>
     * With html forms, only "checked" checkbox values are submitted
     * to the server. So the request does not supply us the information needed to calculate
     * the nodes to be deselected. To find the nodes to deselect,
     * the newly selected nodes are subtracted from the currently selected nodes. This
     * implies that the tree's model is stored between http requests.
     * <p>
     * Note: to find the collection of selected nodes, the HttpServletRequest is
     * checked against the value of the field {@link #SELECT_TREE_NODE_PARAM}.
     */
    public void bindSelectOrDeselectValues() {
        //find id's of all the new selected node's'
        String[] nodeIds = getRequestValues(SELECT_TREE_NODE_PARAM);

        //find currently selected nodes
        Collection currentlySelected = getSelectedNodes(false);

        //is there any new selected node's
        if (nodeIds == null || nodeIds.length == 0) {
            //deselect all the current selected nodes
            setSelectState(currentlySelected, false);
            return;
        }
        //build hashes of id's for fast lookup
        Set hashes = new HashSet();
        List newSelectedNodes = new ArrayList();
        for (int i = 0; i < nodeIds.length; i++) {
            hashes.add(nodeIds[i]);
        }
        nodeIds = null;

        //build list of newSelectedNodes
        for (Iterator it = iterator(getRootNode()); it.hasNext();) {
            TreeNode result = (TreeNode) it.next();
            if (hashes.contains(result.getId())) {
                newSelectedNodes.add(result);
            }
        }

        //calculate nodes for deselection by removing from currentlySelected nodes
        //those that must be selected.
        currentlySelected.removeAll(newSelectedNodes);

        setSelectState(currentlySelected, false);
        setSelectState(newSelectedNodes, true);
    }

    /**
     * Overridden onProcess() to remove call to {@link #bindSelectOrDeselectValues()}.
     * <p/>
     * For this tree implementation {@link #bindSelectOrDeselectValues()} should
     * only be called once the user submits the form and not on each request.
     *
     * @return true if processing of the page should continue, false otherwise
     */
    public boolean onProcess() {
        bindExpandOrCollapseValues();
        return true;
    }

    //------------------------------------------------------------Inner classes

    /**
     * Demonstrates the usage of a decorator to provide custom tree node
     * rendering.
     */
    protected class DecoratorFactory {

        /**
         * Creates and returns a custom created {@link Decorator}.
         *
         * @return custom defined rendering of the tree node
         */
        protected Decorator createDecorator() {
            return new Decorator() {
                public String render(Object object, Context context) {
                    TreeNode treeNode = (TreeNode) object;
                    HtmlStringBuffer buffer = new HtmlStringBuffer();

                    renderIcon(buffer, treeNode);

                    //TODO IE HACK. Witht a empty span <span></span> IE does not render the
                    //icons. Putting a '&nbsp;' in the span seemed to work. Perhaps there is a
                    //better workaround.
                    buffer.append("&nbsp;");

                    buffer.append("</span>");

                    buffer.append("<input ");
                    if (isJavascriptEnabled()) {
                        ((CheckboxJavascriptRenderer) javascriptHandler.getJavascriptRenderer()).renderCheckbox(buffer);
                    }
                    buffer.append(" style=\"margin:0\" type=\"checkbox\"");
                    buffer.appendAttribute("name", SELECT_TREE_NODE_PARAM);
                    buffer.appendAttribute("value", treeNode.getId());

                    if (treeNode.isSelected()) {
                        buffer.appendAttribute("checked", "checked");
                    }

                    buffer.elementEnd();

                    buffer.elementStart("span");
                    if (treeNode.isSelected()) {
                        buffer.appendAttribute("class", "selected");
                    } else {
                        buffer.appendAttribute("class", "unselected");
                    }
                    if (isJavascriptEnabled()) {
                        ((CheckboxJavascriptRenderer) javascriptHandler.getJavascriptRenderer()).renderSelect(buffer);
                    }
                    buffer.closeTag();

                    renderValue(buffer, treeNode);
                    buffer.elementEnd("span");

                    return buffer.toString();
                }

                /**
                 * Render the node's value.
                 *
                 * @param buffer string buffer containing the markup
                 * @param treeNode treeNode to render
                 */
                protected void renderValue(HtmlStringBuffer buffer, TreeNode treeNode) {

                    if (isJavascriptEnabled()) {
                        //create a href to interact with the checkbox on browser
                        buffer.elementStart("a");
                        buffer.append(" href=\"");
                        buffer.append(getContext().getRequest().getRequestURI());
                        buffer.append("?");
                        buffer.append(SELECT_TREE_NODE_PARAM);
                        buffer.append("=");
                        buffer.append(treeNode.getId());
                        buffer.append("\"");

                        ((CheckboxJavascriptRenderer) javascriptHandler.getJavascriptRenderer()).renderValue(buffer);
                        buffer.closeTag();
                        if (treeNode.getValue() != null) {
                            buffer.append(treeNode.getValue());
                        }
                        buffer.elementEnd("a");
                        buffer.append("\n");
                    } else {
                        //just print normal value
                        if (treeNode.getValue() != null) {
                            buffer.append(treeNode.getValue());
                        }
                        buffer.append("\n");
                    }
                }
            };
        }
    }

    /**
     * <strong>Please note</strong> this interface is only meant for
     * developers of this control, not users.
     * <p/>
     * Provides the contract for pluggable javascript renderers, for
     * the CheckboxTree.
     */
    interface CheckboxJavascriptRenderer {

        /**
         * Called when a tree node's value is rendered. Enables the renderer
         * to add attributes needed by javascript functionality for example
         * something like:
         * <pre class="codeJava">
         *      buffer.append(<span class="st">"onclick=\"handleNodeSelection(this,event);\""</span>);
         * </pre>
         * The code above adds a javascript function call to the element.
         * <p/>
         * The code above is appended to whichever element the
         * tree is currently rendering at the time renderValue
         * is called.
         *
         * @param buffer string buffer containing the markup
         */
        void renderValue(HtmlStringBuffer buffer);

        /**
         * Called when a tree node's checkbox is rendered. Enables the
         * renderer to add attributes needed by javascript functionality
         * for example something like:
         * <pre class="codeJava">
         *     buffer.append(<span class="st">" onclick=\"checkboxClicked(this,event);\""</span>);
         * </pre>
         * The code above adds a javascript function call to the element.
         * <p/>
         * The code above is appended to whichever element the
         * tree is currently rendering at the time renderValue
         * is called.
         *
         * @param buffer string buffer containing the markup
         */
        void renderCheckbox(HtmlStringBuffer buffer);

        /**
         * Called when a tree node's selected state is rendered. Enables
         * the renderer to add attributes needed by javascript functionality
         * for example something like:
         * <pre class="codeJava">
         *     buffer.appendAttribute(<span class="st">"id"</span>, selectId);
         * </pre>
         * The code above adds a javascript function call to the element.
         * <p/>
         * The code above is appended to whichever element the
         * tree is currently rendering at the time renderSelect
         * is called.
         *
         * @param buffer string buffer containing the markup
         */
        void renderSelect(HtmlStringBuffer buffer);
    }

    /**
     * <strong>Please note</strong> this class is only meant for
     * developers of this control, not users.
     * <p/>
     * Provides a base implementation of a CheckboxJavascriptRenderer
     * that subclasses can extend from.
     */
    protected class BaseCheckboxJavascriptRenderer extends AbstractJavascriptRenderer
            implements CheckboxJavascriptRenderer {

        /** holds the id of the select html element. */
        protected String selectId;

        /** holds the javascript call to select the node. */
        protected String nodeSelectionString;

        /** holds the id of the checkbox html element. */
        protected String checkboxId;

        /** holds the javascript call when user clicks on checkbox. */
        protected String checkboxOnClickString;

        /**
         * @see #renderValue(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderValue(HtmlStringBuffer buffer) {
            buffer.append(nodeSelectionString);
        }

        /**
         * @see #renderSelect(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderSelect(HtmlStringBuffer buffer) {
            buffer.appendAttribute("id", selectId);
        }

        /**
         * @see #renderCheckbox(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderCheckbox(HtmlStringBuffer buffer) {
            buffer.append(checkboxOnClickString);
            buffer.appendAttribute("id", checkboxId);
        }

        /**
         * @see #init(TreeNode)
         *
         * @param treeNode the current node rendered
         */
        public void init(TreeNode treeNode) {
            super.init(treeNode);
            selectId = buildString("s_", treeNode.getId(), "");
            checkboxId = buildString("c_", treeNode.getId(), "");

            String tmp = buildString(" onclick=\"handleNodeSelection(this,event,'", selectId, "','");
            nodeSelectionString = buildString(tmp, checkboxId, "'); return false;\"");

            checkboxOnClickString = buildString(" onclick=\"checkboxClicked(this,event,'", selectId, "');\"");
        }
    }

    /**
     * <strong>Please note</strong> this class is only meant for
     * developers of this control, not users.
     * <p/>
     * Provides the rendering needed when a {@link #JAVASCRIPT_SESSION_POLICY}
     * is in effect.
     */
    protected class CheckboxSessionJavascriptRenderer extends SessionRenderer
            implements CheckboxJavascriptRenderer {

        /** A delegate for javascript rendering. */
        protected BaseCheckboxJavascriptRenderer checkboxRenderer = new BaseCheckboxJavascriptRenderer();

        /**
         * @see #renderValue(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderValue(HtmlStringBuffer buffer) {
            checkboxRenderer.renderValue(buffer);
        }

        /**
         * @see #renderSelect(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderSelect(HtmlStringBuffer buffer) {
            checkboxRenderer.renderSelect(buffer);
        }

        /**
         * @see #renderCheckbox(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderCheckbox(HtmlStringBuffer buffer) {
            checkboxRenderer.renderCheckbox(buffer);
        }

        /**
         * @see #init(TreeNode)
         *
         * @param treeNode the current node rendered
         */
        public void init(TreeNode treeNode) {
            super.init(treeNode);
            checkboxRenderer.init(treeNode);
        }
    }

    /**
     * <strong>Please note</strong> this class is only meant for
     * developers of this control, not users.
     * <p/>
     * Provides the rendering needed when a {@link #JAVASCRIPT_COOKIE_POLICY}
     * is in effect.
     */
    protected class CheckboxCookieJavascriptRenderer extends CookieRenderer
            implements CheckboxJavascriptRenderer {

        /** A delegate for javascript rendering. */
        protected BaseCheckboxJavascriptRenderer checkboxRenderer = new BaseCheckboxJavascriptRenderer();

        /**
         * Default constructor.
         *
         * @param expandedCookieName name of the cookie holding expanded id's
         * @param collapsedCookieName name of the cookie holding collapsed id's
         */
        protected CheckboxCookieJavascriptRenderer(String expandedCookieName, String collapsedCookieName) {
            super(expandedCookieName, collapsedCookieName);
        }

        /**
         * @see #renderValue(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderValue(HtmlStringBuffer buffer) {
            checkboxRenderer.renderValue(buffer);
        }

        /**
         * @see #renderSelect(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderSelect(HtmlStringBuffer buffer) {
            checkboxRenderer.renderSelect(buffer);
        }

        /**
         * @see #renderCheckbox(HtmlStringBuffer)
         *
         * @param buffer string buffer containing the markup
         */
        public void renderCheckbox(HtmlStringBuffer buffer) {
            checkboxRenderer.renderCheckbox(buffer);
        }

        /**
         * @see #init(TreeNode)
         *
         * @param treeNode the current node rendered
         */
        public void init(TreeNode treeNode) {
            super.init(treeNode);
            checkboxRenderer.init(treeNode);
        }
    }

    /**
     * <strong>Please note</strong> this class is only meant for
     * developers of this control, not users.
     * <p/>
     * This class implements a session based javascript handler.
     */
    protected class CheckboxSessionHandler extends SessionHandler {

        /**
         * Creates and initializes a new CheckboxSessionHandler.
         *
         * @param context provides access to the http request, and session
         */
        protected CheckboxSessionHandler(Context context) {
            super(context);
        }

        /**
         * @see Tree.JavascriptHandler#getJavascriptRenderer()
         *
         * @return currently installed javascript renderer
         */
        public JavascriptRenderer getJavascriptRenderer() {
            if (javascriptRenderer == null) {
                javascriptRenderer = new CheckboxSessionJavascriptRenderer();
            }
            return javascriptRenderer;
        }
    }

    /**
     * <strong>Please note</strong> this class is only meant for
     * developers of this control, not users.
     * <p/>
     * This class implements a session based javascript handler.
     */
    protected class CheckboxCookieHandler extends CookieHandler {

        /**
         * Creates and initializes a new CookieHandler.
         *
         * @param context provides access to the http request, and session
         */
        protected CheckboxCookieHandler(Context context) {
            super(context);
        }

        /**
         * @see Tree.JavascriptHandler#getJavascriptRenderer()
         *
         * @return currently installed javascript renderer
         */
        public JavascriptRenderer getJavascriptRenderer() {
            if (javascriptRenderer == null) {
                javascriptRenderer = new CheckboxCookieJavascriptRenderer(expandedCookieName, collapsedCookieName);
            }
            return javascriptRenderer;
        }
    }

    /**
     * Creates and return a new JavascriptHandler for the specified
     * tree node. This implementation overrides the super class, to
     * return its own custom JavascriptHandlers.
     *
     * @param javascriptPolicy the current javascript policy
     * @return newly created JavascriptHandler
     */
    protected JavascriptHandler createJavascriptHandler(int javascriptPolicy) {
        if (javascriptPolicy == JAVASCRIPT_SESSION_POLICY) {
            return new CheckboxSessionHandler(getContext());
        } else {
            return new CheckboxCookieHandler(getContext());
        }
    }
}
