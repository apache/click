package examples.page.menu;

import examples.page.BorderedPage;
import net.sf.click.extras.menu.Menu;

public class MenuPage extends BorderedPage {

    public void onInit() {
        Menu menu = Menu.getRootMenu(getContext());
        addModel("rootMenu", menu);
    }

}
