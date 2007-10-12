package net.sf.click.webcode.examples.page.webcode;

import net.sf.click.webcode.examples.page.DecoratorPage;
import net.sf.click.webcode.SyntaxHighlight;
import net.sf.click.control.Form;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 
 */
public class SyntaxHighlightPage extends DecoratorPage {
    public Form form = new Form("form");   

    public void onInit() {        
        SyntaxHighlight sh = new SyntaxHighlight("sh");
        sh.setLabel("");
        sh.setCols(100);
        sh.setRows(20);
        sh.setSyntax("js");

        // get the content of a file to display in this control.
        String realPath = getContext().getServletContext().getRealPath("/click/control.js");
        try {
            String fileContent = FileUtils.readFileToString(new File(realPath),null);
            sh.setValue(fileContent);
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        form.add(sh);
    }
}
