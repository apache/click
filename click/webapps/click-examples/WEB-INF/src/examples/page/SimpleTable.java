package examples.page;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.click.Page;

/**
 * Provides simple Table example which demonstrates Velocity #foreach directive.
 *
 * @author Malcolm Edgar
 */
public class SimpleTable extends BorderedPage {

    /**
     * @see Page#onGet()
     */
    public void onGet() {
        TreeMap properties = new TreeMap();

        Iterator i = System.getProperties().entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            properties.put(entry.getKey(), entry.getValue());
        }

        addModel("properties", properties);
    }
}
