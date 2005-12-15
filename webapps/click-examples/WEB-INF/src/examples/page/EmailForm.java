package examples.page;

import net.sf.click.control.EmailField;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import examples.control.RichTextArea;

/**
 * Provides an example page using the custom {@link RichTextArea} control.
 *
 * @author Malcolm Edgar
 */
public class EmailForm extends BorderedPage {

    public EmailForm() {
        Form form = new Form("form");
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
        form.add(new Submit("canel", this, "onCancelClick"));

        addControl(form);
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }

}
