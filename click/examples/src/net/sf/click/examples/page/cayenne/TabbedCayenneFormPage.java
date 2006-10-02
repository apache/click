package net.sf.click.examples.page.cayenne;

import java.util.List;

import net.sf.click.control.Column;
import net.sf.click.control.FieldSet;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.Client;
import net.sf.click.extras.cayenne.CayenneForm;
import net.sf.click.extras.cayenne.QuerySelect;
import net.sf.click.extras.cayenne.TabbedCayenneForm;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;

import org.objectstyle.cayenne.DataObject;

/**
 * Provides a TabbedCayenneForm and QuerySelect control demonstration.
 *
 * @see FormTablePage
 *
 * @author Malcolm Edgar
 */
public class TabbedCayenneFormPage extends FormTablePage {

    /**
     * Create a TabbedCayenneFormPage object.
     */
    public TabbedCayenneFormPage() {
        ((TabbedCayenneForm)form).setBackgroundColor("#eee");
        ((TabbedCayenneForm)form).setTabHeight("155px");
        ((TabbedCayenneForm)form).setTabWidth("305px");

        FieldSet clientFieldSet = new FieldSet("Client");
        ((TabbedCayenneForm)form).addTabSheet(clientFieldSet);

        QuerySelect querySelect = new QuerySelect("title", true);
        querySelect.setQueryValueLabel("titles", "value", "label");
        clientFieldSet.add(querySelect);

        clientFieldSet.add(new TextField("firstName"));
        clientFieldSet.add(new TextField("lastName"));
        clientFieldSet.add(new DateField("dateJoined"));
        clientFieldSet.add(new EmailField("email"));

        FieldSet addressFieldSet = new FieldSet("Address");
        ((TabbedCayenneForm)form).addTabSheet(addressFieldSet);

        addressFieldSet.add(new TextField("address.line1", "Line One"));
        addressFieldSet.add(new TextField("address.line2", "Line Two"));
        addressFieldSet.add(new TextField("address.suburb", "Suburb"));

        querySelect = new QuerySelect("address.state", "State", true);
        querySelect.setQueryValueLabel("states", "value", "label");
        addressFieldSet.add(querySelect);

        IntegerField postCodeField = new IntegerField("address.postCode", "Post Code");
        postCodeField.setMaxLength(5);
        postCodeField.setSize(5);
        addressFieldSet.add(postCodeField);

        // Table
        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));

        Column column = new Column("email");
        column.setAutolink(true);
        table.addColumn(column);

        table.addColumn(new Column("address.state", "State"));

        column = new Column("dateJoined");
        column.setFormat("{0,date,dd MMM yyyy}");
        table.addColumn(column);
    }

    /**
     * @see FormTablePage#createForm()
     */
    public CayenneForm createForm() {
        return new TabbedCayenneForm();
    }

    /**
     * @see FormTablePage#getDataObject(Object)
     */
    public DataObject getDataObject(Object id) {
        return getClientService().getClient(id);
    }

    /**
     * @see FormTablePage#getDataObjectClass()
     */
    public Class getDataObjectClass() {
        return Client.class;
    }

    /**
     * @see FormTablePage#getRowList()
     */
    public List getRowList() {
        return getClientService().getClients();
    }

}
