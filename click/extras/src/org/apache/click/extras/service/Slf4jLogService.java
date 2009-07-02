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
package org.apache.click.extras.service;


import javax.servlet.ServletContext;

import org.apache.click.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a
 * <a target="_blank" href="http://www.slf4j.org/">SLF4J</a> LogService adapter
 * class with a logger name of "<tt>Click</tt>".
 *
 * <h3>Configuration</h3>
 * To configure the JDK LoggingService add the following element to your
 * <tt>click.xml</tt> configuration file.
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;click-app charset="UTF-8"&gt;
 *
 *     &lt;pages package="org.apache.click.examples.page"/&gt;
 *
 *     &lt;<span class="red">log-service</span> classname="<span class="blue">org.apache.click.extras.service.Slf4jLogService</span>"/&gt;
 *
 * &lt;/click-app&gt; </pre>
 */
public class Slf4jLogService implements LogService {

    /** The wrapped JDK logger instance. */
    protected Logger logger;

    /** The logger category name. The default value is "<tt>Click</tt>". */
    protected String name = "Click";

    /**
     * @see LogService#onInit(javax.servlet.ServletContext)
     *
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the LogService
     */
    public void onInit(ServletContext servletContext) throws Exception {
        logger = LoggerFactory.getLogger(getName());
    }

    /**
     * @see LogService#onDestroy()
     */
    public void onDestroy() {
    }

    /**
     * @see LogService#debug(Object)
     *
     * @param message the message to log
     */
    public void debug(Object message) {
        logger.debug(String.valueOf(message));
    }

    /**
     * @see LogService#debug(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void debug(Object message, Throwable error) {
        logger.debug(String.valueOf(message), error);
    }

    /**
     * @see LogService#error(Object)
     *
     * @param message the message to log
     */
    public void error(Object message) {
        logger.error(String.valueOf(message));
    }

    /**
     * @see LogService#error(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void error(Object message, Throwable error) {
        logger.error(String.valueOf(message), error);
    }

    /**
     * @see LogService#info(Object)
     *
     * @param message the message to log
     */
    public void info(Object message) {
        logger.info(String.valueOf(message));
    }

    /**
     * @see LogService#info(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void info(Object message, Throwable error) {
        logger.info(String.valueOf(message), error);
    }

    /**
     * @see LogService#isDebugEnabled()
     *
     * @return true if [debug] level logging is enabled
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * @see LogService#isInfoEnabled()
     *
     * @return true if [info] level logging is enabled
     */
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    /**
     * @see LogService#isTraceEnabled()
     *
     * @return true if [trace] level logging is enabled
     */
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * @see LogService#trace(Object)
     *
     * @param message the message to log
     */
    public void trace(Object message) {
        logger.trace(String.valueOf(message));
    }

    /**
     * @see LogService#trace(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void trace(Object message, Throwable error) {
        logger.trace(String.valueOf(message), error);
    }

    /**
     * @see LogService#warn(Object)
     *
     * @param message the message to log
     */
    public void warn(Object message) {
        logger.warn(String.valueOf(message));
    }

    /**
     * @see LogService#warn(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void warn(Object message, Throwable error) {
        logger.warn(String.valueOf(message), error);
    }

    /**
     * Return the logger category name.
     *
     * @return the logger category name
     */
    public String getName() {
        return name;
    }

    /**
     * The logger category name. Setting the name after the <tt>onInit()</tt>
     * method has bee invoked will have no effect on the Log4J loggers name.
     *
     * @param name the logger category name to set
     */
    public void setName(String name) {
        this.name = name;
    }


}
