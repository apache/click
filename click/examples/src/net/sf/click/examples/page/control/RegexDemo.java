package net.sf.click.examples.page.control;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.PageSubmit;
import net.sf.click.extras.control.RegexField;

/**
 * Provides a form RegexField example.
 *
 * @author Naoki Takezoe
 */
public class RegexDemo extends BorderPage {

    public Form form = new Form();

    public RegexDemo() {
        RegexField versionField = new RegexField("version", "Version", 10);
        versionField.setPattern("[0-9]+\\.[0-9]+\\.[0-9]+");
        versionField.setRequired(true);
        versionField.setTitle("Version number, e.g. '1.2.0'");
        form.add(versionField);

        RegexField urlField = new RegexField("url", "URL", 30);
        urlField.setPattern("(http|https)://.+");
        urlField.setRequired(true);
        urlField.setTitle("URL address, e.g. 'http://www.google.com'");
        form.add(urlField);

        form.add(new Submit("submit", "  OK  "));
        form.add(new PageSubmit("cancel", HomePage.class));;
    }

}
