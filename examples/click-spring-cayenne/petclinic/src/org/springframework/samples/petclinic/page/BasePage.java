package org.springframework.samples.petclinic.page;

import net.sf.click.Page;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.samples.petclinic.service.ClinicService;

public class BasePage extends Page implements ApplicationContextAware {
    
    /** The Spring application context. */
    protected ApplicationContext applicationContext;

    // --------------------------------------------------------- Public Methods

    /**
     * Set the Spring application context.
     * 
     * @see ApplicationContextAware#setApplicationContext(ApplicationContext)
     * 
     * @param applicationContext the Spring application context
     */
    public void setApplicationContext(ApplicationContext applicationContext) 
        throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Return the named bean from the Spring application context.
     * 
     * @param name the name of the Spring bean
     * @return the named Spring bean
     */
    public Object getBean(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        if (applicationContext == null) {
            throw new IllegalStateException("Application context is not set");
        }
        return applicationContext.getBean(name);
    }
    
    /**
     * Return the application Clinic service.
     * 
     * @return the application Clinic service
     */
    public ClinicService getClinic() {
        return (ClinicService) getBean("clinic");
    }
    
    /**
     * Return the page border template.
     * 
     * @return the border page template
     */
    public String getTemplate() {
        return "border.htm";
    }
}
