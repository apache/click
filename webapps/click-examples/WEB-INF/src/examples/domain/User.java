package examples.domain;

import java.io.Serializable;

/**
 * Provides a mockup persistent User business object for the examples.
 *
 * @author Malcolm Edgar
 */
public class User implements Serializable {

    String username;
    String password;
    String fullname;
    String email;

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Returns the fullname.
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * @param fullname The fullname to set.
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    private static final long serialVersionUID = 1L;

}
