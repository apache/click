package examples.page.menu;

import examples.page.BorderedPage;
import net.sf.click.extras.menu.Menu;

/**
 * Provides a Menu Page.
 *
 * @author Malcolm Edgar
 */
public class MenuA1 extends BorderedPage {

    public MenuA1() {
        addModel("rootMenu", new Menu());
    }

}
