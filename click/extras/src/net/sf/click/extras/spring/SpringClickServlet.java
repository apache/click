/*
 * Copyright 2004-2005 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.extras.spring;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import net.sf.click.ClickServlet;
import net.sf.click.Page;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Provides an example Spring framework integration <tt>SpringClickServlet</tt>.
 * <p/>
 * This specialized Click Servlet can inject Spring dependencies into
 * defined spring Page beans. If a requested Page is not configured as a Spring
 * bean, then a plain new Page instance is created.
 * <p/>
 * The SpringClickServlet overrides the ClickServlet method <tt>newPageInstance()</tt>
 * to provide new Page instances:
 *
 * <pre class="codeJava">
 * <span class="kw">protected</span> Page newPageInstance(String path, Class pageClass) <span class="kw">throws</span> Exception {
 *     Page page = <span class="kw">null</span>;
 *
 *     String beanName = pageClass.getName();
 *
 *     <span class="kw">if</span> (applicationContext.containsBean(beanName)) {
 *         Page page = (Page) applicationContext.getBean(beanName);
 *
 *     } <span class="kw">else</span> {
 *         page = (Page) pageClass.newIntance();
 *     }
 *
 *     <span class="kw">if</span> (page instanceof ApplicationContextAware) {
 *         ApplicationContextAware aware =
 *             (ApplicationContextAware) page;
 *         aware.setApplicationContext(applicationContext);
 *     }
 *
 *     <span class="kw">return</span> page;
 * } </pre>
 *
 * Spring Page beans are defined in a Sring application context XML file.
 * In this file the Page bean id maps to the page class name:
 *
 * <pre class="codeConfig">
 * &lt;beans default-autowire="byName"&gt;
 *   &lt;bean id="com.mycorp.pages.CustomerEdit"
 *         class="com.mycorp.pages.CustomerEdit"
 *         singleton="false"/&gt;
 *   ..
 * &lt;/beans&gt; </pre>
 *
 * <p/>
 * The servlets Spring bean factory is configured when the servlet is initialized on
 * startup:
 * <pre class="codeJava">
 * <span class="kw">public void</span> init() <span class="kw">throws</span>ServletException {
 *     <span class="kw">super</span>.init();
 *
 *     String springPath = getInitParameter(SPRING_PATH);
 *     <span class="kw">if</span> (springPath == <span class="kw">null</span>) {
 *         String msg = SPRING_PATH + <span class="st">" servlet init parameter not defined"</span>;
 *         <span class="kw">throw new</span> UnavailableException(msg);
 *     }
 *     applicationContext = <span class="kw">new</span> ClassPathXmlApplicationContext(springPath);
 * } </pre>
 *
 * The <tt class="blue">spring-path</tt> servlet initialization parameter
 * specifies the class path of the Spring application context XML file.
 * This servet initialization parameter is specified in <tt>WEB-INF/web.xml</tt>:
 *
 * <pre class="codeConfig">
 * &lt;web-app&gt;
 *
 *    &lt;servlet&gt;
 *       &lt;servlet-name&gt;click-servlet&lt;/servlet-name&gt;
 *       &lt;servlet-class&gt;net.sf.click.extras.spring.SpringClickServlet&lt;/servlet-class&gt;
 *       &lt;init-param&gt;
 *         &lt;param-name&gt;<font color="blue">spring-path</font>&lt;/param-name&gt;
 *         &lt;param-value&gt;<font color="red">/applicationContext.xml</font>&lt;/param-value&gt;
 *       &lt;/init-param&gt;
 *       &lt;load-on-startup&gt;0&lt;/load-on-startup&gt;
 *    &lt;/servlet&gt;
 *
 *    &lt;servlet-mapping&gt;
 *       &lt;servlet-name&gt;click-servlet&lt;/servlet-name&gt;
 *       &lt;url-pattern&gt;*.htm&lt;/url-pattern&gt;
 *    &lt;/servlet-mapping&gt;
 *
 * &lt;/web-app&gt; </pre>
 *
 * @author Phil Barnes
 * @author Paul Rule
 * @author Malcolm Edgar
 */
public class SpringClickServlet extends ClickServlet {

    private static final long serialVersionUID = -8251140780990964857L;

    /**
     * The path to the Spring XML appliation context definition file:
     * &nbsp; <tt>"spring-path"</tt>
     */
    public static final String SPRING_PATH = "spring-path";

    /** Spring application context bean factory. */
    protected ApplicationContext applicationContext;

    /**
     * Initialize the SpringClickServlet and the Spring application context
     * bean factory. An Spring <tt>ClassPathXmlApplicationContext</tt> bean
     * factory is used and initialize with the servlet <tt>init-param</tt>
     * named <tt>"spring-path"</tt>.
     *
     * @see ClickServlet#init()
     */
    public void init() throws ServletException {
        super.init();

        String springPath = getInitParameter(SPRING_PATH);
        if (springPath == null) {
            String msg = SPRING_PATH + " servlet init parameter not defined";
            throw new UnavailableException(msg);
        }
        applicationContext = new ClassPathXmlApplicationContext(springPath);
    }

    /**
     * Create a new Spring Page bean if defined in the application context, or
     * a new Page instance otherwise. The bean name used is the full class name
     * of the given pageClass.
     * <p/>
     * If the Page implements the <tt>ApplicationContextAware</tt> interface
     * this method will set the application context in the newly created page.
     *
     * @see ClickServlet#newPageInstance(String, Class)
     */
    protected Page newPageInstance(String path, Class pageClass) throws Exception {
        Page page = null;

        String beanName = pageClass.getName();

        if (applicationContext.containsBean(beanName)) {
            page = (Page) applicationContext.getBean(beanName);

        } else {
            page = (Page) pageClass.newInstance();
        }

        if (page instanceof ApplicationContextAware) {
            ApplicationContextAware aware =
                (ApplicationContextAware) page;
            aware.setApplicationContext(applicationContext);
        }

        return page;
    }

}
