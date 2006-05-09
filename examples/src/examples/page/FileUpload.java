package examples.page;

import net.sf.click.control.FieldSet;
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

        FieldSet fieldSet = new FieldSet("upload", "<b>Upload File</b>");
        form.add(fieldSet);

        fileField = new FileField("selectFile", "Select File", 40);
        fileField.setRequired(true);
        fieldSet.add(fileField);

        descField = new TextField("description", "File Description", 30);
        fieldSet.add(descField);

        form.add(new Submit("ok", "    OK    ", this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));

        addControl(form);
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
 
