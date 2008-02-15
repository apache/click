package benchmark.wicket;

import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;

/**
 * Benchmark application for testing using the HTTP Session store (wicket 1.2
 * mode).
 */
public class WicketHttpSessionStoreApplication extends WebApplication {

    protected void init() {
        getMarkupSettings().setStripWicketTags(true);
        getRequestLoggerSettings().setRequestLoggerEnabled(false);
    }

    /**
     * Return HttpSessionStore instead of the default
     * SecondLevelCacheSessionStore, which was about 80-90% slower in my tests.
     *
     * @return HttpSessionStore
     */
    protected ISessionStore newSessionStore() {
        return new HttpSessionStore(this);
    }

    public Class getHomePage() {
        return CustomerList.class;
    }
}