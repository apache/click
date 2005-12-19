package examples.page;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.extras.control.RegexField;

/**
 * Provides a form RegexField example.
 *
 * @author Naoki Takezoe
 */
public class RegexForm extends BorderedPage {

    public RegexForm() {
        RegexField version = new RegexField("version");
        version.setPattern("[0-9]+\\.[0-9]+\\.[0-9]+");
        version.setRequired(true);

        RegexField url = new RegexField("url", "URL");
        url.setPattern("(http|https)://.+");
        url.setRequired(true);

        Form form = new Form("form");
        form.add(version);
        form.add(url);
        form.add(new Submit("submit", "   OK   "));
        form.add(new Submit("cancel", this, "onCancelClick"));
        addControl(form);
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}
