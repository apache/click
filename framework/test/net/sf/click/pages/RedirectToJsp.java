package net.sf.click.pages;

import net.sf.click.Page;

public class RedirectToJsp extends Page {

    public void onInit() {
        setRedirect(JspPage.class);
    }
}
