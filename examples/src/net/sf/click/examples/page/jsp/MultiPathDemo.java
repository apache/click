
package net.sf.click.examples.page.jsp;

import net.sf.click.control.ActionLink;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.PageLink;

/**
 *
 * @author Bob Schellink
 */
public class MultiPathDemo extends BorderPage {

    public ActionLink changePath = new ActionLink("changePath", this, "changePath");

    public PageLink defaultPath = new PageLink("defaultPath", MultiPathDemo.class);

    public boolean changePath() {
        setPath("/jsp/dummy.jsp");
        return true;
    }

    public String getTemplate() {
        return "/border-template.jsp";
    }
}
