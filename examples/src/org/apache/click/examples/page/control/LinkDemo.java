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
package org.apache.click.examples.page.control;

import java.util.Date;

import org.apache.click.control.ActionLink;
import org.apache.click.control.PageLink;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.ExternalLink;
import org.apache.click.extras.control.PageButton;

/**
 * Provides an ActionLink, ExternalLink and PageLink control examples Page.
 */
public class LinkDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private ActionLink actionLink = new ActionLink("ActionLink", this, "onLinkClick");
    private ActionLink disabledActionLink = new ActionLink("DisabledActionLink", this, "onLinkClick");
    private ActionLink iconActionLink = new ActionLink("IconActionLink", this, "onLinkClick");
    private ActionLink disabledIconActionLink = new ActionLink("DisabledIconActionLink", this, "onLinkClick");

    private PageLink pageLink = new PageLink("PageLink", HomePage.class);
    private PageLink disabledPageLink = new PageLink("DisabledPageLink", HomePage.class);
    private PageLink iconPageLink = new PageLink("IconPageLink",HomePage.class);
    private PageLink disabledIconPageLink = new PageLink("DisabledIconPageLink",HomePage.class);

    private PageButton pageButton = new PageButton("PageButton", HomePage.class);
    private PageButton disabledPageButton = new PageButton("DisabledPageButton", HomePage.class);
    private ExternalLink externalLink = new ExternalLink("ExternalLink", "http://www.google.com/search");
    private ExternalLink disabledExternalLink = new ExternalLink("DisabledExternalLink", "http://www.google.com/search");

    // Constructor ------------------------------------------------------------

    public LinkDemo() {
        addControl(actionLink);
        addControl(disabledActionLink);
        addControl(iconActionLink);
        addControl(disabledIconActionLink);
        addControl(pageLink);
        addControl(disabledPageLink);
        addControl(iconPageLink);
        addControl(disabledIconPageLink);
        addControl(pageButton);
        addControl(disabledPageButton);
        addControl(externalLink);
        addControl(disabledExternalLink);

        iconActionLink.setRenderLabelAndImage(true);
        iconActionLink.addStyleClass("image-link");
        iconActionLink.setImageSrc("/assets/images/table-edit.png");

        disabledIconActionLink.setRenderLabelAndImage(true);
        disabledIconActionLink.addStyleClass("image-link");
        disabledIconActionLink.setImageSrc("/assets/images/table-edit.png");

        iconPageLink.setRenderLabelAndImage(true);
        iconPageLink.addStyleClass("image-link");
        iconPageLink.setImageSrc("/assets/images/home.png");

        disabledIconPageLink.setRenderLabelAndImage(true);
        disabledIconPageLink.addStyleClass("image-link");
        disabledIconPageLink.setImageSrc("/assets/images/home.png");

        externalLink.setParameter("q", "Click Framework");
        externalLink.setAttribute("target", "_blank");
        externalLink.setAttribute("class", "external");

        disabledExternalLink.setParameter("q", "Click Framework");
        disabledExternalLink.setAttribute("target", "_blank");
        disabledExternalLink.setAttribute("class", "external");

        disabledActionLink.setDisabled(true);
        disabledIconActionLink.setDisabled(true);
        disabledIconPageLink.setDisabled(true);
        disabledPageLink.setDisabled(true);
        disabledPageButton.setDisabled(true);
        disabledExternalLink.setDisabled(true);
    }

    // Event Handlers ---------------------------------------------------------

    public boolean onLinkClick() {
        String msg = getClass().getName() + ".onLinkClick invoked at " + (new Date());
        addModel("msg", msg);
        return true;
    }

}
