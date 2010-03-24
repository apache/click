package template.page;

import org.apache.click.extras.control.Menu;
import org.apache.click.extras.control.MenuFactory;

public class BorderPage extends BasePage {

    private static final long serialVersionUID = 1L;

    public Menu rootMenu;

    public BorderPage() {
        MenuFactory menuFactory = new MenuFactory();
        rootMenu = menuFactory.getRootMenu();
    }

    /**
     * @see #getTemplate()
     */
    public String getTemplate() {
        return "border-template.htm";
    }

}
