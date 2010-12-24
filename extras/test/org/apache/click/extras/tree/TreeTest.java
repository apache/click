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

import junit.framework.*;
import java.util.List;
import org.apache.click.MockContext;
import org.apache.click.control.ActionLink;
import org.apache.click.servlet.MockRequest;

/**
 * Basic Tree tests.
 */
public class TreeTest extends TestCase {
    
    public final static int TREE_NODE_COUNT = 18;

    private Tree tree;
    private String[] testIds;
    private MockContext mockContext;

    @Override
    public void setUp() throws Exception {
        mockContext = MockContext.initContext();
        buildTree();
        buildTestIds();
    }
    
    @Override
    public void tearDown() throws Exception {
        tree = null;
    }

    /**
     * Test expand and collapse of nodes.
     */ 
    public void testExpandAndCollapse() {
        MockRequest mockRequest = mockContext.getMockRequest();
        mockRequest.setParameter(Tree.EXPAND_TREE_NODE_PARAM, testIds);
        mockRequest.setParameter(ActionLink.ACTION_LINK, tree.getExpandLink().getName());

        assertExpandOrCollapse(tree, testIds, false);//test for expansion
        assertExpandOrCollapse(tree, testIds, true);//should reverse expand and collapse
    }

    /**
     * Test selected and deselect of nodes.
     */
    public void testSelectAndDeselect() {
        MockRequest mockRequest = mockContext.getMockRequest();
        mockRequest.setParameter(Tree.SELECT_TREE_NODE_PARAM, testIds);
        mockRequest.setParameter(ActionLink.ACTION_LINK, tree.getSelectLink().getName());

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
        List<TreeNode> result = tree.getSelectedNodes(includeInvisibleNodes);
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
        String[] testIdArray = new String[] {"root", "three", "3.1", "3.1.1"};
        for(int i = 0; i < testIdArray.length; i++) {
            tree.expand(testIdArray[i]);
        }
        tree.collapse("3.1");
        
        //nodeId '3.1' should be invisible because its parent 'three' is not expanded,
        //thus only node 'one' and 'three' should be returned.
        boolean includeInvisibleNodes = false;
        List<TreeNode> result = tree.getExpandedNodes(includeInvisibleNodes);
        
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
        TreeNode node1 = new TreeNode("one", "one");
        node.add(node1);
        node1.add(new TreeNode("1.1", "1.1"));
        node1.add(new TreeNode("1.2", "1.2"));
        node1.add(new TreeNode("2.1", "2.1"));
        TreeNode node3 = new TreeNode("three","three");
        node.add(node3);
        TreeNode node4 = new TreeNode("four", "four");
        node.add(node4);
        TreeNode node31 = new TreeNode("3.1", "3.1");
        node3.add(node31);
        node3.add(new TreeNode("3.2", "3.2"));
        node3.add(new TreeNode("3.3", "3.3"));
        node31.add(new TreeNode("3.1.1", "3.1.1"));
         node31.add(new TreeNode("3.1.2", "3.1.2"));
         node31.add(new TreeNode("3.1.3", "3.1.3"));
        
        TreeNode node41 = new TreeNode("4.1", "4.1");
        node4.add(node41);
        node41.add(new TreeNode("4.1.1", "4.1.1"));
        node41.add(new TreeNode("4.2.1", "4.2.1"));
        
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
        context.executeActionListeners();
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
        context.executeActionListeners();
        for(int i = 0; i < nodeIds.length; i++) {
            TreeNode node = tree.find(nodeIds[i]);
            assertTrue("IsExpanded must be " + !expected,node.isSelected() == !expected);
        }
    }
}