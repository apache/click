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
package org.apache.click.extras.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.click.Context;
import org.apache.click.control.Decorator;
import org.apache.click.control.Form;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.ContainerUtils;
import org.apache.click.util.HtmlStringBuffer;

import org.apache.commons.lang.ArrayUtils;

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
 * <pre class="prettyprint">
 * public class PlainTreePage extends BorderPage {
 *
 *     protected Submit submit;
 *     protected Submit cancel;
 *     protected Form form;
 *
 *     public PlainTreePage() {
 *         form = new Form("form");
 *         addControl(form);
 *
 *         Tree tree = createTree();
 *         form.add(tree);
 *
 *         submit = new Submit("save", this, "onSubmitClick");
 *         cancel = new Submit("cancel", this, "onCancelClick");
 *
 *         form.add(submit);
 *         form.add(cancel);
 *     }
 *
 *     public Tree createTree() {
 *         Tree tree = new CheckboxTree("tree");
 *
 *         // Build the tree model, by default the root node is not rendered.
 *         // This can be changed by calling setRootNodeDisplayed(true);
 *         TreeNode root = new TreeNode("c:");
 *         TreeNode dev = new TreeNode("dev", "1", root);
 *         new TreeNode("java.pdf", "2", dev);
 *         new TreeNode("ruby.pdf", "3", dev);
 *
 *         TreeNode programFiles = new TreeNode("program files", "4", root);
 *         TreeNode adobe = new TreeNode("Adobe", "5", programFiles);
 *         // This node is a directory not a file, so setChildrenSupported to true.
 *         adobe.setChildrenSupported(true);
 *
 *         TreeNode download = new TreeNode("downloads", "6", root);
 *         TreeNode web = new TreeNode("web", "7", download);
 *         new TreeNode("html.pdf", "8", web);
 *         new TreeNode("css.html", "9", web);
 *
 *         TreeNode databases = new TreeNode("databases", "10", download);
 *         new TreeNode("mysql.html", "11", databases);
 *         new TreeNode("oracle.pdf", "12", databases);
 *         new TreeNode("postgres", "13", databases);
 *
 *         tree.setRootNode(root);
 *         return tree;
 *     }
 * } </pre>
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * In addition to <a href="Tree.html#resources">Tree's resources</a>,
 * the CheckboxTree control makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click/tree</tt>):
 *
 * <ul>
 * <li><tt>click/tree/checkbox-tree.js</tt></li>
 * </ul>
 *
 * To import these Tree files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 *
 * @see Tree
 */
public class CheckboxTree extends Tree {

    // Constants --------------------------------------------------------------

    /** default serial version id. */
    private static final long serialVersionUID = 1L;

    // Private variables ----------------------------------------------------

    /**
     * Determines if the checkboxes of child nodes should also be
     * selected/deselected, when a parent checkbox is selected/deselected.
     */
    private boolean selectChildNodes = false;

   // Public Constructors ----------------------------------------------------

    /**
     * Create an Tree control for the given name.
     *
     * @param name the tree name
     * @throws IllegalArgumentException if the name is null
     */
    public CheckboxTree(String name) {
        super(name);
    }

    /**
     * Create a Tree with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public CheckboxTree() {
    }

    // Public Properties ------------------------------------------------------

    /**
     * Create and set the Tree's decorator that will render a Checkbox for
     * each tree node.
     *
     * @see #createDecorator()
     * @see org.apache.click.Control#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();
        setDecorator(createDecorator());
    }

    /**
     * Returns true if child nodes will also be selected/deselected.
     *
     * @return true if child nodes will be selected, false otherwise
     */
    public boolean isSelectChildNodes() {
        return selectChildNodes;
    }

    /**
     * Sets whether child nodes will also be selected/deselected.
     * <p/>
     * <b>Please note:</b> this feature only works if
     * {@link #setJavascriptEnabled(boolean) JavaScript} support is enabled.
     *
     * @param selectChildNodes determines if child nodes will be
     * selected/deselected
     */
    public void setSelectChildNodes(boolean selectChildNodes) {
        this.selectChildNodes = selectChildNodes;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Return the CheckboxTree HTML HEAD elements for the following resource:
     *
     * <ul>
     * <li><tt>click/tree/checkbox-tree.js</tt></li>
     * </ul>
     *
     * Additionally all the {@link Tree#getHeadElements() Tree import statements}
     * are also returned.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<Element> getHeadElements() {

        if (headElements == null) {
            headElements = super.getHeadElements();

            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            if (isJavascriptEnabled()) {
                headElements.add(new JsImport("/click/tree/checkbox-tree.js",
                    versionIndicator));
            }
        }

        return headElements;
    }

    /**
     * Binds the users request of selected nodes to the tree's nodes.
     * <p/>
     * This method is automatically invoked when the CheckboxTree's parent form
     * is submitted.
     * <p/>
     * See {@link #onFormSubmission()} for more details.
     * <p/>
     * If you do not want CheckboxTree to automatically invoke this method,
     * you can override {@link #onFormSubmission()} to do nothing by default.
     * <p/>
     * Then you must manually invoke this method when the form is submitted.
     * For example:
     *
     * <pre class="prettyprint">
     * public void onInit() {
     *     CheckboxTree tree = new CheckboxTree("tree") {
     *         public void onFormSubmission() {
     *             // Do nothing
     *         }
     *     }
     *     Form form = createForm();
     *     form.add(tree);
     *     Submit submit = new Submit("save");
     *     form.add(submit);
     *     submit.setActionListener(new ActionListener() {
     *         public boolean onAction(Control source) {
     *             tree.bindSelectOrDeselectValues();
     *             return true;
     *         }
     *     });
     *     addControl(form);
     * } </pre>
     */
    @Override
    public void bindSelectOrDeselectValues() {
        // With html forms, only "checked" checkbox values are submitted
        // to the server. So the request does not supply us the information
        // needed to calculate the nodes to be deselected. To find the nodes to
        // deselect, the newly selected nodes are subtracted from the currently
        // selected nodes. This implies that the tree's model is stored between
        // http requests.

        // To find the collection of selected nodes, the HttpServletRequest is
        // checked against the value of the field {@link #SELECT_TREE_NODE_PARAM}.

        // find id's of all the new selected node's'
        String[] nodeIds = getRequestValues(SELECT_TREE_NODE_PARAM);

        // find currently selected nodes
        boolean includeInvisibleNodes = isSelectChildNodes();
        Collection<TreeNode> currentlySelected = getSelectedNodes(includeInvisibleNodes);

        // is there any new selected node's
        if (nodeIds == null || nodeIds.length == 0) {
            // deselect all the current selected nodes
            setSelectState(currentlySelected, false);
            return;
        }
        // build hashes of id's for fast lookup
        Set<String> hashes = new HashSet<String>();
        List<TreeNode> newSelectedNodes = new ArrayList<TreeNode>();
        for (int i = 0; i < nodeIds.length; i++) {
            hashes.add(nodeIds[i]);
        }
        nodeIds = null;

        // build list of newSelectedNodes
        for (Iterator<TreeNode> it = iterator(getRootNode()); it.hasNext();) {
            TreeNode result = it.next();
            if (hashes.contains(result.getId())) {
                newSelectedNodes.add(result);
            }
        }

        // calculate nodes for deselection by removing from currentlySelected nodes
        // those that must be selected.
        currentlySelected.removeAll(newSelectedNodes);

        setSelectState(currentlySelected, false);
        setSelectState(newSelectedNodes, true);
    }

    /**
     * This method binds any expand/collapse changes from the request parameters.
     * <p/>
     * In other words the node id's of expanded and collapsed nodes are
     * retrieved from the request.
     *
     * @see #bindExpandOrCollapseValues()
     */
    @Override
    public void bindRequestValue() {
        bindExpandOrCollapseValues();
    }

    /**
     * This method is invoked when the CheckboxTree parent Form is submitted.
     * <p/>
     * This method delegates to {@link #bindSelectOrDeselectValues()} in order
     * to update the selected and deselected nodes.
     */
    protected void onFormSubmission() {
        bindSelectOrDeselectValues();
    }

    // Inner classes ----------------------------------------------------------

    /**
     * Creates and returns a custom {@link Decorator} that will render a Checkbox
     * for each tree node.
     *
     * @return a decorator that renders a Checkbox for each tree node
     */
    protected Decorator createDecorator() {
        return new Decorator() {

            public String render(Object object, Context context) {
                TreeNode treeNode = (TreeNode) object;
                HtmlStringBuffer buffer = new HtmlStringBuffer();

                renderIcon(buffer, treeNode);

                renderCheckbox(buffer, treeNode);

                buffer.elementStart("span");
                if (treeNode.isSelected()) {
                    buffer.appendAttribute("class", "selected");
                } else {
                    buffer.appendAttribute("class", "unselected");
                }
                if (isJavascriptEnabled()) {
                    ((CheckboxJavascriptRenderer) javascriptHandler.getJavascriptRenderer()).renderSelect(
                        buffer);
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
            protected void renderValue(HtmlStringBuffer buffer,
                TreeNode treeNode) {

                if (isJavascriptEnabled()) {
                    //create a href to interact with the checkbox on browser
                    buffer.elementStart("a");
                    Map<String, String> hrefParameters =
                        Collections.singletonMap(SELECT_TREE_NODE_PARAM,
                                                 treeNode.getId());
                    buffer.appendAttribute("href", getHref(hrefParameters));

                    ((CheckboxJavascriptRenderer) javascriptHandler.getJavascriptRenderer()).renderValue(
                        buffer);
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

    /**
     * Renders a Checkbox for the specified treeNode to the buffer.
     * <p/>
     * This method invokes {@link #getInputType()} which returns <tt>"checkbox"</tt>
     * by default, but allows subclasses to change the input type if necessary.
     *
     * @param buffer string buffer containing the markup
     * @param treeNode treeNode to render
     */
    protected void renderCheckbox(HtmlStringBuffer buffer, TreeNode treeNode) {
        buffer.append("<input ");
        if (isJavascriptEnabled()) {
            ((CheckboxJavascriptRenderer) javascriptHandler.getJavascriptRenderer()).renderCheckbox(
                buffer);
        }
        buffer.append(" style=\"margin:0\" type=\"");
        buffer.append(getInputType());
        buffer.append("\"");
        buffer.appendAttribute("name", SELECT_TREE_NODE_PARAM);
        buffer.appendAttribute("value", treeNode.getId());

        if (treeNode.isSelected()) {
            buffer.appendAttribute("checked", "checked");
        }

        buffer.elementEnd();
    }

    /**
     * Return the input type of the CheckboxTree, default value is
     * <tt>"checkbox"</tt>.
     * <p/>
     * This method allows subclasses to change the input type if necessary.
     * For example in order to render Radio buttons instead of Checkboxes,
     * override this method and return the input type <tt>"radio"</tt>.
     *
     * @return the input type of the CheckboxTree
     */
    protected String getInputType() {
        return "checkbox";
    }

    /**
     * <strong>Please note</strong> this interface is only meant for
     * developers of this control, not users.
     * <p/>
     * Provides the contract for pluggable javascript renderers, for
     * the CheckboxTree.
     */
    protected interface CheckboxJavascriptRenderer {

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
         * for example:
         * <pre class="codeJava">
         *     buffer.append(<span class="st">" onclick=\"onCheckboxClick(this,event);\""</span>);
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
        @Override
        public void init(TreeNode treeNode) {
            super.init(treeNode);
            selectId = buildString("s_", treeNode.getId(), "");
            checkboxId = buildString("c_", treeNode.getId(), "");

            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append(" onclick=\"handleNodeSelection(this, event,'");
            buffer.append(selectId);
            buffer.append("','");
            buffer.append(checkboxId);
            buffer.append("',false); return false;\"");
            nodeSelectionString = buffer.toString();

            buffer = new HtmlStringBuffer();
            buffer.append(" onclick=\"onCheckboxClick(this,event,'");
            buffer.append(selectId);
            buffer.append("',");
            buffer.append(Boolean.toString(isSelectChildNodes()));
            buffer.append(");\"");
            checkboxOnClickString = buffer.toString();
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
        @Override
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
        @Override
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
        private static final long serialVersionUID = 1L;

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
        @Override
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
        private static final long serialVersionUID = 1L;

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
        @Override
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
    @Override
    protected JavascriptHandler createJavascriptHandler(int javascriptPolicy) {
        if (javascriptPolicy == JAVASCRIPT_SESSION_POLICY) {
            return new CheckboxSessionHandler(getContext());
        } else {
            return new CheckboxCookieHandler(getContext());
        }
    }

    // Package Private Methods ------------------------------------------------

    /**
     * Expand / collapse the tree nodes.
     *
     * @return true to continue Page event processing or false otherwise
     */
    @Override
    boolean postProcess() {
        if (isJavascriptEnabled()) {
            javascriptHandler.init(getContext());
        }

        if (!ArrayUtils.isEmpty(expandOrCollapseNodeIds)) {
            expandOrCollapse(expandOrCollapseNodeIds);
        }

        // Try and locate a parent form
        Form form = ContainerUtils.findForm(this);
        if (form != null) {
            // If the form was submitted, invoke bindSelectOrDeselectValues()
            if (form.isFormSubmission()) {
                onFormSubmission();
            }
        }
        return true;
    }
}
