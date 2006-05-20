package examples.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Provides a mockup Customer Service for the examples.
 *
 * @see Customer
 * @author Malcolm Edgar
 */
public class CustomerService {

    private static final SimpleDateFormat FORMAT
        = new SimpleDateFormat("yyyy-MM-dd");

    private static final Map CUSTOMER_BY_NAME = new TreeMap();

    private static final Map CUSTOMER_BY_ID = new HashMap();

    private static final List ALL_CUSTOMER_LIST = new ArrayList(20000);

    static {
        loadCustomers();
    }

    // --------------------------------------------------------- Public Methods

    public synchronized List getCustomersSortedByName() {
        ensureDataAvailable();
        List customers = new ArrayList(CUSTOMER_BY_NAME.values());
        return customers;
    }

    public synchronized List getCustomersSortedByName(int rows) {
        ensureDataAvailable();
        List customers = new ArrayList(rows);

        for (Iterator i = CUSTOMER_BY_NAME.values().iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            customers.add(customer);
            if (customers.size() >= rows) {
                break;
            }
        }

        return customers;
    }

    public synchronized void setCustomer(Customer customer) {
        ensureDataAvailable();
        if (customer != null) {
            deleteCustomer(customer.id);
            CUSTOMER_BY_ID.put(customer.id, customer);
            CUSTOMER_BY_NAME.put(customer.name, customer);
        }
    }

    public synchronized Customer getCustomer(Long id) {
        ensureDataAvailable();
        return (Customer) CUSTOMER_BY_ID.get(id);
    }

    public synchronized void deleteCustomer(Long id) {
        ensureDataAvailable();
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

    public synchronized Customer findCustomerByID(Long id) {
        ensureDataAvailable();
        return (Customer) CUSTOMER_BY_ID.get(id);
    }

    public synchronized Customer findCustomerByID(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        ensureDataAvailable();

        try {
            // Search for customer id
            return findCustomerByID(Long.valueOf(value));

        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public synchronized Customer findCustomerByName(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        ensureDataAvailable();

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

    public synchronized Customer findCustomerByAge(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }

        ensureDataAvailable();

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

    public synchronized List findCustomersByPage(int offset, int pageSize) {
        ensureDataAvailable();
        List customers = getAllCustomers();
        int toIndex = Math.min(offset + pageSize, customers.size());
        return customers.subList(offset, toIndex);
    }

    public synchronized List getAllCustomers() {
        return ALL_CUSTOMER_LIST;
    }

    // -------------------------------------------------------- Private Methods

    private synchronized void ensureDataAvailable() {
        if (CUSTOMER_BY_NAME.size() < 6) {
            loadCustomers();
        }
    }

    private static synchronized void loadCustomers() {
        try {
            InputStream is =
                CustomerService.class.getResourceAsStream("customers.txt");

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            while (line != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                Customer customer = new Customer();
                customer.id = Long.valueOf(tokenizer.nextToken().trim());
                customer.name = tokenizer.nextToken().trim();
                if (tokenizer.hasMoreTokens()) {
                    customer.email = tokenizer.nextToken().trim();
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.age = Integer.valueOf(tokenizer.nextToken().trim());
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.investments = tokenizer.nextToken().trim();
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.holdings = Double.valueOf(tokenizer.nextToken().trim());
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.dateJoined = createDate(tokenizer.nextToken().trim());
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.active = Boolean.valueOf(tokenizer.nextToken().trim());
                }

                CUSTOMER_BY_NAME.put(customer.name, customer);
                CUSTOMER_BY_ID.put(customer.id, customer);

                line = reader.readLine();
            }

            ALL_CUSTOMER_LIST.clear();
            for (int i = 0; i < 1000; i++) {
                ALL_CUSTOMER_LIST.addAll(new ArrayList(CUSTOMER_BY_NAME.values()));
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static Date createDate(String pattern) {
        try {
            return FORMAT.parse(pattern);
        } catch (ParseException pe) {
            return null;
        }
    }
}
