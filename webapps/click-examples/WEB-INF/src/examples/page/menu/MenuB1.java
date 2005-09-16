package examples.page.menu;

import net.sf.click.extras.menu.Menu;

/**
 * Provides a Menu Page.
 *
 * @author Malcolm Edgar
 */
public class MenuB1 extends MenuA1 {

    public void onInit() {
        addModel("rootMenu", Menu.getRootMenu(getContext()));
    }

}
