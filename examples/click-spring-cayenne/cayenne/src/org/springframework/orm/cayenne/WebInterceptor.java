package org.springframework.orm.cayenne;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.access.DataDomain;
import org.objectstyle.cayenne.conf.BasicServletConfiguration;
import org.objectstyle.cayenne.conf.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Cayenne Spring web request interceptor that wraps Cayenne DataDomain,
 * providing session DataContext to request threads.
 * 
 * @author Andrei Adamchik
 */
public class WebInterceptor implements HandlerInterceptor {
    protected DataDomain domain;

    /**
     * Default constructor. "domain" property must be set if to finish
     * configuration.
     */
    public WebInterceptor() {
    }

    /**
     * Creates WebInterceptor for default DataDomain.
     */
    public WebInterceptor(Configuration configuration) {
        this.domain = configuration.getDomain();
    }

    /**
     * Creates WebInterceptor for a named DataDomain.
     */
    public WebInterceptor(Configuration configuration, String domainName) {
        this.domain = configuration.getDomain(domainName);
    }

    public DataDomain getDomain() {
        return domain;
    }

    public void setDomain(DataDomain domain) {
        this.domain = domain;
    }

    /**
     * Binds session DataContext to the current thread. If no DataContext exists
     * in the session, creates one.
     */
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // get DataContext from the session.

        // don't use BasicServletConfiguration.getDefaultContext() - it uses
        // static method to create DataContext which is not what we may want.
        HttpSession session = request.getSession();
        DataContext context = (DataContext) session
                .getAttribute(BasicServletConfiguration.DATA_CONTEXT_KEY);

        if (context == null) {
            context = getDomain().createDataContext();
            session.setAttribute(BasicServletConfiguration.DATA_CONTEXT_KEY,
                    context);
        }

        // bind DataContext to thread...
        DataContext.bindThreadDataContext(context);
        return true;
    }

    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        // I guess it is too early to dispose of thread DataContext here
    }

    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

        // cleanup thread-locals...
        DataContext.bindThreadDataContext(null);
    }
}