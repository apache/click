package examples.domain;

import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a mockup User Service for the examples.
 *
 * @see User
 *
 * @author Malcolm Edgar
 */
public class UserService {

    public boolean isAuthenticatedUser(User user) {
        if (user != null) {
            User foundUser = getUser(user.getUsername());
            if (foundUser != null) {
                return foundUser.getPassword().equals(user.getPassword());
            }
        }
        return false;
    }

    public User getUser(String username) {
        return (User) USERS_MAP.get(username.toLowerCase());
    }

    public void addUser(User user) {
        USERS_MAP.put(user.username.toLowerCase(), user);
    }

    public void deleteUser(User user) {
        USERS_MAP.remove(user.username.toLowerCase());
    }

    private static final Map USERS_MAP = new TreeMap();

    static {
        User user = new User();
        user.fullname = "Ann Melan";
        user.email = "amelan@mycorp.com";
        user.username = "amelan";
        user.password = "password";
        USERS_MAP.put(user.username.toLowerCase(), user);
 
        user = new User();
        user.fullname = "Bod Harrold";
        user.email = "bharrold@mycorp.com";
        user.username = "bharrold";
        user.password = "password";
        USERS_MAP.put(user.username.toLowerCase(), user);

        user = new User();
        user.fullname = "John Tessel";
        user.email = "jtessel@mycorp.com";
        user.username = "jtessel";
        user.password = "password";
        USERS_MAP.put(user.username.toLowerCase(), user);

        user = new User();
        user.fullname = "Rodger Alan";
        user.email = "ralan@mycorp.com";
        user.username = "ralan";
        user.password = "password";
        USERS_MAP.put(user.username.toLowerCase(), user);

        user = new User();
        user.fullname = "David Henderson";
        user.email = "dhenderson@mycorp.com";
        user.username = "dhenderson";
        user.password = "password";
        USERS_MAP.put(user.username.toLowerCase(), user);
    }

}
