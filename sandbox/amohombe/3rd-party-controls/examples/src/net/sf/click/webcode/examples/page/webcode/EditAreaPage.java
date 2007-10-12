package net.sf.click.webcode.examples.page.webcode;

import net.sf.click.control.Form;
import net.sf.click.webcode.EditArea;
import net.sf.click.webcode.examples.page.DecoratorPage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class EditAreaPage extends DecoratorPage {
    public Form form = new Form("form");

    public void onInit() {
        EditArea editArea = new EditArea("editArea");
        editArea.setLabel("");
        editArea.setStyle("width", "400px");
        editArea.setStyle("height", "400px");
        
        String realPath = getContext().getServletContext().getRealPath("/click/control.css");
        try {
            String fileContent = FileUtils.readFileToString(new File(realPath), null);
            editArea.setValue(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        form.add(editArea);


    }

}
