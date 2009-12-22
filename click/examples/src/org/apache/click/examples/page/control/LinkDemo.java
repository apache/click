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
 *
 * @author Malcolm Edgar
 */
public class LinkDemo extends BorderPage {

    @Bindable public ActionLink actionLink = new ActionLink("ActionLink", this, "onLinkClick");
    @Bindable public ActionLink disabledActionLink = new ActionLink("DisabledActionLink", this, "onLinkClick");
    @Bindable public ActionLink iconActionLink = new ActionLink("IconActionLink", this, "onLinkClick");

    @Bindable public PageLink pageLink = new PageLink("PageLink", HomePage.class);
    @Bindable public PageLink disabledPageLink = new PageLink("DisabledPageLink", HomePage.class);
    @Bindable public PageLink iconPageLink = new PageLink("IconPageLink",HomePage.class);

    @Bindable public ExternalLink externalLink = new ExternalLink("ExternalLink", "http://www.google.com/search");
    @Bindable public PageButton pageButton = new PageButton("PageButton", HomePage.class);
    @Bindable public PageButton disabledPageButton = new PageButton("DisabledPageButton", HomePage.class);

    @Bindable public String clicked;

    public LinkDemo() {
        iconActionLink.setRenderLabelAndImage(true);
        iconActionLink.addStyleClass("image-link");
        iconActionLink.setImageSrc("/assets/images/table-edit.png");
        iconPageLink.setRenderLabelAndImage(true);
        iconPageLink.addStyleClass("image-link");
        iconPageLink.setImageSrc("/assets/images/home.png");

        externalLink.setParameter("q", "Click Framework");
        externalLink.setAttribute("target", "_blank");
        externalLink.setAttribute("class", "external");
        disabledActionLink.setDisabled(true);
        disabledPageLink.setDisabled(true);
        disabledPageButton.setDisabled(true);
    }

    public boolean onLinkClick() {
        clicked = getClass().getName() + ".onLinkClick invoked at " + (new Date());
        return true;
    }

}
