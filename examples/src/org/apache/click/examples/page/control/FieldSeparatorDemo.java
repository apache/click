package org.apache.click.examples.page.control;

import org.apache.click.control.*;
import org.apache.click.examples.control.FieldSeparator;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.PageSubmit;
import org.apache.click.util.Bindable;
import org.apache.click.util.ContainerUtils;

/**
 * Provides a form FieldSet usage as Separator example.
 */
public class FieldSeparatorDemo extends BorderPage {
    private static final long serialVersionUID = 1L;

    @Bindable protected Form classicForm = new Form();
    @Bindable protected Form newForm = new Form();

    public FieldSeparatorDemo() {
        // field set based form
        makeFieldSetForm();

        // separator based form
        makeSeparatorForm();
    }

    private void makeFieldSetForm() {
        classicForm.setLabelAlign(Form.ALIGN_RIGHT);
        classicForm.setButtonAlign(Form.ALIGN_RIGHT);
        FieldSet contactFieldSet = new FieldSet("contactDetails");
        classicForm.add(contactFieldSet);
        TextField name = new TextField("name");
        contactFieldSet.add(name);
        EmailField email = new EmailField("email");
        contactFieldSet.add(email);

        FieldSet feedbackFieldSet = new FieldSet("feedbackDetails");
        classicForm.add(feedbackFieldSet);
        TextArea comment = new TextArea("Comment");
        feedbackFieldSet.add(comment);

        Checkbox inform = new Checkbox("inform", "Yes, I agree!");
        feedbackFieldSet.add(inform);
        classicForm.add(new Submit("ok", "  OK  ",  this, "onOkClick1"));
        classicForm.add(new PageSubmit("cancel", HomePage.class));
    }

    private void makeSeparatorForm() {
        newForm.setLabelAlign(Form.ALIGN_RIGHT);
        newForm.setButtonAlign(Form.ALIGN_RIGHT);
        FieldSeparator contactFieldSet = new FieldSeparator("contactDetails");
        newForm.add(contactFieldSet);
        TextField name = new TextField("name");
        newForm.add(name);
        EmailField email = new EmailField("email");
        newForm.add(email);

        FieldSeparator feedbackFieldSet = new FieldSeparator("feedbackDetails");
        newForm.add(feedbackFieldSet);
        TextArea comment = new TextArea("Comment");
        newForm.add(comment);

        Checkbox inform = new Checkbox("inform","Yes, I agree!");
        newForm.add(inform);

        FieldSeparator sepo = new FieldSeparator("separator","");
        newForm.add(sepo);

        newForm.add(new Submit("ok", "  OK  ",  this, "onOkClick2"));
        newForm.add(new PageSubmit("cancel", HomePage.class));
    }

    public boolean onOkClick1(){
        if(classicForm.isValid()){
            for (Field field : ContainerUtils.getInputFields(classicForm)) {
                System.out.println(field.getName() + "=" + field.getValue());
            }            
        }
        return true;
    }

    public boolean onOkClick2(){
        if(newForm.isValid()){
            for (Field field : ContainerUtils.getInputFields(newForm)) {
                System.out.println(field.getName() + "=" + field.getValue());
            }
        }
        return true;
    }

}
