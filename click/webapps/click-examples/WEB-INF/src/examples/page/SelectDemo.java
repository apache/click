package examples.page;

import net.sf.click.Page;
import net.sf.click.control.Form;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.Select.Option;

/**
 * Provides an Select example secure Page.
 *
 * @author Malcolm Edgar
 */
public class SelectDemo extends Page {
    
    Form form;
    Select genderSelect;
    Select stateSelect;
        
    public void onInit() {
        form = new Form("form", getContext());
        addControl(form);
        
        genderSelect = new Select("Gender");
        genderSelect.setRequired(true);
        genderSelect.add(new Option("U", ""));
        genderSelect.add(new Option("M", "Male"));
        genderSelect.add(new Option("F", "Female"));
        form.add(genderSelect);
        
        stateSelect = new Select("Location");
        stateSelect.setRequired(true);
        stateSelect.setMultiple(true);
        stateSelect.setSize(7);
        stateSelect.add("QLD");
        stateSelect.add("NSW");
        stateSelect.add("NT");
        stateSelect.add("SA");
        stateSelect.add("TAS");
        stateSelect.add("VIC");
        stateSelect.add("WA");
        form.add(stateSelect);
        
        form.add(new Submit("Sumbit"));
    }
    
    public void onPost() {        
        if (form.isValid()) {
            addModel("gender", genderSelect.getValue());
            addModel("location", stateSelect.getMultipleValues());
        }
    }
}
