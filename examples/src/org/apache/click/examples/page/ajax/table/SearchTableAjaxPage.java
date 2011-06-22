package org.apache.click.examples.page.ajax.table;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.click.ActionResult;
import org.apache.click.Control;
import org.apache.click.ControlRegistry;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextField;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.TableInlinePaginator;
import org.springframework.stereotype.Component;

/**
 * Demonstrates a AJAX search form updating an table using jQuery.
 */
@Component
public class SearchTableAjaxPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");
    private TextField nameField = new TextField(Customer.NAME_PROPERTY);
    private Table table = new Table("table");

    @Resource(name="customerService")
    private CustomerService customerService;

    public SearchTableAjaxPage() {
        // Setup search form
        setupForm(form);

        addControl(form);

        // Setup results table
        setupTable(table);

        table.setDataProvider(new DataProvider<Customer>() {
            public List<Customer> getData() {
                return customerService.getCustomersForName(nameField.getValue());
            }
        });

        addControl(table);
    }

    // Public Methods --------------------------------------------------------

    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));
            headElements.add(new JsImport("/assets/js/jquery.blockUI-2.39.js"));
            headElements.add(new JsScript("/ajax/table/search-table-ajax.js", new HashMap()));
        }
        return headElements;
    }

    // Private Methods --------------------------------------------------------

    private void setupForm(final Form form) {

        ControlRegistry.registerAjaxTarget(form);

        form.add(nameField);

        Submit searchButton = new Submit("search");
        form.add(searchButton);

        Submit clearButton = new Submit("clear");
        form.add(clearButton);

        form.addBehavior(new DefaultAjaxBehavior() {
            @Override
            public ActionResult onAction(Control source) {
                // Return a success response
                // Form data can be saved here
                return new ActionResult(table.toString(), ActionResult.HTML);
            }
        });
    }

    private void setupTable(final Table table) {
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setPaginator(new TableInlinePaginator(table));
        table.setPaginatorAttachment(Table.PAGINATOR_INLINE);

        Column column = new Column(Customer.NAME_PROPERTY);
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column(Customer.EMAIL_PROPERTY);
        column.setAutolink(true);
        column.setWidth("230px;");
        table.addColumn(column);

        column = new Column(Customer.AGE_PROPERTY);
        column.setTextAlign("center");
        column.setWidth("40px;");
        table.addColumn(column);

        column = new Column(Customer.HOLDINGS_PROPERTY);
        column.setFormat("${0,number,#,##0.00}");
        column.setTextAlign("right");
        column.setWidth("100px;");
        table.addColumn(column);

        table.getControlLink().addBehavior(new DefaultAjaxBehavior() {
            @Override
            public ActionResult onAction(Control source) {
                // NOTE: Ajax requests only process the target Control. Here we
                // process the table in order to update paging and sorting state
                table.onProcess();
                return new ActionResult(table.toString(), ActionResult.HTML);
            }
        });
    }

}
