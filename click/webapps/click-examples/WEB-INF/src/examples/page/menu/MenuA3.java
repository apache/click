package examples.page.menu;

import net.sf.click.extras.menu.Menu;

public class MenuA3 extends MenuA1 {

    public void onInit() {
        addModel("rootMenu", Menu.getRootMenu(getContext()));
    }

}
