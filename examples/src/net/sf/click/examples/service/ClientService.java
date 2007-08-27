package net.sf.click.examples.service;

import java.util.List;

import net.sf.click.examples.domain.Client;
import net.sf.click.extras.cayenne.CayenneTemplate;

import org.apache.cayenne.query.SelectQuery;

/**
 * Provides a Client Service.
 *
 * @see Client
 *
 * @author Malcolm Edgar
 */
public class ClientService extends CayenneTemplate {

    public List getClients() {
        SelectQuery query = new SelectQuery(Client.class);
        query.addOrdering("db:id", true);
        return performQuery(query);
    }

    public Client getClient(Object id) {
        return (Client) getObjectForPK(Client.class, id);
    }

}
