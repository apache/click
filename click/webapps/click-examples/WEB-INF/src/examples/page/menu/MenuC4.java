package examples.page.menu;

import examples.page.BorderedPage;
import net.sf.click.extras.menu.Menu;

/**
 * Provides a Menu Page.
 * 
 * @author Malcolm Edgar
 */
public class MenuC4 extends BorderedPage {

    public void onInit() {
        addModel("rootMenu", Menu.getRootMenu(getContext()));
    }

}
