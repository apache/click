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

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Provides an example Spring framework integration <tt>ClickServlet</tt>.
 * <p/>
 *
 *
 * @author Phil Barnes
 * @author Paul Rule
 * @author Malcolm Edgar
 */
public class SpringClickServlet extends ClickServlet {

    /**
     * The path to the Spring XML appliation context definition file:
     * &nbsp; <tt>"spring-path"</tt>
     */
    public static final String SPRING_PATH = "spring-path";

    /** Spring bean factory. */
    protected BeanFactory beanFactory;

    /**
     * @see ClickServlet#init()
     */
    public void init() throws ServletException {
        super.init();

        String springPath = getInitParameter(SPRING_PATH);
        if (springPath == null) {
            String msg = SPRING_PATH + " servlet init parameter not defined";
            throw new UnavailableException(msg);
        }
        beanFactory = new ClassPathXmlApplicationContext(springPath);
    }

    /**
     * @see ClickServlet#newPageInstance(String, Class)
     */
    protected Page newPageInstance(String path, Class pageClass) throws Exception {
        String beanName = path.substring(0, path.indexOf("."));

        if (beanFactory.containsBean(beanName)) {
            return (Page) beanFactory.getBean(beanName);

        } else {
            return (Page) pageClass.newInstance();
        }
    }

}
