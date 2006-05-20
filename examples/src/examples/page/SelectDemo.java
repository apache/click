package examples.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.OptionGroup;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;

/**
 * Provides an Select example secure Page.
 *
 * @author Malcolm Edgar
 */
public class SelectDemo extends BorderPage {

    private Form form;
    private Select genderSelect;
    private Select investmentSelect;
    private Select locationSelect;

    public SelectDemo() {
        form = new Form("form");
        form.setErrorsPosition(Form.POSITION_TOP);
        addControl(form);

        // Gender Select
        genderSelect = new Select("gender");
        genderSelect.setRequired(true);
        genderSelect.add(new Option("U", ""));
        genderSelect.add(new Option("M", "Male"));
        genderSelect.add(new Option("F", "Female"));
        form.add(genderSelect);

        // Investment Select
        List investmentOptions = new ArrayList();

        OptionGroup property = new OptionGroup("property");
        property.add(new Option("Commerical Property", "Commercial"));
        property.add(new Option("Residential Property", "Residential"));
        investmentOptions.add(property);

        OptionGroup securities = new OptionGroup("securities");
        securities.add(new Option("Bonds"));
        securities.add(new Option("Options"));
        securities.add(new Option("Stocks"));
        investmentOptions.add(securities);

        investmentSelect = new Select("investment");
        investmentSelect.setOptionList(investmentOptions);
        investmentSelect.setMultiple(true);
        investmentSelect.setRequired(true);
        investmentSelect.setSize(7);
        form.add(investmentSelect);

        // Location Select
        locationSelect = new Select("location");
        locationSelect.add("QLD");
        locationSelect.add("NSW");
        locationSelect.add("NT");
        locationSelect.add("SA");
        locationSelect.add("TAS");
        locationSelect.add("VIC");
        locationSelect.add("WA");
        form.add(locationSelect);

        form.add(new Submit("ok", "   OK   "));
        form.add(new Submit("canel", this, "onCancelClick"));
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }

    /**
     * @see net.sf.click.Page#onPost()
     */
    public void onPost() {
        if (form.isValid()) {
            addModel("gender", genderSelect.getValue());
            addModel("investment", investmentSelect.getMultipleValues());
            addModel("location", locationSelect.getValue());
        }
    }
}
