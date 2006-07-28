package net.sf.click.examples.page;

import net.sf.click.control.FieldSet;
import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.PageSubmit;

/**
 * Provides File Upload example using the FileField control.
 *
 * @author Malcolm Edgar
 */
public class FileUpload extends BorderPage {

    public Form form = new Form();

    private FileField fileField;
    private TextField descField;

    public FileUpload() {
        form.setLabelsPosition("top");

        FieldSet fieldSet = new FieldSet("upload", "<b>Upload File</b>");
        form.add(fieldSet);

        fileField = new FileField("selectFile", "Select File", 40);
        fileField.setRequired(true);
        fieldSet.add(fileField);

        descField = new TextField("description", "File Description", 30);
        fieldSet.add(descField);

        form.add(new Submit("ok", "    OK    ", this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));
    }

    public boolean onOkClick() {
        if (form.isValid()) {
            addModel("fileItem", fileField.getFileItem());
            addModel("fileDesc", descField.getValue());
        }
        return true;
    }

}
 
