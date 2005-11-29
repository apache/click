package examples.page;

import net.sf.click.control.EmailField;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import examples.control.RichTextArea;

public class EmailForm extends BorderedPage {

    public EmailForm() {
        Form form = new Form("form");
        form.setLabelsPosition(Form.POSITION_TOP);
        addControl(form);

        EmailField addressField = new EmailField("address", "To:");
        addressField.setRequired(true);
        form.add(addressField);

        TextField subjectField = new TextField("subject", "Subject:");
        subjectField.setRequired(true);
        subjectField.setSize(30);
        form.add(subjectField);

        RichTextArea messageTextArea = new RichTextArea("message");
        messageTextArea.setLabel("Message:");
        messageTextArea.setCols(50);
        messageTextArea.setRows(10);
        messageTextArea.setRequired(true);
        form.add(messageTextArea);

        form.add(new Submit("send", "  Send "));
        form.add(new Submit("canel", this, "onCancelClick"));
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }

}
