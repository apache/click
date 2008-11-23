package net.sf.click.pages;

import net.sf.click.Page;

/**
 * This page sets its path to a JSP.
 */
public class SetPathToJspPage extends Page {

    public static final String PATH = "dummy.jsp";

    /**
     * Set path to non-existent dummy.jsp
     */
    public void onInit() {
        setPath(PATH);
    }
}
