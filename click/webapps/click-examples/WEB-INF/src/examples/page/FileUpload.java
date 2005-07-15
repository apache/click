package examples.page;

import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;

import org.apache.commons.fileupload.FileItem;

/**
 * Provides File Upload example using the FileField control.
 *
 * @author Malcolm Edgar
 */
public class FileUpload extends BorderedPage {

    Form form;
    FileField fileField;
    TextField descField;

    public void onInit() {
        form = new Form("form", getContext());
        form.setLabelsPosition("top");
        addControl(form);

        fileField = new FileField("Select File");
        fileField.setRequired(true);
        fileField.setSize(40);
        form.add(fileField);

        descField = new TextField("File Description");
        descField.setRequired(true);
        form.add(descField);

        form.add(new Submit("    OK    "));

        Submit cancelButton = new Submit(" Cancel ");
        cancelButton.setListener(this, "onCancelClick");
        form.add(cancelButton);
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }

    public void onPost() {
        System.err.println("here");
        if (form.isValid()) {
            FileItem fileItem = fileField.getFileItem();

            if (fileItem != null) {
                addModel("fileItem", fileItem);
            }

            addModel("fileDesc", descField.getValue());
        }
    }

}
 
