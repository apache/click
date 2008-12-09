/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.sf.click.examples.page;

import net.sf.click.Page;
import net.sf.click.examples.service.BookingService;
import net.sf.click.examples.service.ClientService;
import net.sf.click.examples.service.CustomerService;
import net.sf.click.examples.service.PostCodeService;
import net.sf.click.examples.service.UserService;

import org.apache.cayenne.access.DataContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Provides a Spring Services page for subclasses to extend.
 * <p/>
 * Please note this page is designed for extending by Page subclasses and will
 * not be auto mapped as there are no page template name matching the
 * Pages class name <tt>SpringPage</tt>.
 *
 * @author Malcolm Edgar
 */
public class SpringPage extends Page implements ApplicationContextAware {

    /**
     * The Spring application context. Note this variable is transient to
     * support stateful page serialization.
     */
    protected transient ApplicationContext applicationContext;

    /**
     * Return the course Booking Service object.
     *
     * @return the course Booking Service object.
     */
    public BookingService getBookingService() {
        return (BookingService) getBean("bookingService");
    }

    /**
     * Return the Client Service object.
     *
     * @return the Client Service object.
     */
    public ClientService getClientService() {
        return (ClientService) getBean("clientService");
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
     * Return the PostCode Service object.
     *
     * @return the PostCode Service object.
     */
    public PostCodeService getPostCodeService() {
        return (PostCodeService) getBean("postCodeService");
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
