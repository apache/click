package benchmark.click.velocity;

import benchmark.dao.CustomerDao;
import net.sf.click.Page;

public class VelocityCustomerList extends Page {

    public VelocityCustomerList() {
        addModel("customers", CustomerDao.getInstance().findAll());
    }
}
