package net.sf.click.examples.page.menu;

import net.sf.click.examples.page.BorderPage;
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
