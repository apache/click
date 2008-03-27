package clickplugin.page;

import clickplugin.ClickPlugin1;
import net.sf.click.Page;
import net.sf.click.PluginContext;

/**
 *
 * @author Bob Schellink
 */
public class BorderPage extends Page {

    public String getTemplate() {
        return ((PluginContext) getContext()).getPluginRoot(ClickPlugin1.PLUGIN_NAME) + "/my-template.htm";
    }
}
