package benchmark.tapestry.pages.tapestry;

import benchmark.dao.Customer;
import benchmark.dao.CustomerDao;
import java.util.List;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Retain;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

/**
 *
 */
public class CustomerList {

    @Inject
    private BeanModelSource _beanModelSource;

    @Inject
    private ComponentResources _componentResources;

    @SuppressWarnings("unchecked")
    @Property
    @Retain
    private BeanModel _myModel;

    @Property
    private Customer customer;

    @SuppressWarnings("unused")
    @Property
    private List<Customer> customers;

    void onActionFromDelete(Integer userId) {
    }

    void onActionFromEdit(Integer userId) {
    }

    void setupRender() {

        if (_myModel == null) {
            _myModel =
                _beanModelSource.createDisplayModel(Customer.class,
                                                    _componentResources.getMessages());
            _myModel.add("delete", null);
            _myModel.get("firstName").sortable(false);
            _myModel.get("lastName").sortable(false);
            _myModel.get("state").sortable(false);
            _myModel.get("birthDate").sortable(false);
        }

        // Get all persons - ask business service to find them (from the database)
        customers = CustomerDao.getInstance().findAll();
    }
}
