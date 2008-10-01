package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.control.Form;
import org.apache.commons.lang.StringUtils;

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
        assertEquals(1, StringUtils.countMatches(imports, "/table.css"));
        assertEquals(1, StringUtils.countMatches(imports, "/control.js"));
        assertEquals(1, StringUtils.countMatches(imports, "/control.css"));


        // Check imports using an external Form Control
        Form form = new Form("form");
        table = new FormTable("table", form);
        form.add(table);

        imports = form.getHtmlImports();
        assertEquals(1, StringUtils.countMatches(imports, "/table.css"));
        assertEquals(1, StringUtils.countMatches(imports, "/control.js"));
        assertEquals(1, StringUtils.countMatches(imports, "/control.css"));
    }
}
