package net.sf.click.examples.page.ajax;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.domain.PostCode;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.AutoCompleteTextField;

/**
 * Provides AJAX AutoCompleteTextField example page.
 *
 * @author Malcolm Edgar
 */
public class AutoCompletePage extends BorderPage {

    public Form form = new Form();
    public Customer customer;

    private TextField nameField;

    // ------------------------------------------------------------ Constructor

    public AutoCompletePage() {
        nameField = new AutoCompleteTextField("name", true) {
            public List getAutoCompleteList(String criteria) {
                return getPostCodeLocations(criteria);
            }
        };
        nameField.setSize(40);

        form.add(nameField);

        form.add(new Submit(" OK ", this, "onOkClick"));
    }

    // --------------------------------------------------------- Event Handlers

    public boolean onOkClick() {
//        if (form.isValid()) {
//            String name = nameField.getValue();
//            customer = getCustomerService().findCustomerByName(name);
//        }
        return true;
    }
    
    public List getPostCodeLocations(String criteria) {
    	SelectQuery query = new SelectQuery(PostCode.class);
    	
        query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(PostCode.LOCALITY_PROPERTY, criteria + "%"));

        query.addOrdering(PostCode.LOCALITY_PROPERTY, true);

        query.setFetchLimit(10);

        List list = getDataContext().performQuery(query);

        for (int i = 0; i < list.size(); i++) {
        	PostCode postCode = (PostCode) list.get(i);
        	String value = postCode.getLocality() + ", " + postCode.getState() + " " + postCode.getPostCode();
            list.set(i, value);
        }

        return list;
    }

}
