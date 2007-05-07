package net.sf.click.examples.page.introduction;

import java.util.Date;

import net.sf.click.examples.page.BorderPage;

/**
 * Provides HelloWorld world example Page. Possibly the simplest dynamic example
 * you can get.
 * <p/>
 * Note the public scope time Date field is automatically added to the page's
 * model as a value named "time".
 *
 * @author Malcolm Edgar
 */
public class HelloWorld extends BorderPage {

    public Date time = new Date();

}
