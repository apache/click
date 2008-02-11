package benchmark.dao;

import org.apache.wicket.model.LoadableDetachableModel;

public class DetachableCustomer extends LoadableDetachableModel {

    /** */
    private static final long serialVersionUID = 1L;

    private Integer id;

    public DetachableCustomer(Customer customer) {
        super(customer);
        id = customer.getId();
    }

    @Override
    protected Object load() {
        return CustomerDao.getInstance().findById(id);
    }
}