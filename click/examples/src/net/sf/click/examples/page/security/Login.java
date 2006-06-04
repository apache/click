package net.sf.click.examples.page.security;

import net.sf.click.Page;
import net.sf.click.control.Form;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.User;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides a user authentication login Page.
 *
 * @author Malcolm Edgar
 */
public class Login extends BorderPage {

    private Form form = new Form("form");

    public Login() {
        TextField usernameField = new TextField("username", true);
        usernameField.setMaxLength(20);
        usernameField.setMinLength(5);
        usernameField.setFocus(true);
        form.add(usernameField);

        PasswordField passwordField = new PasswordField("password", true);
        passwordField.setMaxLength(20);
        passwordField.setMinLength(5);
        form.add(passwordField);

        form.add(new Submit("ok", "    OK    ", this, "onOkClicked"));
        form.add(new Submit("cancel", this, "onCancelClicked"));

        addControl(form);
    }

    /**
     * @see Page#onSecurityCheck()
     */
    public boolean onSecurityCheck() {
        if (getContext().hasSessionAttribute("user")) {
            setRedirect(Secure.class);
            return false;

        } else {
            return true;
        }
    }

    public boolean onOkClicked() {
        if (form.isValid()) {
            User user = new User();
            form.copyTo(user);

            if (getUserService().isAuthenticatedUser(user)) {
                user = getUserService().getUser(user.getUsername());
                getContext().setSessionAttribute("user", user);
                setRedirect("/security/secure.htm");

            } else {
                form.setError(getMessage("authentication-error"));
            }
        }

        return true;
    }

    public boolean onCancelClicked() {
        setRedirect("/index.html");
        return false;
    }
}
