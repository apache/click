package examples.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a mockup Customer database for the examples.
 * 
 * @see Customer
 * 
 * @author Malcolm Edgar
 */
public class CustomerDatabase {

    public static List getCustomersSortedByName() {
        List customers = new ArrayList(CUSTOMER_BY_NAME.values());

        return customers;
    }

    public static void setCustomer(Customer customer) {
        if (customer != null) {
            deleteCustomer(customer.id);
            CUSTOMER_BY_ID.put(customer.id, customer);
            CUSTOMER_BY_NAME.put(customer.name, customer);
        }
    }

    public static Customer getCustomer(Long id) {
        return (Customer) CUSTOMER_BY_ID.get(id);
    }

    public static void deleteCustomer(Long id) {
        CUSTOMER_BY_ID.remove(id);

        Iterator i = CUSTOMER_BY_NAME.values().iterator();
        while (i.hasNext()) {
            Customer customer = (Customer) i.next();
            if (customer.getId().equals(id)) {
                i.remove();
                break;
            }
        }
    }
    
    public static Customer findCustomerByID(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        try {
            // Search for customer id
            Long id = Long.valueOf(value);        
            return (Customer) CUSTOMER_BY_ID.get(id);

        } catch (NumberFormatException nfe) {
            return null;
        }  
    }
    
    public static Customer findCustomerByName(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }        
        
        // Search for customer name
        String nameValue = value.toLowerCase();

        Iterator customers = CUSTOMER_BY_NAME.values().iterator();
        while (customers.hasNext()) {
            Customer customer = (Customer) customers.next();
            String name = customer.getName();
            if (name.toLowerCase().indexOf(nameValue) != -1) {
                return customer;
            }
        }
        // No customer was found
        return null;
    }

    public static Customer findCustomerByAge(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        try {
            // Search for customer by age
            Integer age = Integer.valueOf(value);

            Iterator customers = CUSTOMER_BY_ID.values().iterator();
            while (customers.hasNext()) {
                Customer customer = (Customer) customers.next();

                if (customer.getAge().equals(age)) {
                    return customer;
                }
            }
            // No customer was found
            return null;
            
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private static final Map CUSTOMER_BY_NAME = new TreeMap();

    private static final Map CUSTOMER_BY_ID = new HashMap();

    static {
        Customer customer = new Customer();
        customer.id = new Long(2023);
        customer.name = "Ann Melan";
        customer.email = "ann_melan@inet.com";
        customer.age = new Integer(41);
        customer.investments = "Residential Property";
        customer.holdings = new Double(34560);
        customer.dateJoined = new java.sql.Date(101, 11, 03);
        CUSTOMER_BY_NAME.put(customer.name, customer);
        CUSTOMER_BY_ID.put(customer.id, customer);

        customer = new Customer();
        customer.id = new Long(4501);
        customer.name = "Bob Harrold";
        customer.email = "bobh@citibank.com";
        customer.age = new Integer(50);
        customer.holdings = new Double(45030);
        customer.investments = "Options";
        customer.dateJoined = new java.sql.Date(89, 5, 30);
        CUSTOMER_BY_NAME.put(customer.name, customer);
        CUSTOMER_BY_ID.put(customer.id, customer);

        customer = new Customer();
        customer.id = new Long(7620);
        customer.name = "John Tessel";
        customer.email = "john_tessel@hotmail.com";
        customer.age = new Integer(58);
        customer.holdings = new Double(90400);
        customer.investments = "Bonds";
        customer.dateJoined = new java.sql.Date(92, 0, 19);
        CUSTOMER_BY_NAME.put(customer.name, customer);
        CUSTOMER_BY_ID.put(customer.id, customer);

        customer = new Customer();
        customer.id = new Long(7634);
        customer.name = "Rodger Alan";
        customer.email = "ralan@westpower.com";
        customer.age = new Integer(27);
        customer.holdings = new Double(0);
        customer.investments = "None";
        customer.dateJoined = new java.sql.Date(97, 6, 26);
        CUSTOMER_BY_NAME.put(customer.name, customer);
        CUSTOMER_BY_ID.put(customer.id, customer);

        customer = new Customer();
        customer.id = new Long(4424);
        customer.name = "David Henderson";
        customer.email = "dhendi@yahoo.com";
        customer.age = new Integer(45);
        customer.holdings = new Double(430500.0);
        customer.investments = "Commercial Property";
        customer.dateJoined = new java.sql.Date(103, 8, 14);
        CUSTOMER_BY_NAME.put(customer.name, customer);
        CUSTOMER_BY_ID.put(customer.id, customer);

        customer = new Customer();
        customer.id = new Long(4478);
        customer.name = "Katherine Malar";
        customer.email = "kmalar@mycorp.com";
        customer.age = new Integer(52);
        customer.holdings = new Double(870000.0);
        customer.investments = "Residential Property";
        customer.dateJoined = new java.sql.Date(83, 4, 19);
        CUSTOMER_BY_NAME.put(customer.name, customer);
        CUSTOMER_BY_ID.put(customer.id, customer);

        customer = new Customer();
        customer.id = new Long(1056);
        customer.name = "John Merton";
        customer.email = "John.Merton@meriton.com";
        customer.age = new Integer(48);
        customer.holdings = new Double(109000.0);
        customer.investments = "Options";
        customer.dateJoined = new java.sql.Date(103, 6, 29);
        CUSTOMER_BY_NAME.put(customer.name, customer);
        CUSTOMER_BY_ID.put(customer.id, customer);
    }

}