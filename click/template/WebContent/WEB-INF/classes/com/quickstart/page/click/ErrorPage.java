package com.quickstart.page.click;

import net.sf.click.extras.control.Menu;

public class ErrorPage extends net.sf.click.util.ErrorPage {
    
    public String title = "Error Page";
    public Menu rootMenu = new Menu();
    
    public void onDestroy() {
        Throwable error = getError();
        
        // TODO: Log error to Log4J or Commons Logger
        getContext().getServletContext().log(error.toString(), error);
    }

}
