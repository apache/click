package net.sf.click.util;

import java.util.Locale;

import net.sf.click.Context;

public class MockContext extends Context {
    
    private Locale locale;

    public MockContext(Locale locale) {
        super(null, null, null, null, true, null);
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
    
    public String getApplicationMode() {
        return "debug";
    }

}
