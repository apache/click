package clickmodule;

import net.sf.click.ClickModule;

/**
 *
 * @author Bob Schellink
 */
public class ClickModule1 extends ClickModule {
    
    public static final String MODULE_NAME = "module1";

    public ClickModule1() {
        System.out.println("CLICK MODULE <init>");
    }

    public String getModuleName() {
        return MODULE_NAME;
    }

    public String getModulePackage() {
        return "clickmodule";
    }

    public String getModulePath() {
        return "/module1";
    }
}
