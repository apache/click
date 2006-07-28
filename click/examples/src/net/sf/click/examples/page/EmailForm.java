package net.sf.click.examples.page;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.control.RichTextArea;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.PageSubmit;

/**
 * Provides an example page using the custom RichTextArea control.
 *
 * @see RichTextArea
 *
 * @author Malcolm Edgar
 */
public class EmailForm extends BorderPage {
    
    public Form form = new Form();

    public EmailForm() {
        form.setLabelsPosition(Form.POSITION_TOP);
        form.setLabelRequiredPrefix("<b>");
        form.setLabelRequiredSuffix("</b>");
        form.setErrorsPosition(Form.POSITION_TOP);

        EmailField addressField = new EmailField("address", "To:");
        addressField.setRequired(true);
        addressField.setSize(60);
        form.add(addressField);

        TextField subjectField = new TextField("subject", "Subject:");
        subjectField.setRequired(true);
        subjectField.setSize(60);
        form.add(subjectField);

        RichTextArea messageTextArea = new RichTextArea("message");
        messageTextArea.setLabel("Message:");
        messageTextArea.setCols(45);
        messageTextArea.setRows(8);
        form.add(messageTextArea);

        form.add(new Submit("send", "  Send "));
        form.add(new PageSubmit("cancel", HomePage.class));
    }

}
