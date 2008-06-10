package clickmodule.page;

import clickmodule.ClickModule1;
import net.sf.click.Page;
import net.sf.click.ModuleContext;

/**
 *
 * @author Bob Schellink
 */
public class BorderPage extends Page {

    public String getTemplate() {
        return ((ModuleContext) getContext()).getModuleRoot(ClickModule1.MODULE_NAME) + "/my-template.htm";
    }
}
