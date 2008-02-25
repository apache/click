package template.page;

public class LogoutPage extends BorderPage {

    public String remoteUser;
    public String title = "Logout";

    public void onInit() {
        remoteUser = getContext().getRequest().getRemoteUser();
        getContext().getSession().invalidate();
    }

}
