package examples.page.menu;

import net.sf.click.extras.menu.Menu;

/**
 * Provides a Menu Page.
 * 
 * @author Malcolm Edgar
 */
public class MenuB5 extends MenuA1 {

    public void onInit() {
        addModel("rootMenu", Menu.getRootMenu(getContext()));
    }

}
