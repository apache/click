package examples.page;

import java.util.TreeMap;

import net.sf.click.Page;

/**
 * Provides simple Table example which demonstrates Velocity #foreach directive.
 *
 * @author Malcolm Edgar
 */
public class SimpleTable extends BorderPage {

    /**
     * @see Page#onGet()
     */
    public void onGet() {
        addModel("properties", new TreeMap(System.getProperties()));
    }
}
