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

import org.apache.click.Page;
import org.apache.click.util.ClickUtils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

/**
 * Provides a Spring bean scope resolver when using Spring
 * instantiated pages with the &#64;Component annotation.
 * <p/>
 * This scope meta data resolver will resolve "prototype" scope for any Click
 * Page bean, and "singleton" scope for any other bean.
 *
 * <h3>Example &#64;Component Page</h3>
 * An example Page class is provided below which uses the Spring &#64;Component annotation.
 *
 * <pre class="prettyprint">
 * package com.mycorp.page;
 *
 * import javax.annotation.Resource;
 * import org.apache.click.Page;
 * import org.springframework.stereotype.Component;
 *
 * import comp.mycorp.service.CustomerService;
 *
 * &#64;Component
 * public class CustomerEditPage extends Page {
 *
 *     &#64;Resource(name="customerService")
 *     private CustomerService customerService;
 *
 *     ..
 * } </pre>
 *
 * Note in this example page the customerService with the &#64;Resource
 * annotation is injected by Spring after the page instance has been instantiated.
 *
 * <h3>Example Spring Configuration</h3>
 * An example Spring XML configuration is provided below.
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;beans xmlns="http://www.springframework.org/schema/beans"
 *      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *      xmlns:context="http://www.springframework.org/schema/context"
 *      xsi:schemaLocation="
 *      http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
 *      http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd"&gt;
 *
 *   &lt;context:component-scan base-package="<span class="red">com.mycorp</span>" scope-resolver=<span class="blue">"org.apache.click.extras.spring.PageScopeResolver"</span>/&gt;
 *
 * &lt;/beans&gt; </pre>
 *
 * In this example any page class under the base package "com.mycorp" which
 * includes the &#64;Component annotation will have "prototype" scope.
 *
 * @see SpringClickServlet
 */
public class PageScopeResolver implements ScopeMetadataResolver {

    /**
     * Return the scope meta data for the given bean definition. This scope meta
     * data resolver will resolve "prototype" scope for any Click Page bean
     * and will resolve "singleton" scope for other beans.
     *
     * @see ScopeMetadataResolver#resolveScopeMetadata(BeanDefinition)
     *
     * @param beanDef the component bean definition to resolve
     * @return the scope meta data for the given bean definition.
     */
    public ScopeMetadata resolveScopeMetadata(BeanDefinition beanDef) {
        ScopeMetadata sm = new ScopeMetadata();

        try {
            Class<?> beanClass = ClickUtils.classForName(beanDef.getBeanClassName());

            if (Page.class.isAssignableFrom(beanClass)) {
                sm.setScopeName(ConfigurableBeanFactory.SCOPE_PROTOTYPE);

            } else {
                // TODO: see whether we can determine the default scope definition
                // from the beanDef and return this instead.
                sm.setScopeName(ConfigurableBeanFactory.SCOPE_SINGLETON);
            }

            return sm;

        } catch (Exception e) {
            String msg = "Could not load class for beanDef: " + beanDef;
            throw new RuntimeException(msg, e);
        }
    }

}
