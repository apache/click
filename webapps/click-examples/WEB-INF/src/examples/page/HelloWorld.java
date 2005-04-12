package examples.page;

import java.util.Date;

import net.sf.click.Page;

/**
 * Provides HelloWorld world example Page. Possibly the simplest dynamic example
 * you can get.
 *
 * @author Malcolm Edgar
 */
public class HelloWorld extends BorderedPage {

    /**
     * @see Page#onGet()
     */
    public void onGet() {
        addModel("time", new Date());
    }
}
