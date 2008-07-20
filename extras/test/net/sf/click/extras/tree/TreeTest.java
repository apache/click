package net.sf.click.extras.tree;

import junit.framework.*;
import java.util.List;
import net.sf.click.MockContext;
import net.sf.click.servlet.MockRequest;

/**
 *
 * @author Bob Schellink
 */
public class TreeTest extends TestCase {
    
    public final static int TREE_NODE_COUNT = 18;

    private Tree tree;
    private String[] testIds;
    private MockContext mockContext;

    public void setUp() throws Exception {
        mockContext = MockContext.initContext();
        buildTree();
        buildTestIds();
    }
    
    public void tearDown() throws Exception {
        tree = null;
    }

    /**
     * Test expand and collapse of nodes.
     */ 
    public void testExpandAndCollapse() {
        MockRequest mockRequest = mockContext.getMockRequest();
        mockRequest.setParameter(Tree.EXPAND_TREE_NODE_PARAM, testIds);

        assertExpandOrCollapse(tree, testIds, false);//test for expansion
        assertExpandOrCollapse(tree, testIds, true);//should reverse expand and collapse
    }

    /**
     * Test selected and deselect of nodes.
     */
    public void testSelectAndDeselect() {
        MockRequest mockRequest = mockContext.getMockRequest();
        mockRequest.setParameter(Tree.SELECT_TREE_NODE_PARAM, testIds);

        assertSelectOrDeselect(tree, testIds, false);//test for selection
        assertSelectOrDeselect(tree, testIds, true);//should reverse selection and deselect
    }

    /**
     * Test that getSelectedNodes return only visible nodes.
     */
    public void testSelectOnlyVisibleNodes() {
        // ensure the testIds are selected
        for(int i = 0; i < testIds.length; i++){
            String id = testIds[i];
            tree.select(id);
        }
        
        //nodeId '3.1' should be invisible because its parent 'three' is not expanded,
        //thus only node 'one' and 'three' should be returned.
        boolean includeInvisibleNodes = false;
        List result = tree.getSelectedNodes(includeInvisibleNodes);
        assertEquals(2, result.size());
        
        //now check if all values are selected
        includeInvisibleNodes = true;
        result = tree.getSelectedNodes(includeInvisibleNodes);
        assertEquals(3, result.size());
    }
    
    /**
     * Test that getExpandedNodes return only visible nodes.
     */
    public void testGetExpandedNodes() {
        //ensure the testIds are expanded
        String[] testIds = new String[] {"root", "three", "3.1", "3.1.1"};
        for(int i = 0; i < testIds.length; i++) {
            tree.expand(testIds[i]);
        }
        tree.collapse("3.1");
        
        //nodeId '3.1' should be invisible because its parent 'three' is not expanded,
        //thus only node 'one' and 'three' should be returned.
        boolean includeInvisibleNodes = false;
        List result = tree.getExpandedNodes(includeInvisibleNodes);
        
        //rootnode is expanded by default.
        //Result below should be 2 because 3.1 is collapsed hiding 3.1.1 which is expanded
        //but skipped, since includeInvisibleNodes is false.
        assertTrue("Expected 2. Found " + result.size(), result.size() == 2);
        
        //now check if all values are expanded
        includeInvisibleNodes = true;
        tree.expand("3.1");
        result = tree.getExpandedNodes(includeInvisibleNodes);
        assertTrue("Expected 4. Found " + result.size(), result.size() == 4);
    }

    // ------------------------------------------------------ Protected Methods

    protected Tree createTree() {
        return new Tree("tree");
    }

    protected Tree buildTree() {
        tree = createTree();
        if(tree.hasRootNode()) {
            return tree;
        }
        TreeNode node = new TreeNode("root", "root");
        TreeNode node1 = new TreeNode("one", "one", node);
        new TreeNode("1.1", "1.1", node1);
        new TreeNode("1.2", "1.2", node1);
        new TreeNode("2.1", "2.1", node1);
        TreeNode node3 = new TreeNode("three","three", node);
        TreeNode node4 = new TreeNode("four", "four", node);
        TreeNode node31 = new TreeNode("3.1", "3.1", node3);
        new TreeNode("3.2", "3.2", node3);
        new TreeNode("3.3", "3.3", node3);
        new TreeNode("3.1.1", "3.1.1", node31);
        new TreeNode("3.1.2", "3.1.2", node31);
        new TreeNode("3.1.3", "3.1.3", node31);
        
        TreeNode node41 = new TreeNode("4.1", "4.1", node4);
        new TreeNode("4.1.1", "4.1.1", node41);
        new TreeNode("4.2.1", "4.2.1", node41);
        
        tree.setRootNode(node);
        tree.expand(node);
        return tree;
    }

    // -------------------------------------------------------- Private Methods

    private void buildTestIds() {
        testIds = new String[] {
            "one",
            "three",
            "3.1"};
    }
    
    private void assertExpandOrCollapse(Tree tree, String[] nodeIds, boolean expected) {
        MockContext context = (MockContext) MockContext.getThreadLocalContext();
        for(int i = 0; i < nodeIds.length; i++) {
            TreeNode node = tree.find(nodeIds[i]);
            assertTrue("IsExpanded must be " + expected,node.isExpanded() == expected);
        }
        tree.onProcess();
        context.fireActionEventsAndClearRegistry();
        for(int i = 0; i < nodeIds.length; i++) {
            TreeNode node = tree.find(nodeIds[i]);
            assertTrue("IsExpanded must be " + !expected,node.isExpanded() == !expected);
        }
    }

    private void assertSelectOrDeselect(Tree tree, String[] nodeIds, boolean expected) {
        MockContext context = (MockContext) MockContext.getThreadLocalContext();
        for(int i = 0; i < nodeIds.length; i++) {
            TreeNode node = tree.find(nodeIds[i]);
            assertTrue("IsExpanded must be " + expected,node.isSelected() == expected);
        }
        tree.onProcess();
        context.fireActionEventsAndClearRegistry();
        for(int i = 0; i < nodeIds.length; i++) {
            TreeNode node = tree.find(nodeIds[i]);
            assertTrue("IsExpanded must be " + !expected,node.isSelected() == !expected);
        }
    }
}