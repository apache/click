package examples.sandbox.chrisichris.page.control;

import examples.sandbox.chrisichris.control.SearchComposite;
import net.sf.click.sandbox.chrisichris.control.StatePage;

public class ShowCustomer extends StatePage{

    public void onInit() {
        addControl(new SearchComposite("search1"));
        addControl(new SearchComposite("search2"));
    }
}
