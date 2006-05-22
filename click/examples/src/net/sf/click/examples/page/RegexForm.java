package net.sf.click.examples.page;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.extras.control.RegexField;

/**
 * Provides a form RegexField example.
 *
 * @author Naoki Takezoe
 */
public class RegexForm extends BorderPage {

    public RegexForm() {
        RegexField versionField = new RegexField("version", "Version", 10);
        versionField.setPattern("[0-9]+\\.[0-9]+\\.[0-9]+");
        versionField.setRequired(true);
        versionField.setTitle("Version number, e.g. '1.2.0'");

        RegexField urlField = new RegexField("url", "URL", 30);
        urlField.setPattern("(http|https)://.+");
        urlField.setRequired(true);
        urlField.setTitle("URL address, e.g. 'http://www.google.com'");

        Form form = new Form("form");
        form.add(versionField);
        form.add(urlField);
        form.add(new Submit("submit", "   OK   "));
        form.add(new Submit("cancel", this, "onCancelClick"));
        addControl(form);
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}
