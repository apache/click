package examples.page.menu;

import examples.page.BorderPage;
import net.sf.click.extras.control.Menu;

/**
 * Provides a Menu Page.
 *
 * @author Malcolm Edgar
 */
public class MenuPage extends BorderPage {

    public MenuPage() {
        addControl(new Menu("rootMenu"));
    }

}
