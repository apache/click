/*
 * Copyright 2008 Malcolm A. Edgar
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
package net.sf.click.extras.service;

import javax.servlet.ServletContext;

import net.sf.click.service.LogService;

import org.apache.log4j.Logger;

/**
 * Provides a Log4J LogService adapter class with a logger category name of
 * "Click".
 *
 * @author Malcolm Edgar
 */
public class Log4JLogService implements LogService {

    /** The wrapped Log4J logger instance. */
    protected Logger logger = Logger.getLogger("Click");

    /**
     * @see LogService#onInit(javax.servlet.ServletContext)
     *
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the LogService
     */
    public void onInit(ServletContext servletContext) throws Exception {
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
        logger.debug(message);
    }

    /**
     * @see LogService#debug(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void debug(Object message, Throwable error) {
        logger.debug(message, error);
    }

    /**
     * @see LogService#error(Object)
     *
     * @param message the message to log
     */
    public void error(Object message) {
        logger.error(message);
    }

    /**
     * @see LogService#error(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void error(Object message, Throwable error) {
        logger.error(message, error);
    }

    /**
     * @see LogService#info(Object)
     *
     * @param message the message to log
     */
    public void info(Object message) {
        logger.info(message);
    }

    /**
     * @see LogService#info(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void info(Object message, Throwable error) {
        logger.info(message, error);
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
        logger.trace(message);
    }

    /**
     * @see LogService#trace(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void trace(Object message, Throwable error) {
        logger.trace(message, error);
    }

    /**
     * @see LogService#warn(Object)
     *
     * @param message the message to log
     */
    public void warn(Object message) {
        logger.warn(message);
    }

    /**
     * @see LogService#warn(Object, Throwable)
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void warn(Object message, Throwable error) {
        logger.warn(message, error);
    }

}
