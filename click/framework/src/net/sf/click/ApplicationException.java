/*
 * Copyright 2004 Malcolm A. Edgar
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
package net.sf.click;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Provides an unchecked Click application exception.
 * <p/>
 * This RuntimeException  should be used when the calling
 * code cannot manage an error. The ApplicationException may be thrown 
 * by the Click runtime environment if there is a configuration error or some
 * unmanagable error.
 * <p/>
 * This class supports nested causing exceptions via the {@link #getCause()}
 * method. If a cause is defined, then the cause's message and stack track will
 * be returned by this exception.
 * <p/>
 * The {@link net.sf.click.ClickServlet} provides a top level error handler:
 * <blockquote>
 * {@link net.sf.click.ClickServlet#handleException(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, boolean, Throwable, Page)} 
 * </blockquote>
 * This error handler will catch ApplicationExceptions and delegate them to the
 * configured "error.htm" page. The default error page class is
 * {@link net.sf.click.util.ErrorPage}.
 *
 * @author Malcolm Edgar
 */
public class ApplicationException extends RuntimeException {

    /** The exception cause. */
    protected final Throwable cause;

    /**
     * Create a new application exception.
     */
    public ApplicationException() {
        super();
        cause = null;
    }

    /**
     * Create a new application exception with the given message.
     *
     * @param message the error message
     */
    public ApplicationException(String message) {
        super(message);
        cause = null;
    }

    /**
     * Create a new application exception with the given message and cause.
     *
     * @param message the error message
     * @param cause the original cause of the error
     */
    public ApplicationException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * Create a new application exception with the given cause
     *
     * @param cause the original cause of the error
     */
    public ApplicationException(Throwable cause) {
        super();
        this.cause = cause;
    }

    /**
     * Return the nested cause of this exception, or null if not defined.
     *
     * @return the nested cause of this exception, or null if not defined.
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        String msg = super.getMessage();

        if (cause != null && msg == null || msg.length() == 0) {
            return cause.getMessage();
        } else {
            return msg;
        }
    }

    /**
     * @see Throwable#printStackTrace()
     */
    public void printStackTrace() {
        if (cause != null) {
            cause.printStackTrace();
        } else {
            super.printStackTrace();
        }
    }

    /**
     * @see Throwable#printStackTrace(PrintStream)
     */
    public void printStackTrace(PrintStream s) {
        if (cause != null) {
            cause.printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }

    /**
     * @see Throwable#printStackTrace(PrintWriter)
     */
    public void printStackTrace(PrintWriter s) {
        if (cause != null) {
            cause.printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }

}
