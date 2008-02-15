package benchmark.dao;

import org.apache.wicket.model.LoadableDetachableModel;

public class DetachableCustomerList extends LoadableDetachableModel {

    /** */
    private static final long serialVersionUID = 1L;

    protected Object load() {
        return CustomerDao.getInstance().findAll();
    }
}