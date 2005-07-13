package examples.page;

import java.util.List;

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
public class UploadPage extends BorderedPage {
    
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
        form.add(descField);
        
        form.add(new Submit("    OK    "));

        Submit cancelButton = new Submit(" Cancel ");
        cancelButton.setListener(this, "onCancelClick");
        form.add(cancelButton);
        
//        setHeader("Content-type", Form.MULTIPART_FORM_DATA);
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }
    
    public void onPost() {
        if (form.isValid()) {
            List fileItems = fileField.getFileItems();
            
            if (!fileItems.isEmpty()) {
                FileItem fileItem = (FileItem) fileItems.get(0);
                addModel("fileItem", fileItem);
            }
        }
    }

}
 