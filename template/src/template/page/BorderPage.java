package template.page;

import org.apache.click.extras.control.Menu;
import org.apache.click.extras.control.MenuFactory;

public class BorderPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private Menu rootMenu;

    public BorderPage() {
        MenuFactory menuFactory = new MenuFactory();
        rootMenu = menuFactory.getRootMenu();
        addControl(rootMenu);
    }

    /**
     * @see #getTemplate()
     */
    public String getTemplate() {
        return "/border-template.htm";
    }

}
