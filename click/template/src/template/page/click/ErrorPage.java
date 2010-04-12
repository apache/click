package template.page.click;

import org.apache.click.extras.control.Menu;
import org.apache.click.extras.control.MenuFactory;

public class ErrorPage extends org.apache.click.util.ErrorPage {

    public String title = "Error Page";

    private Menu rootMenu;

    public ErrorPage() {
        MenuFactory menuFactory = new MenuFactory();
        rootMenu = menuFactory.getRootMenu();
        addControl(rootMenu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Throwable error = getError();

        // TODO: Log error to Log4J or Commons Logger
        getContext().getServletContext().log(error.toString(), error);
    }

}
