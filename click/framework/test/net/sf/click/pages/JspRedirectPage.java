package net.sf.click.pages;

public class JspRedirectPage extends RedirectToJsp {

    public void onInit() {
        setJSPRedirect("/test.jsp");
    }

    public void setJSPRedirect(String location) {
        String contextPath = getContext().getRequest().getContextPath();
        location = contextPath + location;
        this.redirect = location;
    }
}
