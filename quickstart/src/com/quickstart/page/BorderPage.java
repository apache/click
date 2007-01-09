package com.quickstart.page;

import com.quickstart.page.user.HomePage;

import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.extras.control.Menu;

public class BorderPage extends Page {

    public ActionLink logoutLink = new ActionLink(this, "onLogoutClick");
    public Menu rootMenu = new Menu();

	/**
	 * @see #getTemplate()
	 */
	public String getTemplate() {
		return "border-template.htm";
	}
    
    /**
     * Handle the logout click event, invalidating the users session and
     * redirect them to the application home page.
     * 
     * @return false
     */
    public boolean onLogoutClick() {
        getContext().getRequest().getSession().invalidate();        
        String path = getContext().getPagePath(HomePage.class);
        setRedirect(path);
        return false;
    }
	
}
