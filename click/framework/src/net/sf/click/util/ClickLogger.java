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
package net.sf.click.util;

import javax.servlet.ServletContext;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

/**
 * Provides the internal loggers for the Click and Velocity runtime. By default
 * the runtime loggers will send messages to the console [System.out].
 * <p/>
 * To configure the runtime loggers to send their messages to
 * <a href="../../../../../servlet-api/javax/servlet/ServletContext.html#log(java.lang.String)">ServletContext.log</a>
 * set the config application <b>mode</b> <span class="blue">logto</span> attribute,
 * for example:
 *
 * <pre class="codeConfig">
 * &lt;mode< value="production" <span class="blue">logto</span>="<span class="red">servlet</span>"&gt; </pre>
 *
 * <p/>
 * The ClickLogger is designed to avoid the logging configuration, classpath
 * and appender issues that plague existing logging frameworks.
 *
 * @author Malcolm Edgar
 */
public class ClickLogger implements LogChute {

    // -------------------------------------------------------------- Constants

    /** The logging level Velocity application attribute key. */
    public static final String LOG_LEVEL =
        ClickLogger.class.getName() + ".LOG_LEVEL";

    /** The log to target Velocity application attribute key. */
    public static final String LOG_TO =
        ClickLogger.class.getName() + ".LOG_TO";

    /** The logger instance Velocity application attribute key. */
    private static final String LOG_INSTANCE =
        ClickLogger.class.getName() + ".LOG_INSTANCE";

    /** The level names. */
    private static final String[] LEVELS =
        { " [trace] ", " [debug] ", " [info ] ", " [warn ] ", " [error] " };

    /** The ThreadLocal logger holder. */
    private static final ThreadLocal LOGGER_HOLDER = new ThreadLocal();

    // ----------------------------------------------------- Instance Variables

    /** The logging level. */
    protected int logLevel = DEBUG_ID;

    /** The logger name. */
    protected final String name;

    /**
     * The servlet context to log messages to. If the servlet context is null
     * message will be logged to the console [System.out].
     */
    protected ServletContext servletContext = null;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new Click logger with the given name. The logger name will be
     * written out in the logging messages.
     *
     * @param name the logger name
     */
    public ClickLogger(String name) {
        this.name = name;
    }

    /**
     * Create a new Click logger with the "Velocity" name. This logger instance
     * will be created by the Velocity runtime.
     */
    public ClickLogger() {
        this.name = "Velocity";
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Initialize the logger instance for the Velocity runtime. This method
     * is invoked by the Velocity runtime.
     *
     * @see LogChute#init(RuntimeServices)
     *
     * @param rs the Velocity runtime services
     * @throws Exception if an initialization error occurs
     */
    public void init(RuntimeServices rs) throws Exception {
        String logto = (String) rs.getApplicationAttribute(LOG_TO);
        if ("servlet".equals(logto)) {
            servletContext = (ServletContext)
                rs.getApplicationAttribute(ServletContext.class.getName());
        }

        Integer level = (Integer) rs.getApplicationAttribute(LOG_LEVEL);
        if (level instanceof Integer) {
            setLevel(level.intValue());

        } else {
            String msg = "Could not retrieve LOG_LEVEL from Runtime attributes";
            throw new IllegalStateException(msg);
        }

        rs.setApplicationAttribute(LOG_INSTANCE, this);
    }

    /**
     * Return the ClickLogger instance initialized by the given VelocityEngine.
     *
     * @param ve the VelocityEngine which initialized the logger
     * @return the logger instance initialized by the Velocity runtime
     */
    public static ClickLogger getInstance(VelocityEngine ve) {
        return (ClickLogger) ve.getApplicationAttribute(LOG_INSTANCE);
    }

    /**
     * Return the thread bound Click logger instance.
     *
     * @return the thread bound Click logger instance
     */
    public static ClickLogger getInstance() {
        return (ClickLogger) LOGGER_HOLDER.get();
    }

    /**
     * Set the thread bound Click logger instance.
     *
     * @param logger the Click logger instance to set on the thread
     */
    public static void setInstance(ClickLogger logger) {
        LOGGER_HOLDER.set(logger);
    }

    /**
     * Log the given message and optional error at the specified logging level.
     *
     * @see LogChute#log(int, java.lang.String)
     *
     * @param level the logging level
     * @param message the message to log
     */
    public void log(int level, String message) {
        log(level, message, null);
    }

    /**
     * Log the given message and optional error at the specified logging level.
     * <p/>
     * If you need to customise the Click and Velocity runtime logging for your
     * application modify this method.
     *
     * @see LogChute#log(int, java.lang.String, java.lang.Throwable)
     *
     * @param level the logging level
     * @param message the message to log
     * @param error the optional error to log
     */
    public void log(int level, String message, Throwable error) {
        if (level < logLevel) {
            return;
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.append("[");
        buffer.append(name);
        buffer.append("]");

        buffer.append(LEVELS[level + 1]);
        buffer.append(message);

        if (servletContext != null) {
            if (error != null) {
                servletContext.log(buffer.toString(), error);
            } else {
                servletContext.log(buffer.toString());
            }
        } else {
            if (error != null) {
                System.out.print(buffer.toString());
                error.printStackTrace(System.out);
            } else {
                System.out.println(buffer.toString());
            }
        }
    }

    /**
     * Tell whether or not a log level is enabled.
     *
     * @see LogChute#isLevelEnabled(int)
     *
     * @param level the logging level to test
     * @return true if the given logging level is enabled
     */
    public boolean isLevelEnabled(int level) {
        return logLevel >= level;
    }

    /**
     * Set the logging level
     * <tt>[ TRACE_ID | DEBUG_ID | INFO_ID | WARN_ID | ERROR_ID ]</tt>.
     *
     * @param level the logging level
     */
    public void setLevel(int level) {
        logLevel = level;
    }

    /**
     * Set the servlet context to log messages to. If the servlet context is
     * null messages will be logged to the console [System.out].
     *
     * @param servletContext to log message to
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Log the given message at [debug] logging level.
     *
     * @param message the message to log
     */
    public void debug(Object message) {
        log(DEBUG_ID, String.valueOf(message), null);
    }

    /**
     * Log the given message and error at [debug] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void debug(Object message, Throwable error) {
        log(DEBUG_ID, String.valueOf(message), error);
    }

    /**
     * Log the given message at [error] logging level.
     *
     * @param message the message to log
     */
    public void error(Object message) {
        log(ERROR_ID, String.valueOf(message), null);
    }

    /**
     * Log the given message and error at [error] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void error(Object message, Throwable error) {
        log(ERROR_ID, String.valueOf(message), error);
    }

    /**
     * Log the given message at [info] logging level.
     *
     * @param message the message to log
     */
    public void info(Object message) {
        log(INFO_ID, String.valueOf(message), null);
    }

    /**
     * Log the given message at [trace] logging level.
     *
     * @param message the message to log
     */
    public void trace(Object message) {
        log(TRACE_ID, String.valueOf(message), null);
    }

    /**
     * Log the given message and error at [trace] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void trace(Object message, Throwable error) {
        log(TRACE_ID, String.valueOf(message), error);
    }

    /**
     * Log the given message at [warn] logging level.
     *
     * @param message the message to log
     */
    public void warn(Object message) {
        log(WARN_ID, String.valueOf(message), null);
    }

    /**
     * Log the given message and error at [warn] logging level.
     *
     * @param message the message to log
     * @param error the error to log
     */
    public void warn(Object message, Throwable error) {
        log(WARN_ID, String.valueOf(message), error);
    }

    /**
     * Return true if [debug] level logging is enabled.
     *
     * @return true if [debug] level logging is enabled
     */
    public boolean isDebugEnabled() {
        return logLevel <= DEBUG_ID;
    }

    /**
     * Return true if [info] level logging is enabled.
     *
     * @return true if [info] level logging is enabled
     */
    public boolean isInfoEnabled() {
        return logLevel <= INFO_ID;
    }

    /**
     * Return true if [trace] level logging is enabled.
     *
     * @return true if [trace] level logging is enabled
     */
    public boolean isTraceEnabled() {
        return logLevel <= TRACE_ID;
    }

}
