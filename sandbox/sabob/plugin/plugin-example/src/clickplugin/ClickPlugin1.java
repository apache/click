package clickplugin;

import net.sf.click.ClickPlugin;

/**
 *
 * @author Bob Schellink
 */
public class ClickPlugin1 extends ClickPlugin {
    
    public static final String PLUGIN_NAME = "plugin1";

    public ClickPlugin1() {
        System.out.println("CLICK PLUGIN1 <init>");
    }

    public String getPluginName() {
        return PLUGIN_NAME;
    }

    public String getPluginPackage() {
        return "clickplugin";
    }

    public String getPluginPath() {
        return "/plugin1";
    }
}
