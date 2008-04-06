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
package net.sf.click.service;

import javax.servlet.ServletContext;

/**
 * Provides a log service.
 *
 * @author Malcolm Edgar
 */
public interface LogService {

    /**
     * Initialize the LogService with the given application servlet context.
     * <p/>
     * This method is invoked after the LogService has been constructed.
     *
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the LogService
     */
    public void onInit(ServletContext servletContext) throws Exception;

    /**
     * Destroy the LogService.
     */
    public void onDestroy();

    /**
     * Log the given message at [debug] logging level.
     *
     * @param message the message to log
     */
    public void debug(Object message);

    /**
     * Log the given message and error at [debug] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void debug(Object message, Throwable error);

    /**
     * Log the given message at [error] logging level.
     *
     * @param message the message to log
     */
    public void error(Object message);

    /**
     * Log the given message and error at [error] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void error(Object message, Throwable error);

    /**
     * Log the given message at [info] logging level.
     *
     * @param message the message to log
     */
    public void info(Object message);

    /**
     * Log the given message and error at [info] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void info(Object message, Throwable error);

    /**
     * Log the given message at [trace] logging level.
     *
     * @param message the message to log
     */
    public void trace(Object message);

    /**
     * Log the given message and error at [trace] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void trace(Object message, Throwable error);

    /**
     * Log the given message at [warn] logging level.
     *
     * @param message the message to log
     */
    public void warn(Object message);

    /**
     * Log the given message and error at [warn] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void warn(Object message, Throwable error);

    /**
     * Return true if [debug] level logging is enabled.
     *
     * @return true if [debug] level logging is enabled
     */
    public boolean isDebugEnabled();

    /**
     * Return true if [info] level logging is enabled.
     *
     * @return true if [info] level logging is enabled
     */
    public boolean isInfoEnabled();

    /**
     * Return true if [trace] level logging is enabled.
     *
     * @return true if [trace] level logging is enabled
     */
    public boolean isTraceEnabled();

}
