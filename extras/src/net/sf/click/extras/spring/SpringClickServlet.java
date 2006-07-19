/*
 * Copyright 2004-2006 Malcolm A. Edgar
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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;

import net.sf.click.ClickServlet;
import net.sf.click.Page;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Provides an example Spring framework integration <tt>SpringClickServlet</tt>.
 *
 * <h3>Page Creation</h3>
 *
 * This specialized Click Servlet can inject Spring dependencies into
 * defined spring Page beans. If a requested Page is not configured as a Spring
 * bean, then a plain new Page instance is created.
 * <p/>
 * The SpringClickServlet overrides the ClickServlet method <tt>newPageInstance()</tt>
 * to provide new Page instances:
 *
 * <pre class="codeJava">
 * <span class="kw">protected</span> Page newPageInstance(String path, Class pageClass, HttpServletRequest request)
 *     <span class="kw">throws</span> Exception {
 *
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
 * The SpringClickServlet support Spring Page injection in two ways.
 *
 * <h4>Spring Instantiated Pages</h4>
 *
 * With Spring instantiated pages you define your Pages as beans in a Spring
 * appliction context XML file. For example in this file the Page bean id maps
 * to the page class name:
 *
 * <pre class="codeConfig">
 * &lt;beans&gt;
 *
 *    &lt;bean id="com.mycorp.pages.CustomerEdit" class="com.mycorp.pages.CustomerEdit"
 *         singleton="false"&gt;
 *       &lt;property name="userService" ref="userService"/&gt;
 *    &lt;/bean&gt;
 *
 * &lt;/beans&gt; </pre>
 *
 * <b>Please Note</b> ensure that your Page bean is not a singleton, otherwise
 * the page instance will not be thread safe.
 *<p/>
 * Using this technique the SpringClickServlet will look up the Page bean and
 * have Spring create the page instance and inject all its dependencies.
 *
 * <h4>Click Instantiated Pages</h4>
 *
 * With Click instantiated pages you have your Page classes implement the Spring
 * {@link org.springframework.context.ApplicationContextAware} interface.
 * The SpringClickServlet then create the Page instance an inject the Spring
 * <tt>ApplicationContext</tt> instance.
 * <p/>
 * The advantage of using this technique is that you don't need to
 * define your Pages as beans in Spring configuration files. However you will
 * need hard code acessor methods in you Click pages. For example:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> SpringPage <span class="kw">extends</span> Page <span class="kw">implements</span> ApplicationContextAware {
 *
 *     <span class="kw">protected</span> ApplicationContext applicationContext;
 *
 *     <span class="kw">public void</span> setApplicationContext(ApplicationContext applicationContext)  {
 *         <span class="kw">this</span>.applicationContext = applicationContext;
 *     }
 *
 *     <span class="kw">public</span> Object getBean(String beanName) {
 *         <span class="kw">return</span> applicationContext.getBean(beanName);
 *     }
 *
 *     <span class="kw">public</span> UserService getUserService() {
 *         <span class="kw">return</span> (UserService) getBean(<span class="st">"userService"</span>);
 *     }
 * } </pre>
 *
 * <h3>Application Context Configuration</h3>
 *
 * By convention Spring beans are defined in an <tt>applicationContext.xml</tt>
 * file. The Spring runtime needs to be initialized with location of this
 * file so that it can build up the configured Spring beans. There are two
 * typical location options for the <tt>applicationContext.xml</tt> file.
 *
 * <h4>WEB-INF Directory</h4>
 *
 * You can place the file under your WEB-INF directory, adjacent to your web.xml
 * file.
 *
 * <pre class="codeConfig">
 * /WEB-INF/applicationContext.xml </pre>
 *
 * To use this option configure a Spring
 * {@link org.springframework.web.context.ContextLoaderListener} in your
 * <tt>web.xml</tt> file. For example:
 *
 * <pre class="codeConfig">
 * &lt;web-app&gt;
 *
 *    &lt;listener&gt;
 *       &lt;listener-class&gt;
 *          <font color="blue">org.springframework.web.context.ContextLoaderListener</font>
 *       &lt;/listener-class&gt;
 *    &lt;/listener&gt;
 *
 *    &lt;servlet&gt;
 *       &lt;servlet-name&gt;click-servlet&lt;/servlet-name&gt;
 *       &lt;servlet-class&gt;net.sf.click.extras.spring.SpringClickServlet&lt;/servlet-class&gt;
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
 * The advantage of this configuration option is that any changes made to the
 * <tt>applicationContext.xml</tt> file during development will be
 * automatically loaded by the Spring runtime.
 *
 * <h4>Class Path</h4>
 *
 * The second configuration option is to locate the <tt>applicationContext.xml</tt>
 * file on the class path.
 *
 * <pre class="codeConfig">
 * /WEB-INF/classes/applicationContext.xml </pre>
 *
 * To use this configration option add a
 * <tt class="blue">spring-path</tt> servlet initialization parameter which
 * specifies the files class path location to the <tt>SpringClickServlet</tt>
 * servlet config. For example:
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
 * The advantage of this confirguration option is that you can locate your
 * <tt>applicationContext.xml</tt> file in any classpath location. For instance
 * you may package your Spring business tier objects in a separate JAR file
 * which you include with your web application.
 *
 * <h3>Servlet Intialization</h3>
 *
 * Now that we have discussed the configuration options, below you see how the
 * <tt>ClickSpringServlet</tt> loads the
 * {@link org.springframework.context.ApplicationContext} at
 * startup:
 *
 * <pre class="codeJava">
 * <span class="kw">public void</span> init() <span class="kw">throws</span>ServletException {
 *     <span class="kw">super</span>.init();
 *
 *     ServletContext servletContext = getServletContext();
 *     applicationContext =
 *         WebApplicationContextUtils.getWebApplicationContext(servletContext);
 *
 *     <span class="kw">if</span> (applicationContext == <span class="kw">null</span>) {
 *         String springPath = getInitParameter(SPRING_PATH);
 *         <span class="kw">if</span> (springPath == <span class="kw">null</span>) {
 *             String msg = SPRING_PATH + <span class="st">" servlet init parameter not defined"</span>;
 *             <span class="kw">throw new</span> UnavailableException(msg);
 *         }
 *         applicationContext = <span class="kw">new</span> ClassPathXmlApplicationContext(springPath);
 *     }
 * } </pre>
 *
 * <h3>Examples</h3>
 *
 * Please see the Click Examples application for a demonstration of Spring integration.
 *
 * @author Phil Barnes
 * @author Paul Rule
 * @author Malcolm Edgar
 */
public class SpringClickServlet extends ClickServlet {

    private static final long serialVersionUID = -8251140780990964857L;

    /**
     * The path to the Spring XML appliation context definition file:
     * &nbsp; <tt>"spring-path"</tt>.
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
     *
     * @throws ServletException if the click app could not be initialized
     */
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
    }

    /**
     * Create a new Spring Page bean if defined in the application context, or
     * a new Page instance otherwise. The bean name used is the full class name
     * of the given pageClass.
     * <p/>
     * If the Page implements the <tt>ApplicationContextAware</tt> interface
     * this method will set the application context in the newly created page.
     *
     * @see ClickServlet#newPageInstance(String, Class, HttpServletRequest)
     *
     * @param path the request page path
     * @param pageClass the page Class the request is mapped to
     * @param request the page request
     * @return a new Page object
     * @throws Exception if an error occurs creating the Page
     */
    protected Page newPageInstance(String path, Class pageClass,
            HttpServletRequest request) throws Exception {

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
