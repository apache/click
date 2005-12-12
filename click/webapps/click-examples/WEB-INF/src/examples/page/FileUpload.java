package examples.page;

import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;

/**
 * Provides File Upload example using the FileField control.
 *
 * @author Malcolm Edgar
 */
public class FileUpload extends BorderedPage {

    private Form form;
    private FileField fileField;
    private TextField descField;

    public FileUpload() {
        form = new Form("form");
        form.setLabelsPosition("top");
        addControl(form);

        fileField = new FileField("selectFile");
        fileField.setRequired(true);
        fileField.setSize(40);
        form.add(fileField);

        descField = new TextField("description");
        form.add(descField);

        form.add(new Submit("ok", "    OK    ", this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
    }

    public boolean onOkClick() {
        if (form.isValid()) {
            addModel("fileItem", fileField.getFileItem());
            addModel("fileDesc", descField.getValue());
        }
        return true;
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
}
 
