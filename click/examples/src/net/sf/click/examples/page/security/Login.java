package net.sf.click.examples.page.security;

import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.User;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.PageSubmit;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a user authentication login Page.
 *
 * @author Malcolm Edgar
 */
public class Login extends BorderPage {

    public Form form = new Form();
    public HiddenField redirectField = new HiddenField("redirect", String.class);

    private TextField usernameField = new TextField("username", true);
    private PasswordField passwordField = new PasswordField("password", true);

    public Login() {
        usernameField.setMaxLength(20);
        usernameField.setMinLength(5);
        usernameField.setFocus(true);
        form.add(usernameField);

        passwordField.setMaxLength(20);
        passwordField.setMinLength(5);
        form.add(passwordField);

        form.add(new Submit("ok", " OK ", this, "onOkClicked"));
        form.add(new PageSubmit("cancel", HomePage.class));
    }

    public void onInit() {
        String username = null;

        if (getContext().isPost()) {
            username = getContext().getRequestParameter("username");

        } else {
            username = getContext().getCookieValue("username");
            if (username != null) {
                usernameField.setValue(username);
                usernameField.setFocus(false);
                passwordField.setFocus(true);
            }
        }
    }

    public boolean onOkClicked() {
        if (form.isValid()) {
            User user = new User();
            form.copyTo(user);

            if (getUserService().isAuthenticatedUser(user)) {

                user = getUserService().getUser(user.getUsername());
                getContext().setSessionAttribute("user", user);

                getContext().setCookie("username",
                                       user.getUsername(),
                                       Integer.MAX_VALUE);

                String redirect = redirectField.getValue();
                if (StringUtils.isNotBlank(redirect)) {
                    setRedirect(redirect);

                } else {
                    setRedirect(Secure.class);
                }

            } else {
                form.setError(getMessage("authentication-error"));
            }
        }

        return true;
    }

}
