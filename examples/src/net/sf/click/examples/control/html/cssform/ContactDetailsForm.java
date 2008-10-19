package net.sf.click.examples.control.html.cssform;

import net.sf.click.examples.control.html.FeedbackBorder;
import net.sf.click.control.Field;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.html.FieldLabel;
import net.sf.click.examples.control.html.list.HtmlList;
import net.sf.click.examples.control.html.list.ListItem;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.HtmlFieldSet;
import net.sf.click.extras.control.HtmlForm;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * This page demonstrates how to manually layout a form using Java.
 *
 * The form is laid out as specified by the article:
 * http://www.sitepoint.com/print/fancy-form-design-css
 *
 * @author Bob Schellink
 */
public class ContactDetailsForm extends HtmlForm {

    private HtmlList htmlList;

    public ContactDetailsForm(String name) {
        super(name);
    }

    public void onInit() {
        super.onInit();
        buildForm();
    }

    public void buildForm() {
        HtmlFieldSet fieldset = new HtmlFieldSet("contactDetails");
        fieldset.setLegend("Contact Details");
        htmlList = new HtmlList(HtmlList.ORDERED_LIST);

        addTextField("name", htmlList).setRequired(true);

        addTextField("email", "Email Address", htmlList);

        addTextField("phone", "Telephone", htmlList).setRequired(true);

        fieldset.add(htmlList);
        add(fieldset);

        fieldset = new HtmlFieldSet("deliveryAddress");
        fieldset.setLegend("Delivery Address");
        htmlList = new HtmlList(HtmlList.ORDERED_LIST);

        addTextField("address1", htmlList);

        addTextField("address2", htmlList);

        addTextField("suburb", "Suburb/Town", htmlList);

        addTextField("postcode", htmlList, Integer.class).setRequired(true);

        addTextField("country", htmlList);

        fieldset.add(htmlList);
        add(fieldset);

        fieldset = new HtmlFieldSet("buttons");
        // Setting legend to "", draws fieldset border but does not display label
        fieldset.setLegend("");
        fieldset.setAttribute("class", "submit");
        Submit submit = new Submit("submit", "Begin download");
        fieldset.add(submit);
        add(fieldset);
    }

    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(512);

        // Include default imports
        buffer.append(super.getHtmlImports());

        // Include CSS for ContactDetailsForm
        String imports = "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/assets/css/cssform{1}.css\"/>\n";
        buffer.append(ClickUtils.createHtmlImport(imports, getContext()));
        return buffer.toString();
    }

    private Field addTextField(String nameStr, HtmlList htmlList) {
        return addTextField(nameStr, null, htmlList);
    }

    private Field addTextField(String nameStr, HtmlList htmlList, Class fieldType) {
        return addTextField(nameStr, null, htmlList, fieldType);
    }

    private Field addTextField(String nameStr, String labelStr, HtmlList htmlList) {
        return addTextField(nameStr, labelStr, htmlList, String.class);
    }

    private Field addTextField(String nameStr, String labelStr, HtmlList htmlList, Class fieldType) {
        ListItem item = new ListItem();
        htmlList.add(item);

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
