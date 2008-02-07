package net.sf.click.examples.domain;

import net.sf.click.examples.domain.auto._Client;

/**
 * Provides an Client entity class.
 *
 * @author Malcolm Edgar
 */
public class Client extends _Client {

    private static final long serialVersionUID = 1L;

    public String getName() {
        return getFirstName() + " " + getLastName();
     }

}



