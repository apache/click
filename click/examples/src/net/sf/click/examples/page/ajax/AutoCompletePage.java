package net.sf.click.examples.page.ajax;

import java.util.List;

import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.AutoCompleteTextField;

/**
 * Provides AJAX AutoCompleteTextField example page.
 *
 * @author Malcolm Edgar
 */
public class AutoCompletePage extends BorderPage {

    public Form form = new Form();

    // ------------------------------------------------------------ Constructor

    public AutoCompletePage() {
        FieldSet fieldSet = new FieldSet("Enter a Suburb Location");
        fieldSet.setStyle("background-color", "");
        form.add(fieldSet);

        AutoCompleteTextField postCodeField = new AutoCompleteTextField("postCode") {
            public List getAutoCompleteList(String criteria) {
                return getPostCodeService().getPostCodeLocations(criteria);
            }
        };
        postCodeField.setSize(40);

        fieldSet.add(postCodeField);
    }

}
