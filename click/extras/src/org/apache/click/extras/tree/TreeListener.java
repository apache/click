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

import java.io.Serializable;
import org.apache.click.Context;

/**
 * The listener interface for receiving tree events.
 * <p/>
 * A common use case for this interface is to lazily load
 * the tree as the user is expanding node's, while traversing the tree.
 */
public interface TreeListener extends Serializable {

    /**
     * Invoked when user selected the specified node.
     *
     * @param tree tree the operation was made on
     * @param node node that was selected
     * @param context provides access to {@link org.apache.click.Context}
     * @param previousState contains the previous selected state
     */
    public void nodeSelected(Tree tree, TreeNode node, Context context, boolean previousState);

    /**
     * Invoked when user deselected the specified node.
     *
     * @param tree tree the operation was made on
     * @param node node that was deselected
     * @param context provides access to {@link org.apache.click.Context}
     * @param previousState contains the previous selected state
     */
    public void nodeDeselected(Tree tree, TreeNode node, Context context, boolean previousState);

    /**
     * Invoked when user expanded the specified node.
     *
     * @param tree tree the operation was made on
     * @param node node that was expanded
     * @param context provides access to {@link org.apache.click.Context}
     * @param previousState contains the previous expanded state
     */
    public void nodeExpanded(Tree tree, TreeNode node, Context context, boolean previousState);

    /**
     * Invoked when user collapsed the specified node.
     *
     * @param tree tree the operation was made on
     * @param node node that was collapsed
     * @param context provides access to {@link org.apache.click.Context}
     * @param previousState contains the previous expanded state
     */
    public void nodeCollapsed(Tree tree, TreeNode node, Context context, boolean previousState);
}
