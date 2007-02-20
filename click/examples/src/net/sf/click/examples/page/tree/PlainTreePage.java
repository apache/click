package net.sf.click.examples.page.tree;

import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.tree.Tree;
import net.sf.click.extras.control.tree.TreeNode;

/**
 * Example usage of the {@link Tree} control.
 *
 * @author Bob Schellink
 */
public class PlainTreePage extends BorderPage {

    public static final String TREE_NODES_SESSION_KEY = "treeNodes";

    protected Tree tree;

    public void onInit() {
        super.onInit();
        tree = buildTree();

        addControl(tree);
    }

    protected Tree createTree() {
        return new Tree("tree");
    }

    protected Tree buildTree() {
        tree = createTree();
        tree.setContext(getContext());

        //by default the root node is not rendered. This can be changed by calling setRootNodeDisplayed(true);
        tree.setRootNodeDisplayed(false);
        loadNodesFromSession();

        if(tree.hasRootNode()) {
            return tree;
        }

        TreeNode root = new TreeNode("c:");

        TreeNode dev = new TreeNode("dev","1", root);
        new TreeNode("java.pdf", "2", dev);
        new TreeNode("ruby.pdf", "3", dev);

        TreeNode programFiles = new TreeNode("program files", "4", root);
        TreeNode adobe = new TreeNode("Adobe", "5", programFiles);
        adobe.setChildrenSupported(true);

        TreeNode download = new TreeNode("downloads","6", root);

        TreeNode web = new TreeNode("web", "7", download);
        new TreeNode("html.pdf", "8", web);
        new TreeNode("css.html", "9", web);

        TreeNode web2 = new TreeNode("web2", "100", web);
        new TreeNode("rdf.pdf", "101", web2);
        new TreeNode("owl.html", "102", web2);

        TreeNode web3 = new TreeNode("web3", "103", web2);
        new TreeNode("machines.pdf", "104", web3);
        new TreeNode("AI.html", "105", web3);

        TreeNode databases = new TreeNode("databases", "10", download);
        new TreeNode("mysql.html","11",databases);
        new TreeNode("oracle.pdf","12",databases);
        new TreeNode("postgres","13",databases);

        tree.setRootNode(root);
        storeNodesInSession();
        return tree;
    }

    private void storeNodesInSession() {
        if(getContext() == null)
            return;
        if(tree.getRootNode() == null)
            return;
        getContext().getSession().setAttribute(TREE_NODES_SESSION_KEY, tree.getRootNode());
    }

    private void loadNodesFromSession() {
        if(getContext() == null)
            return;
        tree.setRootNode((TreeNode) getContext().getSession().getAttribute(TREE_NODES_SESSION_KEY));
    }
}
