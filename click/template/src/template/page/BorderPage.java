package template.page;

import net.sf.click.extras.control.Menu;

public class BorderPage extends BasePage {

    public Menu rootMenu = Menu.getRootMenu();

    /**
     * @see #getTemplate()
     */
    public String getTemplate() {
        return "border-template.htm";
    }

}
