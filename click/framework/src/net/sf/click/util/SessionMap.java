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

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

/**
 * Provides a Map adaptor for HttpSession objects. A SessionMap instance is
 * available in each Velocity page using the name "<span class="blue">session</span>".
 * <p/>
 * For example suppose we have a User object in the session with the
 * attribute name "user" when a user is logged on.  We can display the users
 * name in the page when the are logged onto the system.
 *
 * <pre class="codeHtml">
 * <span class="red">#if</span> (<span class="blue">$session</span>.user)
 *   <span class="blue">$session</span>.user.fullname you are logged on.
 * <span class="red">#else</span>
 *   You are not logged on.
 * <span class="red">#end</span> </pre>
 *
 * The ClickServlet adds a SessionMap instance to the Velocity Context before
 * it is merged with the page template.
 *
 * @author Malcolm.Edgar
 */
public class SessionMap implements Map {

    /** The internal session attribute. */
    protected HttpSession session;

    /**
     * Create a <tt>HttpSession</tt> <tt>Map</tt> adaptor.
     *
     * @param value the http session
     */
    public SessionMap(HttpSession value) {
        session = value;
    }

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        if (session != null) {
            int size = 0;
            Enumeration enumeration = session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                enumeration.nextElement();
                size++;
            }
            return size;
        } else {
            return 0;
        }
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * @see java.util.Map#containsKey(Object)
     */
    public boolean containsKey(Object key) {
        if (session != null && key != null) {
            return session.getAttribute(key.toString()) != null;
        } else {
            return false;
        }
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see java.util.Map#containsValue(Object)
     */
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Map#get(Object)
     */
    public Object get(Object key) {
        if (session != null && key != null) {
            return session.getAttribute(key.toString());
        } else {
            return null;
        }
    }

    /**
     * @see java.util.Map#put(Object, Object)
     */
    public Object put(Object key, Object value) {
        if (session != null && key != null) {
            Object out = session.getAttribute(key.toString());

            session.setAttribute(key.toString(), value);

            return out;

        } else {
            return null;
        }
    }

    /**
     * @see java.util.Map#remove(Object)
     */
    public Object remove(Object key) {
        if (session != null && key != null) {
            Object out = session.getAttribute(key.toString());
            session.removeAttribute(key.toString());

            return out;

        } else {
            return null;
        }
    }

    /**
     * @see java.util.Map#putAll(Map)
     */
    public void putAll(Map map) {
        if (session != null && map != null) {
            for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
                String key = i.next().toString();
                Object value = map.get(key);
                session.setAttribute(key, value);
            }
        }
    }

    /**
     * @see java.util.Map#clear()
     */
    public void clear() {
        if (session != null) {
            Enumeration enumeration = session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                String name = enumeration.nextElement().toString();
                session.removeAttribute(name);
            }
        }
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        if (session != null) {
            Set keySet = new HashSet();

            Enumeration enumeration = session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                keySet.add(enumeration.nextElement());
            }

            return keySet;

        } else {
            return Collections.EMPTY_SET;
        }
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see java.util.Map#values()
     */
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        if (session != null) {
            Set entrySet = new HashSet();

            Enumeration enumeration = session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                String name = enumeration.nextElement().toString();
                Object value = session.getAttribute(name);
                entrySet.add(value);
            }

            return entrySet;

        } else {
            return Collections.EMPTY_SET;
        }
    }

}
