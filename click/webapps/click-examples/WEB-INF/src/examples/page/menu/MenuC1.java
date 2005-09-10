package examples.page.menu;

import examples.page.BorderedPage;
import net.sf.click.extras.menu.Menu;

public class MenuC1 extends BorderedPage {

    public void onInit() {
        addModel("rootMenu", Menu.getRootMenu(getContext()));
    }

}
