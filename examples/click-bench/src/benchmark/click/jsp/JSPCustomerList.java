package benchmark.click.jsp;

import benchmark.click.velocity.*;
import benchmark.dao.CustomerDao;
import net.sf.click.Page;

public class JSPCustomerList extends Page {

    public JSPCustomerList() {
        addModel("customers", CustomerDao.getInstance().findAll());
    }
}
