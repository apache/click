package net.sf.click.examples.page;

import net.sf.click.Page;
import net.sf.click.examples.service.BookingService;
import net.sf.click.examples.service.CustomerService;
import net.sf.click.examples.service.UserService;

import org.objectstyle.cayenne.access.DataContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Provides a Spring Services page for subclasses to extend.
 *
 * @author Malcolm Edgar
 */
public class SpringPage extends Page implements ApplicationContextAware {

    /** The Spring application context. */
    protected ApplicationContext applicationContext;

    /**
     * Return the course Booking Service object.
     *
     * @return the course Booking Service object.
     */
    public BookingService getBookingService() {
        return (BookingService) getBean("bookingService");
    }

    /**
     * Return the Customer Service object.
     *
     * @return the Customer Service object.
     */
    public CustomerService getCustomerService() {
        return (CustomerService) getBean("customerService");
    }

    /**
     * Return the User Service object.
     *
     * @return the User Service object.
     */
    public UserService getUserService() {
        return (UserService) getBean("userService");
    }

    /**
     * Return the thread local Cayenne DataContext.
     *
     * @return the thread local DataContext
     */
    public DataContext getDataContext() {
        return DataContext.getThreadDataContext();
    }

    /**
     * Return the named Spring bean.
     *
     * @param beanName the configured name of the Spring bean
     * @return the named Spring bean
     */
    public Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext context)
        throws BeansException {

        this.applicationContext = context;
    }
}
