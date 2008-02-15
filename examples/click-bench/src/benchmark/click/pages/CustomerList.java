package benchmark.click.pages;

import net.sf.click.Page;
import net.sf.click.control.AbstractLink;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Column;
import net.sf.click.control.Table;
import net.sf.click.extras.control.LinkDecorator;

import benchmark.dao.CustomerDao;
import benchmark.dao.Customer;

public class CustomerList extends Page {

    private Table table;

    private ActionLink editLink = new ActionLink("Edit", this, "onEditClick");

    private ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    public CustomerList() {
        table = new Table("table");
        table.addStyleClass("decorated");
        Column first = new Column("firstName");
        table.addColumn(first);
        Column last = new Column("lastName");
        table.addColumn(last);
        Column state = new Column("state");
        table.addColumn(state);
        Column dob = new Column("birthDate", "Date of Birth");
        dob.setFormat("{0,date,MMMM d, yyyy}");
        table.addColumn(dob);

        Column actions = new Column("Options");
        AbstractLink[] links = new AbstractLink[]{editLink, deleteLink};
        actions.setDecorator(new LinkDecorator(table, links, "id"));
        table.addColumn(actions);

        addControl(table);
        addControl(editLink);
        addControl(deleteLink);
    }

    public void onRender() {
        table.setRowList(CustomerDao.getInstance().findAll());
    }

    public boolean onDeleteClick() {
        Integer id = deleteLink.getValueInteger();
        Customer customer = CustomerDao.getInstance().findById(id);
        CustomerDao.getInstance().delete(customer);
        return true;
    }

    public boolean onEditClick() {
        Integer id = editLink.getValueInteger();
        Customer customer = CustomerDao.getInstance().findById(id);
        EditCustomer editPage = (EditCustomer) getContext().createPage(EditCustomer.class);
        editPage.setCustomer(customer);
        setForward(editPage);
        return false;
    }
}
