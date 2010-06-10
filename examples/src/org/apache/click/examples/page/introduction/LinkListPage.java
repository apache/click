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
package org.apache.click.examples.page.introduction;

import java.util.ArrayList;
import java.util.List;
import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides an example on adding a list of ActionLinks to a page.
 */
public class LinkListPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    /* List of ActionLinks. */
    private List links = new ArrayList();

    /** An output message. */
    private String msg;

    public LinkListPage() {
        for (int i = 0; i < 3; i++) {

            ActionLink link = new ActionLink("link" + i);
            link.setActionListener(new ActionListener() {

                public boolean onAction(Control source) {
                    msg = "ControlListenerListPage#" + source.getName()
                        + " object method <tt>onLinkClick()</tt> invoked.";

                    addModel("msg", msg);
                    return true;
                }
            });

            addControl(link);
            links.add(link);
        }

        addModel("links", links);
    }
}
