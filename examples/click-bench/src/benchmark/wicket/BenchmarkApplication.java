package benchmark.wicket;

import org.apache.wicket.protocol.http.WebApplication;

public class BenchmarkApplication extends WebApplication {

    protected void init() {
        getMarkupSettings().setStripWicketTags(true);
    }

    public Class getHomePage() {
        return CustomerList.class;
    }
}