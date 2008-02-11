package benchmark.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A very simple implementation of a typical data access object. All the data
 * is generated at random on application startup and stored using a linked
 * hash map to mymic an actual database.
 * 
 * @author Phil Kulak
 */
public class CustomerDao {
    private static final Random GENERATOR = new Random();    
    
    private static final String[] FIRST_NAMES = new String[] {
        "Emily", "Jacob", "Michael", "Emma", "Joshua", "Madison", "Matthew",
        "Hannah", "Andrew", "Olivia", "Cletus", "Warner", "Sarah", "Billy",
        "Brittany", "Daniel", "David", "Cristman", "Colin", "Royalle"
    };
    
    private static final String[] LAST_NAMES = new String[] {
        "Aaron", "Bolingbroke", "Crounse", "Duff", "Drake", "Downs", "Driver",
        "Jasper", "Jetter", "O'Leary", "O'Malley", "Neville", "Towers", "Tripp",
        "Trull", "Wakefield", "Waller", "Badger", "Bagley", "Baker"
    };
    
    public static final String[] STATES = new String[] {
        "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
        "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
        "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
        "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
        "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada",
        "New Hampshire", "New Jersey", "New Mexico", "New York",
        "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
        "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
        "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
        "West Virginia", "Wisconsin", "Wyoming"
    };
    
    private static final CustomerDao INSTANCE = new CustomerDao();
    
    private Map<Integer, Customer> customers = Collections.synchronizedMap(createNewMap());
    
    public static CustomerDao getInstance() {
        return INSTANCE;
    }
    
    public List<Customer> findAll() {
        List<Customer> ret = new ArrayList<Customer>(50);
        ret.addAll(customers.values());
        return ret;
    }
    
    public Customer findById(Integer id) {
        return customers.get(id);
    }
    
    public void saveOrUpdate(Customer customer) {
        if (customer != null) {
            customers.put(customer.getId(), customer);
        }
    }
    
    public void delete(Customer customer) {
        if (customer != null) {
            customers.remove(customer.getId());
        }
    }
    
    private static Map<Integer, Customer> createNewMap() {
        Map<Integer, Customer> ret = new LinkedHashMap<Integer, Customer>(50);
        
        for (int i = 0; i < 50; i++) {
            Customer customer = new Customer();            
            customer.setFirstName(randomString(FIRST_NAMES));
            customer.setLastName(randomString(LAST_NAMES));
            customer.setState(randomString(STATES));
            customer.setBirthDate(randomDate());
            customer.setId(new Integer(i));
            ret.put(customer.getId(), customer);
        }
        
        return ret;
    }
    
    private static String randomString(String[] items) {
        return items[GENERATOR.nextInt(items.length)];
    }
    
    private static Date randomDate() {
        int daysAgo = GENERATOR.nextInt(18250) + 4745;
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -daysAgo);
        return calendar.getTime();
    }
}
