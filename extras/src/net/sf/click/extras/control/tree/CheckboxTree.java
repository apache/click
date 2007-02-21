/*
 * Copyright 2007 Malcolm A. Edgar
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.click.Context;
import net.sf.click.control.Decorator;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Implementation of a tree control that provides checkboxes to enable selection of nodes. This implementation
 * assumes the tree is wrapped inside a html form. Each time the form is submitted, all checkbox
 * values are processed by this control.
 *
 * <p/>Below is screenshot of how the tree will render in a browser.
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
 * @author Bob Schellink
 */
public class CheckboxTree extends Tree {

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
     * This method binds the users request of selected nodes to the tree's nodes.
     *
     * <p>With html forms, only "checked" checkbox values are submitted
     * to the server. So the request does not supply us the information needed to calculate
     * the nodes to be deselected. To find the nodes to deselect,
     * the newly selected nodes are subtracted from the currently selected nodes. This implies
     * that the tree's model is stored between http requests.
     *
     * <p>Note: to find the collection of selected nodes, the HttpServletRequest is
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
     * Overiden onProcess() to remove call to {@link #bindSelectOrDeselectValues()}.
     *
     * <p/>For this tree implementation {@link #bindSelectOrDeselectValues()} should only be called once the
     * user submits the form and not on each request.
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
    private class DecoratorFactory {

        /**
         * Creates and returns a custom created {@link Decorator}.
         *
         * @return custom defined rendering of the tree node
         */
        private Decorator createDecorator() {
            return new Decorator() {
                public String render(Object object, Context context) {
                    TreeNode treeNode = (TreeNode) object;
                    HtmlStringBuffer buffer = new HtmlStringBuffer();

                    //TODO IE HACK. IE7 displays the tree nodes properly alligned when rendered inside a table.  Without the code below
                    //the icons and node values does not align correctly.
                    //Firefox and Opera displays nicely without this hack. There might be a better way to fix this ;-)
                    //A second IE7 issue solved by the code is when using the new zooming feature, the node value is hidden behind the checkbox.

                    boolean isIE = isIE(context);
                    if (isIE) {
                        buffer.append("<table style=\"line-height:1.3em;margin:0;padding:0;display:inline\" "
                                + "border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");
                    }

                    //render the icon to display
                    buffer.append("<span class=\"");
                    buffer.append(getIconClass(treeNode));
                    buffer.append("\">");

                    //TODO IE HACK. Witht a empty span <span></span> IE does not render the icons. Putting a '&nbsp;' in the span
                    //seemed to work. Perhaps there is a better workaround.
                    buffer.append("&nbsp;");

                    buffer.append("</span>");

                    if (isIE) {
                        buffer.append("</td><td>");
                    }

                    buffer.append("<input onclick=\"checkboxClicked(this, event);\" style=\"margin:0\" type=\"checkbox\"");
                    buffer.appendAttribute("name", SELECT_TREE_NODE_PARAM);
                    buffer.appendAttribute("value", treeNode.getId());
                    if (treeNode.isSelected()) {
                        buffer.appendAttribute("checked", "checked");
                    }

                    buffer.elementEnd();

                    if (isIE) {
                        buffer.append("</td><td>");
                    }

                    buffer.elementStart("span");
                    if (treeNode.isSelected()) {
                        buffer.appendAttribute("class", "selected");
                    } else {
                        buffer.appendAttribute("class", "unselected");
                    }
                    buffer.closeTag();

                    renderValue(buffer, treeNode);
                    buffer.elementEnd("span");

                    if (isIE) {
                        buffer.append("</td></tr></table>");
                    }
                    return buffer.toString();
                }

                /**
                 * Render the node's value.
                 *
                 * @param buffer string buffer containing the markup
                 * @param treeNode treeNode to render
                 */
                protected void renderValue(HtmlStringBuffer buffer, TreeNode treeNode) {
                    if (treeNode.getValue() != null) {
                        buffer.append(treeNode.getValue());
                    }
                    buffer.append("\n");
                }
            };
        }
    }
}
