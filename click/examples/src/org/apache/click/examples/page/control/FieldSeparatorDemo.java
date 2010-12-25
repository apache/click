package org.apache.click.examples.page.control;

import org.apache.click.control.*;
import org.apache.click.examples.control.FieldSeparator;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.PageSubmit;
import org.apache.click.util.ContainerUtils;

/**
 * Provides a FieldSeparator usage example vs the classical FieldSet usage.
 *
 * @see org.apache.click.examples.control.FieldSeparator
 * @see org.apache.click.control.FieldSet
 */
public class FieldSeparatorDemo extends BorderPage {
    private static final long serialVersionUID = 1L;

    private Form newForm = new Form("newForm");
    private Form classicForm = new Form("classicForm");

    public FieldSeparatorDemo() {
        // a separator based form
        makeSeparatorForm();

        // a field set based form
        makeFieldSetForm();
    }

    private void makeSeparatorForm() {
        addControl(newForm);
        addControl(classicForm);

        newForm.setLabelAlign(Form.ALIGN_RIGHT);
        newForm.setButtonAlign(Form.ALIGN_RIGHT);
        FieldSeparator contactSeparator = new FieldSeparator("contactDetails");
        newForm.add(contactSeparator); // unlike the FieldSet, we just add the separator to the Form
        TextField name = new TextField("name", "Name", 30);
        newForm.add(name); // without adding the fields to it.
        EmailField email = new EmailField("email");
        newForm.add(email);

        FieldSeparator feedbackSeparator = new FieldSeparator("feedbackDetails");
        newForm.add(feedbackSeparator);
        TextArea comment = new TextArea("Comment");
        newForm.add(comment);

        Checkbox inform = new Checkbox("inform","Yes, I agree!");
        newForm.add(inform);

        // Separator without a "legend": much like a HR element but with a consistent style with the other separators.
        FieldSeparator separator = new FieldSeparator("separator","");
        newForm.add(separator);

        newForm.add(new Submit("ok", "  OK  ",  this, "onOkClick1"));
        newForm.add(new PageSubmit("cancel", HomePage.class));
    }

    private void makeFieldSetForm() {
        classicForm.setLabelAlign(Form.ALIGN_RIGHT);
        classicForm.setButtonAlign(Form.ALIGN_RIGHT);
        FieldSet contactFieldSet = new FieldSet("contactDetails");
        classicForm.add(contactFieldSet);
        TextField name = new TextField("name", "Name", 30);
        contactFieldSet.add(name);
        EmailField email = new EmailField("email");
        contactFieldSet.add(email);

        FieldSet feedbackFieldSet = new FieldSet("feedbackDetails");
        classicForm.add(feedbackFieldSet);
        TextArea comment = new TextArea("Comment");
        feedbackFieldSet.add(comment);

        Checkbox inform = new Checkbox("inform", "Yes, I agree!");
        feedbackFieldSet.add(inform);
        classicForm.add(new Submit("ok", "  OK  ",  this, "onOkClick2"));
        classicForm.add(new PageSubmit("cancel", HomePage.class));
    }

    public boolean onOkClick1(){
        if(newForm.isValid()){
            for (Field field : ContainerUtils.getInputFields(newForm)) {
                System.out.println(field.getName() + "=" + field.getValue());
            }
        }
        return true;
    }

    public boolean onOkClick2(){
        if(classicForm.isValid()){
            for (Field field : ContainerUtils.getInputFields(classicForm)) {
                System.out.println(field.getName() + "=" + field.getValue());
            }
        }
        return true;
    }

}
