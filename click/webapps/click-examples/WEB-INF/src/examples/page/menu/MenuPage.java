package examples.page.menu;

import examples.page.BorderedPage;
import net.sf.click.extras.menu.Menu;

/**
 * Provides a Menu Page.
 *
 * @author Malcolm Edgar
 */
public class MenuPage extends BorderedPage {

    public MenuPage() {
        addControl(new Menu("rootMenu"));
    }

}
