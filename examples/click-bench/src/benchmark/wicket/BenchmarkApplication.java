package benchmark.wicket;

import org.apache.wicket.protocol.http.WebApplication;

public class BenchmarkApplication extends WebApplication {

    protected void init() {
        getMarkupSettings().setStripWicketTags(true);
        getRequestLoggerSettings().setRequestLoggerEnabled(false);
    }

    public Class getHomePage() {
        return CustomerList.class;
    }
}