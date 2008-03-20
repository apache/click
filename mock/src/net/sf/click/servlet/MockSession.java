/*
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
package net.sf.click.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import net.sf.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Bob Schellink
 */
public class MockSession implements HttpSession {

    // -------------------------------------------------------- Instance Variables

    protected ServletContext servletContext = null;

    protected Map attributes = new HashMap();

    protected String id = Long.toString(new Random().nextLong());

    protected long creationTime = System.currentTimeMillis();

    protected int maxInactiveInerval = -1;

    // -------------------------------------------------------- Constructors

    public MockSession() {
    }

    public MockSession(String id) {
        this(id, null);
    }

    public MockSession(ServletContext servletContext) {
        this(null, servletContext);
    }

    public MockSession(String id, ServletContext servletContext) {
        if(StringUtils.isNotBlank(id)) {
            setId(id);
        }
        setServletContext(servletContext);
    }

    // -------------------------------------------------------- Test Configuration Methods

    public void setId(String id) {
        this.id = id;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    // -------------------------------------------------------- Session Methods

    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getId() {
        return id;
    }

    public long getLastAccessedTime() {
        return System.currentTimeMillis();
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInerval;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public HttpSessionContext getSessionContext() {
        return null;
    }

    public String[] getValueNames() {
        return (String[]) attributes.keySet().toArray(new String[attributes.size()]);
    }

    public void invalidate() {
        attributes.clear();
    }

    public boolean isNew() {
        return false;
    }

    public void setMaxInactiveInterval(int maxInterval) {
        this.maxInactiveInerval = maxInterval;
    }

    public void removeValue(String key) {
        attributes.remove(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Object getValue(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void putValue(String key, Object value) {
        attributes.put(key, value);
    }

    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        buffer.append("Session attributes {");
        for(Enumeration en = getAttributeNames(); en.hasMoreElements(); ) {
            String name = (String) en.nextElement();
            Object value = getAttribute(name);
            buffer.append(name);
            buffer.append("=");
            buffer.append(value);
            if(en.hasMoreElements()) {
                buffer.append(", ");
            }
        }
        buffer.append("}");
        return buffer.toString();
    }
}
