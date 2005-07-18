package examples.page.security;

import examples.domain.User;
import examples.domain.UserDAO;
import examples.page.BorderedPage;
import net.sf.click.Page;
import net.sf.click.control.Form;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;

/**
 * Provides a user authentication login Page.
 *
 * @author Malcolm Edgar
 */
public class Login extends BorderedPage {

    Form form;
    TextField usernameField;
    PasswordField passwordField;

    /**
     * @see Page#onInit()
     */
    public void onInit() {
        form = new Form("form", getContext());
        addControl(form);

        usernameField = new TextField("Username");
        usernameField.setMaxLength(20);
        usernameField.setMinLength(5);
        usernameField.setRequired(true);
        usernameField.setFocus(true);
        form.add(usernameField);

        passwordField = new PasswordField("Password");
        passwordField.setMaxLength(20);
        passwordField.setMinLength(5);
        passwordField.setRequired(true);
        form.add(passwordField);

        Submit okButton = new Submit("    OK    ");
        okButton.setListener(this, "onOkClicked");
        form.add(okButton);

        Submit cancelButton = new Submit(" Cancel ");
        cancelButton.setListener(this, "onCancelClicked");
        form.add(cancelButton);
    }

    /**
     * @see Page#onSecurityCheck()
     */
    public boolean onSecurityCheck() {
        if (getContext().hasSessionAttribute("user")) {
            setRedirect("secure.htm");
            return false;

        } else {
            return true;
        }
    }

    public boolean onOkClicked() {
        if (form.isValid()) {
            String username = usernameField.getValue();
            String password = passwordField.getValue();

            User user = UserDAO.getUser(username);

            if (user != null && user.getPassword().equals(password)) {
                getContext().setSessionAttribute("user", user);
                setRedirect("secure.htm");

            } else {
                form.setError(getMessage("authentication-error"));
            }
        }

        return true;
    }

    public boolean onCancelClicked() {
        setRedirect("index.html");

        return false;
    }
}
