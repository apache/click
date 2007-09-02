package net.sf.click.examples.page.control;

import java.util.Date;

import net.sf.click.control.ActionLink;
import net.sf.click.control.PageLink;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.ExternalLink;

public class LinkDemo extends BorderPage {
    
    public ActionLink actionLink = new ActionLink("ActionLink", this, "onLinkClick");
    public ExternalLink externalLink = new ExternalLink("ExternalLink", "http://www.google.com/search");
    public PageLink pageLink = new PageLink("PageLink", HomePage.class);

    public String clicked;

    public LinkDemo() {
        externalLink.setParameter("q", "Click Framework");
        externalLink.setAttribute("target", "_blank");
        externalLink.setAttribute("class", "external");
    }

    public boolean onLinkClick() {
        clicked = getClass().getName() + ".onLinkClick invoked at " + (new Date());
        return true;
    }

}
