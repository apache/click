package net.sf.click.examples.page;

import java.util.TreeMap;

import net.sf.click.Page;

/**
 * Provides simple Table example which demonstrates Velocity #foreach directive.
 *
 * @author Malcolm Edgar
 */
public class SimpleTable extends BorderPage {

    /**
     * @see Page#onRender()
     */
    public void onRender() {
        addModel("properties", new TreeMap(System.getProperties()));
    }
}
