package net.sf.click.pages;

import net.sf.click.Page;

public class RedirectToHtm extends Page {
 
    public void onInit() {
        setRedirect("/test.htm");
    }
}
