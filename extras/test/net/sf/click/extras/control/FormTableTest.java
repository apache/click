package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.control.Form;

public class FormTableTest extends TestCase {

    /**
     * Check that both Table and Form imports are included.
     * CLK-453
     */
    public void testGetHtmlImports() {
        MockContext.initContext();
        
        // Check imports using an internal Form Control
        FormTable table = new FormTable("table");

        String imports = table.getHtmlImports();
        assertTrue(imports.indexOf("/table.css") > 0);
        assertTrue(imports.indexOf("/control.js") > 0);
        assertTrue(imports.indexOf("/control.css") > 0);


        // Check imports using an external Form Control
        Form form = new Form("form");
        table = new FormTable("table", form);
        form.add(table);

        imports = form.getHtmlImports();
        assertTrue(imports.indexOf("/table.css") > 0);
        assertTrue(imports.indexOf("/control.js") > 0);
        assertTrue(imports.indexOf("/control.css") > 0);
    }
}
