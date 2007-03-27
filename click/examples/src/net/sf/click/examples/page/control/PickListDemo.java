package net.sf.click.examples.page.control;

import java.util.Set;

import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Submit;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.PickList;

/**
 * Provides an Select example Page.
 *
 * @author Naoki Takezoe
 */
public class PickListDemo extends BorderPage {

    public Form form = new Form();
    public Set selectedValues;

    private PickList pickList = new PickList("languages");

    public PickListDemo() {
        pickList.setHeaderLabel("Languages", "Selected");

        pickList.add(new Option("001", "Java"));
        pickList.add(new Option("002", "Ruby"));
        pickList.add(new Option("003", "Perl"));

        pickList.addSelectedValue("001");

        form.add(pickList);

        form.add(new Submit("ok", " OK ", this, "onOkClick"));
        form.add(new Submit("canel", this, "onCancelClick"));
    }

    public boolean onOkClick() {
        selectedValues = pickList.getSelectedValues();
        return true;
    }

    public boolean onCancelClick() {
        pickList.getSelectedValues().clear();
        return true;
    }

}
