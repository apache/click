package benchmark.wicket;

import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.session.ISessionStore;

public class BenchmarkApplication extends WebApplication {

    protected void init() {
        getMarkupSettings().setStripWicketTags(true);
        getRequestLoggerSettings().setRequestLoggerEnabled(false);
    }

    protected ISessionStore newSessionStore() {
    		return new HttpSessionStore(this);
    }

    public Class getHomePage() {
        return CustomerList.class;
    }
}