package net.sf.click.examples.service;

import net.sf.click.examples.domain.User;

/**
 * Provides a User Service.
 *
 * @see User
 *
 * @author Malcolm Edgar
 */
public class UserService extends CayenneTemplate {

    public boolean isAuthenticatedUser(User user) {
        User user2 = getUser(user.getUsername());

        if (user2 != null && user2.getPassword().equals(user.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    public User getUser(String username) {
        return (User) findObject(User.class, "username", username);
    }
}
