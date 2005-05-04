package examples.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.Select.Option;

/**
 * Provides an Select example secure Page.
 *
 * @author Malcolm Edgar
 */
public class SelectDemo extends BorderedPage {

    Form form;
    Select genderSelect;
    Select investmentSelect;
    Select locationSelect;

    public void onInit() {
        form = new Form("form", getContext());
        addControl(form);

        // Gender Select
        genderSelect = new Select("Gender");
        genderSelect.setRequired(true);
        genderSelect.add(new Option("U", ""));
        genderSelect.add(new Option("M", "Male"));
        genderSelect.add(new Option("F", "Female"));
        form.add(genderSelect);
        
        // Investment Select
        List investmentOptions = new ArrayList();
        Select.Option none = new Select.Option("None");
        investmentOptions.add(none);
        
        Select.OptionGroup property = new Select.OptionGroup("Property");
        property.add(new Select.Option("Commerical Property", "Commercial"));
        property.add(new Select.Option("Residential Property", "Residential"));
        investmentOptions.add(property);
        
        Select.OptionGroup securities = new Select.OptionGroup("Securities");
        securities.add(new Select.Option("Bonds"));
        securities.add(new Select.Option("Options"));
        securities.add(new Select.Option("Stocks"));
        investmentOptions.add(securities);
        
        investmentSelect = new Select("Investment");
        investmentSelect.setOptionList(investmentOptions);
        form.add(investmentSelect);
        
        // Location Select
        locationSelect = new Select("Location");
        locationSelect.setRequired(true);
        locationSelect.setMultiple(true);
        locationSelect.setSize(7);
        locationSelect.add("QLD");
        locationSelect.add("NSW");
        locationSelect.add("NT");
        locationSelect.add("SA");
        locationSelect.add("TAS");
        locationSelect.add("VIC");
        locationSelect.add("WA");
        form.add(locationSelect);

        form.add(new Submit("Sumbit"));
    }

    public void onPost() {
        if (form.isValid()) {
            addModel("gender", genderSelect.getValue());
            addModel("investment", investmentSelect.getValue());
            addModel("location", locationSelect.getMultipleValues());
        }
    }
}
