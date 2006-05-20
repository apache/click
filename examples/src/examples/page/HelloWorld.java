package examples.page;

import java.util.Date;

/**
 * Provides HelloWorld world example Page. Possibly the simplest dynamic example
 * you can get.
 *
 * @author Malcolm Edgar
 */
public class HelloWorld extends BorderPage {

    public HelloWorld() {
        addModel("time", new Date());
    }
}
