package examples.page;

import net.sf.click.Page;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import examples.domain.CourseBookingService;
import examples.domain.CustomerService;
import examples.domain.UserService;

/**
 * Provides a Spring Services page for subclasses to extend.
 *
 * @author Malcolm Edgar
 */
public class SpringPage extends Page implements ApplicationContextAware {

    /** The Spring application context. */
    protected ApplicationContext applicationContext;

    /**
     * Return the CourseBooking Service object.
     *
     * @return the CourseBooking Service object.
     */
    public CourseBookingService getCourseBookingService() {
        return (CourseBookingService) getBean("courseBookingService");
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
