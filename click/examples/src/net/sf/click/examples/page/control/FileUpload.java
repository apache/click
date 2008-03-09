package net.sf.click.examples.page.control;

import net.sf.click.control.FieldSet;
import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.PageSubmit;

/**
 * Provides File Upload example using the FileField control.
 *
 * @author Malcolm Edgar
 */
public class FileUpload extends BorderPage {

    public Form form = new Form();

    private FileField fileField1;
    private TextField descField1;

    private FileField fileField2;
    private TextField descField2;

    public FileUpload() {
        form.setLabelsPosition("top");

        FieldSet fieldSet1 = new FieldSet("upload1", "<b>Upload File 1</b>");
        form.add(fieldSet1);

        fileField1 = new FileField("selectFile1", "Select File 1", 40);
        fileField1.setRequired(true);
        fieldSet1.add(fileField1);

        descField1 = new TextField("description1", "File Description 1", 30);
        descField1.setRequired(true);
        fieldSet1.add(descField1);

        FieldSet fieldSet2 = new FieldSet("upload2", "<b>Upload File 2</b>");
        form.add(fieldSet2);

        fileField2 = new FileField("selectFile2", "Select File 2", 40);
        fileField2.setRequired(true);
        fieldSet2.add(fileField2);

        descField2 = new TextField("description2", "File Description 2", 30);
        descField2.setRequired(true);
        fieldSet2.add(descField2);

        form.add(new Submit("ok", "  OK  ", this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));
    }

    public boolean onOkClick() {

        if (form.isValid()) {
            if (fileField1.getFileItem() != null) {
                addModel("fileItem1", fileField1.getFileItem());
            }
            addModel("fileDesc1", descField1.getValue());

            if (fileField2.getFileItem() != null) {
                addModel("fileItem2", fileField2.getFileItem());
            }
            addModel("fileDesc2", descField2.getValue());
        }
        return true;
    }

}

