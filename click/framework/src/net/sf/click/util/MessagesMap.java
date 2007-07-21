/*
 * Copyright 2004-2007 Malcolm A. Edgar
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.Validate;

import net.sf.click.Context;

/**
 * Provides a localized read only messages Map for Page and Control classes.
 * <p/>
 * A MessagesMap instance is available in each Velocity page using the name
 * "<span class="blue">messages</span>".
 * <p/>
 * For example suppose you have a localized page title, which is stored in the
 * Page's properties file. You can access page "title" message in your page
 * template via:
 *
 * <pre class="codeHtml">
 * <span class="blue">$messages.title</span> </pre>
 *
 * This is roughly equivalent to making the call:
 *
 * <pre class="codeJava">
 * <span class="kw">public void</span> onInit() {
 *    ..
 *    addModel(<span class="st">"title"</span>, getMessage(<span class="st">"title"</span>);
 * } </pre>
 *
 * Please note if the specified message does not exist in your Page's
 * properties file, or if the Page does not have a properties file, then
 * a <tt>MissingResourceException</tt> will be thrown.
 * <p/>
 * The ClickServlet adds a MessagesMap instance to the Velocity Context before
 * it is merged with the page template.
 *
 * @author Malcolm.Edgar
 */
public class MessagesMap implements Map {

    /** Cache of resource bundle and locales which were not found. */
    protected static final Set NOT_FOUND_CACHE =
        Collections.synchronizedSet(new HashSet());

    /** Cache of messages keyed by bundleName + Locale name. */
    protected static final Map MESSAGES_CACHE = new HashMap();

    /** The cache key set load lock. */
    protected static final Object CACHE_LOAD_LOCK = new Object();

    // ----------------------------------------------------- Instance Variables

    /** The base class. */
    protected final Class baseClass;

    /** The class global resource bundle base name. */
    protected final String globalBaseName;

    /** The map of localized messages. */
    protected Map messages;

    /** The resource bundle locale. */
    protected final Locale locale;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a resource bundle messages <tt>Map</tt> adaptor for the given
     * object's class resource bundle, the global resource bundle and
     * <tt>Context</tt>.
     * <p/>
     * Messages located in the object's resource bundle will override any
     * messages defined in the global resource bundle.
     *
     * @param baseClass the target class
     * @param globalResource the global resource bundle name
     * @param context the request context
     */
    public MessagesMap(Class baseClass, String globalResource, Context context) {
        Validate.notNull(baseClass, "Null object parameter");
        Validate.notNull(context, "Null context parameter");

        this.baseClass = baseClass;
        this.locale = context.getLocale();

        this.globalBaseName = globalResource;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        ensureInitialized();
        return messages.size();
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        ensureInitialized();
        return messages.isEmpty();
    }

    /**
     * @see java.util.Map#containsKey(Object)
     */
    public boolean containsKey(Object key) {
        if (key != null) {
            ensureInitialized();
            return messages.containsKey(key);
        }
        return false;
    }

    /**
     * @see java.util.Map#containsValue(Object)
     */
    public boolean containsValue(Object value) {
        ensureInitialized();
        return messages.containsValue(value);

    }

    /**
     * Return localized resource message for the given key. If the message is
     * not found a <tt>MissingResourceExcetion</tt> will be thrown.
     *
     * @see java.util.Map#get(Object)
     * @throws MissingResourceException if the given key was not found
     */
    public Object get(Object key) {
        if (containsKey(key)) {
            return (String) messages.get(key);

        } else {
            String msg =
                "Message \"{0}\" not found in bundle \"{1}\" "
                + "for locale \"{2}\"";
            String keyStr = (key != null) ? key.toString() : null;
            Object[] args = { keyStr, baseClass.getName(), locale };
            msg = MessageFormat.format(msg, args);
            throw new MissingResourceException(msg, baseClass.getName(), keyStr);
        }
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see java.util.Map#put(Object, Object)
     */
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see java.util.Map#remove(Object)
     */
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see java.util.Map#putAll(Map)
     */
    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see java.util.Map#clear()
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        ensureInitialized();
        return messages.keySet();
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection values() {
        ensureInitialized();
        return messages.values();
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        ensureInitialized();
        return messages.entrySet();
    }

    /**
     * @see #toString()
     */
    public String toString() {
        ensureInitialized();
        return messages.toString();
    }

    // ------------------------------------------------------ Protected Methods

    private void ensureInitialized() {
        if (messages == null) {

            String resourceKey = baseClass.getName() + locale.toString();

            messages = (Map) MESSAGES_CACHE.get(resourceKey);

            if (messages != null) {
                return;
            }

            messages = new HashMap();

            synchronized (CACHE_LOAD_LOCK) {

                loadResourceValuesIntoMap(globalBaseName, messages);

                List classnameList = new ArrayList();

                // Build class list
                Class aClass = baseClass;
                while (!aClass.getName().equals("java.lang.Object")) {
                    classnameList.add(aClass.getName());
                    aClass = aClass.getSuperclass();
                }

                // Load messages from parent to child order, so that child
                // class messages override parent messages.
                for (int i = classnameList.size() - 1; i >= 0; i--) {
                    String className = (String) classnameList.get(i);
                    loadResourceValuesIntoMap(className, messages);
                }

                messages = Collections.unmodifiableMap(messages);

                MESSAGES_CACHE.put(resourceKey, messages);
            }
        }
    }

    private void loadResourceValuesIntoMap(String resourceName, Map map) {
        if (resourceName == null) {
            return;
        }

        String resourceKey = resourceName + locale.toString();

        if (!NOT_FOUND_CACHE.contains(resourceKey)) {
            try {
                ResourceBundle resources =
                    getBundle(resourceName, locale);

                Enumeration e = resources.getKeys();
                while (e.hasMoreElements()) {
                    String name = e.nextElement().toString();
                    String value = resources.getString(name);
                    map.put(name, value);
                }

            } catch (MissingResourceException mre) {
                NOT_FOUND_CACHE.add(resourceKey);
            }
        }
    }

    private ResourceBundle getBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }

}
