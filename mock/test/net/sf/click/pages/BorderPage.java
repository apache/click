package net.sf.click.pages;

import net.sf.click.Page;

/**
 * A border page for mock testing.
 */
public class BorderPage extends Page {
    
    /**
     * Return border template path.
     *
     * @return border template path
     */
    public String getTemplate() {
        return "border-template.htm";
    }
}
