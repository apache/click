package net.sf.click.examples.control.html.cssform;

import net.sf.click.examples.control.html.FeedbackBorder;
import javax.servlet.ServletContext;
import net.sf.click.control.BasicForm;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.html.FieldLabel;
import net.sf.click.examples.control.html.list.List;
import net.sf.click.examples.control.html.list.ListItem;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.util.ClickUtils;

/**
 * This page demonstrates how to manually layout a form using Java.
 * 
 * The form is laid out as specified by the article:
 * http://www.sitepoint.com/print/fancy-form-design-css
 *
 * @author Bob Schellink
 */
public class ContactDetailsForm extends BasicForm {

    private List list;

    public ContactDetailsForm(String name) {
        super(name);
    }

    public void onInit() {
        super.onInit();
        buildForm();
    }

    public void buildForm() {
        FieldSet fieldset = new FieldSet();
        fieldset.setLegend("Contact Details");
        list = new List(List.ORDERED_LIST);

        addTextField("name", list).setRequired(true);

        addTextField("email", "Email Address", list);

        addTextField("phone", "Telephone", list).setRequired(true);

        fieldset.add(list);
        add(fieldset);

        fieldset = new FieldSet();
        fieldset.setLegend("Delivery Address");
        list = new List(List.ORDERED_LIST);

        addTextField("address1", list);

        addTextField("address2", list);

        addTextField("suburb", "Suburb/Town", list);

        addTextField("postcode", list, Integer.class).setRequired(true);

        addTextField("country", list);

        fieldset.add(list);
        add(fieldset);

        fieldset = new FieldSet();
        fieldset.setAttribute("class", "submit");
        Submit submit = new Submit("submit", "Begin download");
        fieldset.add(submit);
        add(fieldset);
    }

    public String getHtmlImports() {
        String imports = "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/assets/css/cssform{1}.css\"/>\n";
        return ClickUtils.createHtmlImport(imports, getContext());
    }

    private Field addTextField(String nameStr, List list) {
        return addTextField(nameStr, null, list);
    }
    
    private Field addTextField(String nameStr, List list, Class fieldType) {
        return addTextField(nameStr, null, list, fieldType);
    }
    
    private Field addTextField(String nameStr, String labelStr, List list) {
        return addTextField(nameStr, labelStr, list, String.class);
    }

    private Field addTextField(String nameStr, String labelStr, List list, Class fieldType) {
        ListItem item = new ListItem();
        list.add(item);

        Field field = createField(fieldType);
        field.setName(nameStr);
        field.setAttribute("class", "text");
        FieldLabel label = null;
        if (labelStr != null) {
            label = new FieldLabel(field, labelStr);
        } else {
            label = new FieldLabel(field);
        }
        item.add(label);

        FeedbackBorder border = new FeedbackBorder();
        border.add(field);
        item.add(border);
        return field;
    }

    private Field createField(Class fieldType) {
        if (fieldType == Integer.class) {
            return new IntegerField();
        } else if (fieldType == Double.class) {
            return new DoubleField();
        } else {
            return new TextField();
        }        
    }
}
