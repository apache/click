package examples.sandbox.chrisichris.page.prototype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import examples.control.InvestmentSelect;
import examples.domain.Customer;
import examples.domain.CustomerDAO;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.sandbox.chrisichris.prototype.AjaxAction;
import net.sf.click.sandbox.chrisichris.prototype.AjaxActionLink;
import net.sf.click.sandbox.chrisichris.prototype.AjaxPage;
import net.sf.click.sandbox.chrisichris.prototype.AjaxSubmit;
import net.sf.click.sandbox.chrisichris.prototype.AutoCompleteTextField;

public class CustomerFormPage extends AjaxPage{

    public static final AjaxAction autocompleteProvider = new AutoCompleteTextField.Action(){
        protected java.util.List getData(String param) {
            param = param.toLowerCase();
            List all = CustomerDAO.getCustomersSortedByName();
            List ret = new ArrayList();
            for (Iterator it = all.iterator(); it.hasNext(); ) {
                Customer cust = (Customer) it.next();
                String name = cust.getName().toLowerCase();
                if(name.startsWith(param)){
                    ret.add(cust.getName());
                }
            }
            return ret;
        }
    };
    
    static{
        AjaxAction.createActionMap(CustomerFormPage.class);
    }
    
    private AjaxActionLink editCustomer = new AjaxActionLink("editLink",this,"onEditClicked");
    private AjaxActionLink deleteCustomer = new AjaxActionLink("deleteLink",this,"onDeleteClicked");

    private Form customerForm = new Form("customerForm");
    AjaxSubmit okSubmit = new AjaxSubmit("ok",this,"onOkClick");
    AjaxSubmit cancelSubmit = new AjaxSubmit("cancel",this,"onCancelClick");
    
    private AutoCompleteTextField searchText = new AutoCompleteTextField("searchText","Customer Name",autocompleteProvider);
    
    private HiddenField customerIdF = new HiddenField("id",Long.class);
    
    public CustomerFormPage() {
        super();
    }
    
    public void onInitPage() {
        //the delete and edit customer actions
        addControl(editCustomer);
        
        addControl(deleteCustomer);
        deleteCustomer.getPrototypeAjax().setConfirm("Are you sure you want to delete this Customer?");
        
        addModel("deleteProto",deleteCustomer.getPrototypeAjax());
        
        //the edit form
        customerForm.add(customerIdF);

        TextField nameField = new TextField("name", true);
        nameField.setMinLength(5);
        nameField.setFocus(true);
        customerForm.add(nameField);

        customerForm.add(new EmailField("email"));

        IntegerField ageField = new IntegerField("age");
        ageField.setMinValue(1);
        ageField.setMaxValue(120);
        customerForm.add(ageField);

        customerForm.add(new DoubleField("holdings"));
        customerForm.add(new InvestmentSelect("investments"));
        customerForm.add(new DateField("dateJoined"));
        customerForm.add(new Checkbox("Active"));

        customerForm.add(okSubmit);
        customerForm.add(cancelSubmit);

        addControl(customerForm);
        
        //the search form
        Form searcheForm = new Form("searcheForm");
        searcheForm.add(searchText);
        AjaxSubmit sub = new AjaxSubmit("searchSubmit","Search",this,"onSearchSubmit");
        sub.setUpdateSuccess("customerTable");
        sub.setProgressImage("spinner-searche");
        searcheForm.add(sub);
        addControl(searcheForm);
        
        
    }

    /** (non-Javadoc)
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        addModel("customers",CustomerDAO.getCustomersSortedByName(20));
    }
    
    public boolean onEditClicked() {
        Customer cust = CustomerDAO.getCustomer(editCustomer.getValueLong());
        if(cust != null) {
            customerForm.copyFrom(cust);
            okSubmit.setUpdate("custrow-"+cust.getId());
            okSubmit.setProgressImage("spinner-"+cust.getId());
            cancelSubmit.setUpdate("custrow-"+cust.getId());
            cancelSubmit.setUpdate("custrow-"+cust.getId());
            renderForm(cust);
        } else {
            getContext().getResponse().setStatus(404);
            setPath(null);
        }
        return false;
    }
    
    public boolean onDeleteClicked() {
        Customer cust = CustomerDAO.getCustomer(deleteCustomer.getValueLong());
        if(cust != null) {
            CustomerDAO.deleteCustomer(cust.getId());
        } else {
            getContext().getResponse().setStatus(401);
        }
        setPath(null);
        return false;
    }

    public boolean onOkClick() {
        if (customerForm.isValid()) {

            Customer customer = new Customer();
            customerForm.copyTo(customer);
            CustomerDAO.setCustomer(customer);
            renderData(customer);
            return false;

        } else {
            renderForm(CustomerDAO.getCustomer((Long)customerIdF.getValueObject()));
            return false;
        }
    }
    
    public boolean onCancelClick(){
        renderData(CustomerDAO.getCustomer((Long)customerIdF.getValueObject()));
        return false;
    }
    
    private void renderForm(Customer customer){
        addModel("customer",customer);
        setContentType("text/xml");
        setTemplate(getPath()+"?customerrow-form");
        
    }
    
    private void renderData(Customer cust) {
        addModel("customer",cust);
        setContentType("text/xml");
        setTemplate(getPath()+"?customerrow-show");
    }
    
    public boolean onSearchSubmit(){
        String name = searchText.getValue();
        List custs = CustomerDAO.getCustomersSortedByName();
        List actual = new ArrayList();
        for (Iterator it = custs.iterator(); it.hasNext(); ) {
            Customer cust = (Customer) it.next();
            if(cust.getName().startsWith(name)){
                actual.add(cust);
            }
        }
        addModel("customers",actual);
        
        setContentType("text/html");
        setTemplate(getPath()+"?customerTable");
        return false;
    }
    
    
}
