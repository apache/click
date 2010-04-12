package template.page;

public class LogoutPage extends BorderPage {

    public String remoteUser;
    public String title = "Logout";

    @Override
    public void onInit() {
        super.onInit();
        remoteUser = getContext().getRequest().getRemoteUser();
        getContext().getSession().invalidate();
    }

}
