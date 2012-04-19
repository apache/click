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
package org.apache.click.extras.spring;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;

import org.apache.click.ClickServlet;
import org.apache.click.Page;
import org.apache.click.util.HtmlStringBuffer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Provides a Spring framework integration <tt>SpringClickServlet</tt>.
 * <p/>
 * This Spring integration servlet provides a number of integration options
 * using Spring with Click pages. These options are detailed below.
 * <p/>
 * <b>Stateful pages caveat:</b> please note that stateful pages do not work
 * with all options.
 *
 * <h3><a name="option1"></a>1. Spring instantiated Pages with &#64;Component
 * configuration</h3>
 *
 * With this option Page classes are configured with Spring using the
 * &#64;Component annotation. When the SpringClickServlet receives a page
 * request it converts the auto-mapped page class to the equivalent Spring
 * bean name and gets a new instance from the Spring ApplicationContext.
 *
 * <pre class="codeConfig">
 * customer-list.htm  ->  com.mycorp.page.CustomerListPage  -> customerListPage
 * HTML Request           Click Page Class                     Spring Bean Name </pre>
 *
 * When using this strategy use the PageScopeResolver class to ensure new Page
 * instances are created with each request, rather than Spring's default
 * "singleton" creation policy. Please see the {@link PageScopeResolver} Javadoc
 * for more information on configuring this option.
 * <p/>
 * An example Page class is provided below which uses the Spring &#64;Component annotation.
 * Note in this example page the customerService with the &#64;Resource
 * annotation is injected by Spring after the page instance has been instantiated.
 *
 * <pre class="prettyprint">
 * package com.mycorp.page;
 *
 * import javax.annotation.Resource;
 * import org.apache.click.Page;
 * import org.springframework.stereotype.Component;
 *
 * import com.mycorp.service.CustomerService;
 *
 * &#64;Component
 * public class CustomerListPage extends Page {
 *
 *     &#64;Resource(name="customerService")
 *     private CustomerService customerService;
 *
 *     ..
 * } </pre>
 *
 * This is the most powerful and convenient Spring integration option, but does
 * require Spring 2.5.x or later.
 *
 * <p/>
 * <b><a name="stateful-page-caveat"></a>Stateful page caveat:</b> Spring beans
 * injected on stateful pages will be serialized along with the page, meaning
 * those beans must implement the Serializable interface. If you do
 * not want the beans to be serialized, they need to be marked as
 * <tt>transient</tt>. Transient beans won't be serialized but when the page
 * is deserialized, the transient beans won't be re-injected, causing a
 * NullPointerException when invoked. If you want to use transient beans on
 * stateful pages, see <a href="#option3">option 3</a> below.
 *
 * <h3><a name="option2"></a>2. Spring instantiated Pages with Spring XML
 * configuration</h3>
 *
 * With this option Page classes are configured using Spring XML configuration.
 * When the SpringClickServlet receives a page request it converts the auto-mapped
 * page class to the equivalent Spring bean name and gets a new instance from the
 * Spring ApplicationContext.
 *
 * <pre class="codeConfig">
 * customer-list.htm  ->  com.mycorp.page.CustomerListPage  -> customerListPage
 * HTML Request           Click Page Class                     Spring Bean Name </pre>
 *
 * If the page bean is not found in the ApplicationContxt then the full Page
 * class name is used.
 *
 * <pre class="codeConfig">
 * customer-list.htm  ->  com.mycorp.page.CustomerListPage  -> com.mycorp.page.CustomerListPage
 * HTML Request           Click Page Class                     Spring Bean Name </pre>
 *
 * This integration option requires you to configure all your Spring Page beans
 * in your Spring XML configuration. While this may be quite laborious, it does
 * support Spring 1.x or later. An example page bean configuration is
 * provided below:
 *
 * <pre class="prettyprint">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;beans&gt;
 *
 *    &lt;bean id="customerListPage" class="com.mycorp.page.CustomerListPage" scope="prototype"/&gt;
 *
 * &lt;/beans&gt; </pre>
 *
 * <b>Please Note</b> ensure the page beans scope is set to "prototype" so a new
 * page instance will be created with every HTTP request. Otherwise Spring will
 * default to using singletons and your code will not be thread safe.
 * <p/>
 * <b>Stateful page caveat:</b> option 2 has the same caveat as
 * <a href="#stateful-page-caveat">option 1</a>.
 *
 * <h3><a name="option3"></a>3. Click instantiated Pages with injected Spring
 * beans and/or ApplicationContext</h3>
 *
 * With this integration option Click will instantiate page instances and
 * automatically inject any page properties which match Spring beans defined in
 * the ApplicationContext. In order to enable bean injection, you need to
 * configure the SpringClickServlet init parameter:
 * <a href="#inject-page-beans">inject-page-beans</a>.
 * <p/>
 * While this option is not as powerful as &#64;Component configured pages it is
 * much more convenient than Spring XML configured pages and supports Spring 1.x.
 * You can also use annotation based injection which requires Spring 2.5.x
 * or later.
 * <p/>
 * An example Page class is provided below which has the customerService property
 * automatically injected by the SpringClickServlet. Note the customerService
 * property will need to be defined in a Spring XML configuration.
 *
 * <pre class="prettyprint">
 * package com.mycorp.page;
 *
 * import org.apache.click.Page;
 *
 * import com.mycorp.service.CustomerService;
 *
 * public class CustomerListPage extends Page {
 *
 *     private CustomerService customerService;
 *
 *     public void setCustomerService(CustomerService customerService) {
 *         this.customerService = customerService;
 *     }
 *
 *     ..
 * } </pre>
 *
 * Page property bean name must match the bean name defined in the Spring XML
 * configuration. Continuing our example the Spring XML configuration is provided
 * below:
 *
 * <pre class="prettyprint">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;beans&gt;
 *
 *    &lt;bean id="customerService" class="com.mycorp.service.CustomerService"/&gt;
 *
 * &lt;/beans&gt; </pre>
 *
 * This option will also automatically inject the ApplicationContext into
 * page instances which implement the {@link org.springframework.context.ApplicationContextAware}
 * interface. Using the applicationContext you can lookup Spring beans manually
 * in your pages. For example:
 *
 * <pre class="prettyprint">
 * public class CustomerListPage extends Page implements ApplicationContextAware {
 *
 *     protected ApplicationContext applicationContext;
 *
 *     public void setApplicationContext(ApplicationContext applicationContext)  {
 *         this.applicationContext = applicationContext;
 *     }
 *
 *     public CustomerService getCustomerService() {
 *         return (CustomerService) applicationContext.getBean("customerService");
 *     }
 * } </pre>
 *
 * This last strategy is probably the least convenient integration option.
 *
 * <h4><a name="option31"></a>3.1 Spring beans and Stateful pages</h4>
 *
 * Stateful pages are stored in the HttpSession and Spring beans referenced
 * by a stateful page must implement the Serializable interface. If you do not
 * want beans to be serialized they can be marked as <tt>transient</tt>.
 * Transient beans won't be serialized to disk. However once the page is
 * deserialized the transient beans will need to be injected again.
 * <p/>
 * <a href="#option3">Option 3</a> will re-inject Spring beans and the
 * ApplicationContext after every request. This allows beans to be marked as
 * <tt>transient</tt> and still function properly when used with stateful pages.
 *
 * <pre class="prettyprint">
 * package com.mycorp.page;
 *
 * import org.apache.click.Page;
 *
 * import com.mycorp.service.CustomerService;
 *
 * public class CustomerListPage extends Page implements ApplicationContextAware {
 *
 *     // Note the transient keyword
 *     private transient CustomerService customerService;
 *
 *     protected transient ApplicationContext applicationContext;
 *
 *     public CustomerListPage {
 *         // Page is marked as stateful
 *         setStateful(true);
 *     }
 *
 *     // Inject the customer service
 *     public void setCustomerService(CustomerService customerService) {
 *         this.customerService = customerService;
 *     }
 *
 *     public CustomerService getCustomerService() {
 *         return (CustomerService) applicationContext.getBean("customerService");
 *     }
 *
 *     // Inject Spring's ApplicationContext
 *     public void setApplicationContext(ApplicationContext applicationContext)  {
 *         this.applicationContext = applicationContext;
 *     }
 *
 *     ..
 * } </pre>
 *
 * <h3>Servlet Configuration</h3>
 *
 * The SpringClickServlet can obtain the ApplicationContext either from
 * {@link org.springframework.web.context.support.WebApplicationContextUtils} which is configured with a
 * {@link org.springframework.web.context.ContextLoaderListener}. For example:
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;web-app&gt;
 *
 *    &lt;listener&gt;
 *       &lt;listener-class&gt;
 *          <span class="blue">org.springframework.web.context.ContextLoaderListener</span>
 *       &lt;/listener-class&gt;
 *    &lt;/listener&gt;
 *
 *    &lt;servlet&gt;
 *       &lt;servlet-name&gt;SpringClickServlet&lt;/servlet-name&gt;
 *       &lt;servlet-class&gt;org.apache.click.extras.spring.SpringClickServlet&lt;/servlet-class&gt;
 *       &lt;load-on-startup&gt;0&lt;/load-on-startup&gt;
 *    &lt;/servlet&gt;
 *
 *    ..
 *
 * &lt;/web-app&gt; </pre>
 *
 * Alternatively you can specify the path to the ApplicationContext as a
 * servlet init parameter. For example:
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;web-app&gt;
 *
 *    &lt;servlet&gt;
 *       &lt;servlet-name&gt;SpringClickServlet&lt;/servlet-name&gt;
 *       &lt;servlet-class&gt;org.apache.click.extras.spring.SpringClickServlet&lt;/servlet-class&gt;
 *       &lt;init-param&gt;
 *         &lt;param-name&gt;<span class="blue">spring-path</span>&lt;/param-name&gt;
 *         &lt;param-value&gt;<span class="red">/applicationContext.xml</span>&lt;/param-value&gt;
 *       &lt;/init-param&gt;
 *       &lt;load-on-startup&gt;0&lt;/load-on-startup&gt;
 *    &lt;/servlet&gt;
 *
 *    ..
 *
 * &lt;/web-app&gt; </pre>
 *
 * <a name="inject-page-beans"></a>To configure page Spring bean injection
 * (<a href="#option3">option 3</a> above), you need to configure the
 * <span class="blue">inject-page-beans</span> servlet init parameter. For example:
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;web-app&gt;
 *
 *    ..
 *
 *    &lt;servlet&gt;
 *       &lt;servlet-name&gt;SpringClickServlet&lt;/servlet-name&gt;
 *       &lt;servlet-class&gt;org.apache.click.extras.spring.SpringClickServlet&lt;/servlet-class&gt;
 *       &lt;init-param&gt;
 *         &lt;param-name&gt;<span class="blue">inject-page-beans</span>&lt;/param-name&gt;
 *         &lt;param-value&gt;<span class="red">true</span>&lt;/param-value&gt;
 *       &lt;/init-param&gt;
 *       &lt;load-on-startup&gt;0&lt;/load-on-startup&gt;
 *    &lt;/servlet&gt;
 *
 *    ..
 *
 * &lt;/web-app&gt; </pre>
 *
 * @see PageScopeResolver
 */
public class SpringClickServlet extends ClickServlet {

    private static final long serialVersionUID = 1L;

    /**
     * The Servlet initialization parameter name for the option to have the
     * SpringClickServlet inject Spring beans into page instances: &nbsp;
     * <tt>"inject-page-beans"</tt>.
     */
    public static final String INJECT_PAGE_BEANS = "inject-page-beans";

    /**
     * The Servlet initialization parameter name for the path to the Spring XML
     * application context definition file: &nbsp; <tt>"spring-path"</tt>.
     */
    public static final String SPRING_PATH = "spring-path";

    /** The set of setter methods to ignore. */
    static final Set<String> SETTER_METHODS_IGNORE_SET = new HashSet<String>();

    // Initialize the setter method ignore set
    static {
        SETTER_METHODS_IGNORE_SET.add("setApplicationContext");
        SETTER_METHODS_IGNORE_SET.add("setFormat");
        SETTER_METHODS_IGNORE_SET.add("setForward");
        SETTER_METHODS_IGNORE_SET.add("setHeader");
        SETTER_METHODS_IGNORE_SET.add("setHeaders");
        SETTER_METHODS_IGNORE_SET.add("setPageImports");
        SETTER_METHODS_IGNORE_SET.add("setPath");
        SETTER_METHODS_IGNORE_SET.add("setStateful");
        SETTER_METHODS_IGNORE_SET.add("setRedirect");
        SETTER_METHODS_IGNORE_SET.add("setTemplate");
    }

    /** Spring application context bean factory. */
    protected ApplicationContext applicationContext;

    /** The list of page injectable Spring beans, keyed on page class name. */
    protected Map<Class<? extends Page>, List<BeanNameAndMethod>> pageSetterBeansMap =
        new HashMap<Class<? extends Page>, List<BeanNameAndMethod>>();

    // Public Methods ----------------------------------------------------------

    /**
     * Initialize the SpringClickServlet and the Spring application context
     * bean factory. An Spring <tt>ClassPathXmlApplicationContext</tt> bean
     * factory is used and initialize with the servlet <tt>init-param</tt>
     * named <tt>"spring-path"</tt>.
     *
     * @see ClickServlet#init()
     *
     * @throws ServletException if the click app could not be initialized
     */
    @Override
    public void init() throws ServletException {
        super.init();

        ServletContext servletContext = getServletContext();
        applicationContext =
            WebApplicationContextUtils.getWebApplicationContext(servletContext);

        if (applicationContext == null) {
            String springPath = getInitParameter(SPRING_PATH);
            if (springPath == null) {
                String msg =
                    SPRING_PATH + " servlet init parameter not defined";
                throw new UnavailableException(msg);
            }

            applicationContext = new ClassPathXmlApplicationContext(springPath);
        }

        String injectPageBeans = getInitParameter(INJECT_PAGE_BEANS);
        if ("true".equalsIgnoreCase(injectPageBeans)) {

            // Process page classes looking for setter methods which match beans
            // available in the applicationContext
            List<Class<? extends Page>> pageClassList = getConfigService().getPageClassList();
            for (Class<? extends Page> pageClass : pageClassList) {
                loadSpringBeanSetterMethods(pageClass);
            }
        }
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Create a new Spring Page bean if defined in the application context, or
     * a new Page instance otherwise.
     * <p/>
     * If the "inject-page-beans" option is enabled this method will inject
     * any Spring beans matching the Page's properties.
     *
     * @see ClickServlet#newPageInstance(String, Class, HttpServletRequest)
     *
     * @param path the request page path
     * @param pageClass the page Class the request is mapped to
     * @param request the page request
     * @return a new Page object
     * @throws Exception if an error occurs creating the Page
     */
    @Override
    protected Page newPageInstance(String path, Class<? extends Page> pageClass,
            HttpServletRequest request) throws Exception {

        Page page = null;

        String beanName = toBeanName(pageClass);

        if (getApplicationContext().containsBean(beanName)) {
            page = (Page) getApplicationContext().getBean(beanName);

        } else if (getApplicationContext().containsBean(pageClass.getName())) {
            page = (Page) getApplicationContext().getBean(pageClass.getName());

        } else {
            page = pageClass.newInstance();
        }

        return page;
    }

    /**
     * Return the configured Spring application context.
     *
     * @return the configured Spring application context.
     */
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * This method associates the <tt>ApplicationContext</tt> with any
     * <tt>ApplicationContextAware</tt> pages and supports the deserialization
     * of stateful pages.
     *
     * @see ClickServlet#activatePageInstance(Page)
     *
     * @param page the page instance to activate
     */
    @Override
    protected void activatePageInstance(Page page) {
        ApplicationContext applicationContext = getApplicationContext();

        if (page instanceof ApplicationContextAware) {
            ApplicationContextAware aware =
                (ApplicationContextAware) page;
            aware.setApplicationContext(applicationContext);
        }

        Class<? extends Page> pageClass = page.getClass();
        String beanName = toBeanName(pageClass);

        if (applicationContext.containsBean(beanName)
            || applicationContext.containsBean(pageClass.getName())) {

            // Beans are injected through Spring
        } else {

            // Inject any Spring beans into the page instance
            if (!pageSetterBeansMap.isEmpty()) {
                // In development mode, lazily loaded page classes won't have
                // their bean setters methods mapped, thus beans won't be injected
                List<BeanNameAndMethod> beanList = pageSetterBeansMap.get(page.getClass());
                if (beanList != null) {
                    for (BeanNameAndMethod bnam : beanList) {
                        Object bean = applicationContext.getBean(bnam.beanName);

                        try {
                            Object[] args = { bean };
                            bnam.method.invoke(page, args);

                        } catch (Exception error) {
                            throw new RuntimeException(error);
                        }
                    }
                }
            }
        }
    }

    /**
     * Return the Spring beanName for the given class.
     *
     * @param aClass the class to get the Spring bean name from
     * @return the class bean name
     */
    protected String toBeanName(Class<?> aClass) {
        return Introspector.decapitalize(aClass.getSimpleName());
    }

    // Package Private Inner Classes ------------------------------------------

    /**
     * Provides a Spring bean name and page bean property setter method holder.
     */
    static class BeanNameAndMethod {

        /** The Spring bean name. */
        protected final String beanName;

        /** The page bean property setter method. */
        protected final Method method;

        /**
         * Create a new String bean name and page setter method object.
         *
         * @param beanName the spring bean name
         * @param method the page setter method for the bean
         */
        protected BeanNameAndMethod(String beanName, Method method) {
            this.beanName = beanName;
            this.method = method;
        }
    }

    // Private Methods --------------------------------------------------------

    /**
     * Load the pageClass bean setter methods
     *
     * @param pageClass the page class
     */
    private void loadSpringBeanSetterMethods(Class<? extends Page> pageClass) {

        Method[] methods = pageClass.getMethods();
        for (int j = 0; j < methods.length; j++) {
            Method method = methods[j];
            String methodName = method.getName();

            if (methodName.startsWith("set")
                && !SETTER_METHODS_IGNORE_SET.contains(methodName)
                && method.getParameterTypes().length == 1) {

                // Get the bean name from the setter method name
                HtmlStringBuffer buffer = new HtmlStringBuffer();
                buffer.append(Character.toLowerCase(methodName.charAt(3)));
                buffer.append(methodName.substring(4));
                String beanName = buffer.toString();

                // If Spring contains the bean then cache in map list
                if (getApplicationContext().containsBean(beanName)) {
                    List<BeanNameAndMethod> beanList = pageSetterBeansMap.get(pageClass);
                    if (beanList == null) {
                        beanList = new ArrayList<BeanNameAndMethod>();
                        pageSetterBeansMap.put(pageClass, beanList);
                    }

                    beanList.add(new BeanNameAndMethod(beanName, method));
                }
            }
        }
    }
}
