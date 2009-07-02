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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.click.service.LogService;

/**
 * Provides a JDK Util
 * <a target="_blank" href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/package-summary.html">Logging</a>
 * LogService adapter class with a logger name of "<tt>Click</tt>".
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
 *     &lt;<span class="red">log-service</span> classname="<span class="blue">org.apache.click.extras.service.JdkLogService</span>"/&gt;
 *
 * &lt;/click-app&gt; </pre>
 */
public class JdkLogService implements LogService {

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
        logger = Logger.getLogger(getName());
    }

    /**
     * @see LogService#onDestroy()
     */
    public void onDestroy() {
    }

    /**
     * Log the message at <tt>Level.FINE</tt> level.
     *
     * @see LogService#debug(Object)
     *
     * @param message the message to log
     */
    public void debug(Object message) {
        logger.log(Level.FINE, String.valueOf(message));
    }

    /**
     * Log the message and error at <tt>Level.FINE</tt> level.
     *
     * @see LogService#debug(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void debug(Object message, Throwable error) {
        logger.log(Level.FINE, String.valueOf(message), error);
    }

    /**
     * Log the message at <tt>Level.SEVERE</tt> level.
     *
     * @see LogService#error(Object)
     *
     * @param message the message to log
     */
    public void error(Object message) {
        logger.log(Level.SEVERE, String.valueOf(message));
    }

    /**
     * Log the message and error at <tt>Level.SEVERE</tt> level.
     *
     * @see LogService#error(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void error(Object message, Throwable error) {
        logger.log(Level.SEVERE, String.valueOf(message), error);
    }

    /**
     * Log the message at <tt>Level.INFO</tt> level.
     *
     * @see LogService#info(Object)
     *
     * @param message the message to log
     */
    public void info(Object message) {
        logger.log(Level.INFO, String.valueOf(message));
    }

    /**
     * Log the message and error at <tt>Level.INFO</tt> level.
     *
     * @see LogService#info(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void info(Object message, Throwable error) {
        logger.log(Level.INFO, String.valueOf(message), error);
    }

    /**
     * Is logging enabled at the <tt>Level.FINE</tt> level.
     *
     * @see LogService#isDebugEnabled()
     *
     * @return true if [debug] level logging is enabled
     */
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    /**
     * Is logging enabled at the <tt>Level.INFO</tt> level.
     *
     * @see LogService#isInfoEnabled()
     *
     * @return true if [info] level logging is enabled
     */
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    /**
     * Is Trace logging enabled at the <tt>Level.FINER</tt> level.
     *
     * @see LogService#isTraceEnabled()
     *
     * @return true if [trace] level logging is enabled
     */
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINER);
    }

    /**
     * Log the message at <tt>Level.FINER</tt> level.
     *
     * @see LogService#trace(Object)
     *
     * @param message the message to log
     */
    public void trace(Object message) {
        logger.log(Level.FINER, String.valueOf(message));
    }

    /**
     * Log the message and error at <tt>Level.FINER</tt> level.
     *
     * @see LogService#trace(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void trace(Object message, Throwable error) {
        logger.log(Level.FINER, String.valueOf(message), error);
    }

    /**
     * Log the message at <tt>Level.WARNING</tt> level.
     *
     * @see LogService#warn(Object)
     *
     * @param message the message to log
     */
    public void warn(Object message) {
        logger.log(Level.WARNING, String.valueOf(message));
    }

    /**
     * Log the message and error at <tt>Level.WARNING</tt> level.
     *
     * @see LogService#warn(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void warn(Object message, Throwable error) {
        logger.log(Level.WARNING, String.valueOf(message), error);
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
     * method has been invoked will have no effect on the JDK loggers name.
     *
     * @param name the logger category name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
