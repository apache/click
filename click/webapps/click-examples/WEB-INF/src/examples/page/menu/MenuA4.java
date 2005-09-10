package examples.page.menu;

import net.sf.click.extras.menu.Menu;

public class MenuA4 extends MenuA1 {

    public void onInit() {
        addModel("rootMenu", Menu.getRootMenu(getContext()));
    }

}
