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
import org.apache.click.util.Bindable;

/**
 * Provides an ActionLink, ExternalLink and PageLink control examples Page.
 */
public class LinkDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Bindable protected ActionLink actionLink = new ActionLink("ActionLink", this, "onLinkClick");
    @Bindable protected ActionLink disabledActionLink = new ActionLink("DisabledActionLink", this, "onLinkClick");
    @Bindable protected ActionLink iconActionLink = new ActionLink("IconActionLink", this, "onLinkClick");
    @Bindable protected ActionLink disabledIconActionLink = new ActionLink("DisabledIconActionLink", this, "onLinkClick");

    @Bindable protected PageLink pageLink = new PageLink("PageLink", HomePage.class);
    @Bindable protected PageLink disabledPageLink = new PageLink("DisabledPageLink", HomePage.class);
    @Bindable protected PageLink iconPageLink = new PageLink("IconPageLink",HomePage.class);
    @Bindable protected PageLink disabledIconPageLink = new PageLink("DisabledIconPageLink",HomePage.class);

    @Bindable protected PageButton pageButton = new PageButton("PageButton", HomePage.class);
    @Bindable protected PageButton disabledPageButton = new PageButton("DisabledPageButton", HomePage.class);
    @Bindable protected ExternalLink externalLink = new ExternalLink("ExternalLink", "http://www.google.com/search");
    @Bindable protected ExternalLink disabledExternalLink = new ExternalLink("DisabledExternalLink", "http://www.google.com/search");

    @Bindable protected String clicked;

    // Constructor ------------------------------------------------------------

    public LinkDemo() {
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
        clicked = getClass().getName() + ".onLinkClick invoked at " + (new Date());
        return true;
    }

}
