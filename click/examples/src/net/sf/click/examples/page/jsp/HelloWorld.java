package net.sf.click.examples.page.jsp;

import java.util.Date;

import net.sf.click.Page;

/**
 * Provides HelloWorld world example Page. Possibly the simplest dynamic example
 * you can get.
 * <p/>
 * Note the public scope time Date field is automatically added to the page's
 * model as a value named "time".
 *
 * @author Malcolm Edgar
 */
public class HelloWorld extends Page {

    public Date time = new Date();

}
