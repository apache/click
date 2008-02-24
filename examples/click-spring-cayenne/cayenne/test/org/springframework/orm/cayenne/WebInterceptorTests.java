package org.springframework.orm.cayenne;

import junit.framework.TestCase;

import org.objectstyle.cayenne.access.DataDomain;

public class WebInterceptorTests extends TestCase {
    public void testDomain() {
        WebInterceptor interceptor = new WebInterceptor();
        DataDomain domain = new DataDomain("Test Domain");
        interceptor.setDomain(domain);
        assertSame(domain, interceptor.getDomain());
    }
}