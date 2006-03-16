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

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

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
 * This is roughtly equivalent to making the call:
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
 * <p/>
 * The MessagesMap will automatically cache loaded message properties when
 * the application is in <tt>"production"</tt> or <tt>"profile"</tt> mode.
 *
 * @author Malcolm.Edgar
 */
public class MessagesMap implements Map {

    /** Cache of resource bundle and locales which werer not found. */
    protected static final Set NOT_FOUND_CACHE =
        Collections.synchronizedSet(new HashSet());

    /** Cache of messages keyed by bundleName + Locale name. */
    protected static final Map MESSAGES_CACHE = new HashMap();

    /** The cache key set load lock. */
    protected static final Object CACHE_LOAD_LOCK = new Object();

    // ----------------------------------------------------- Instance Variables

    /** The resource bundle base name. */
    protected final String baseName;

    /** The cache resources flag. */
    protected final boolean cache;

    /** The time the cache was lasted updated. */
    protected static long cacheLastUpdated;

    /** The map of localized messages. */
    protected Map messages;

    /** The resource bundle locale. */
    protected final Locale locale;

    /** Flag indicating no resources found after initialization. */
    protected boolean noResourcesFound;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a resource bundle messages <tt>Map</tt> adaptor for the given
     * bundle name and <tt>Context</tt>.
     *
     * @param baseName the resource bundle name
     * @param context the request context
     */
    public MessagesMap(String baseName, Context context) {
        if (baseName == null) {
            throw new IllegalArgumentException("Null baseName parameter");
        }
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.baseName = baseName;
        this.locale = context.getLocale();

        // Cache resources if application in "production" or "profile" mode
        this.cache = context.getApplicationMode().startsWith("pro");
    }

    // --------------------------------------------------------- Public Methods

    /**
     * @see java.util.Map#size()
     */
    public int size() {
        ensureInitialized();
        if (!noResourcesFound) {
            return messages.size();
        } else {
            return 0;
        }
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        ensureInitialized();
        if (!noResourcesFound) {
            return messages.isEmpty();
        } else {
            return true;
        }
    }

    /**
     * @see java.util.Map#containsKey(Object)
     */
    public boolean containsKey(Object key) {
        if (key != null) {
            ensureInitialized();
            if (!noResourcesFound) {
                return messages.containsKey(key);
            }
        }
        return false;
    }

    /**
     * @see java.util.Map#containsValue(Object)
     */
    public boolean containsValue(Object value) {
        ensureInitialized();
        if (!noResourcesFound) {
            return messages.containsValue(value);
        } else {
            return false;
        }
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
            Object[] args = { keyStr, baseName, locale };
            msg = MessageFormat.format(msg, args);
            throw new MissingResourceException(msg, baseName, keyStr);
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
        if (!noResourcesFound) {
            return messages.keySet();
        } else {
            return Collections.EMPTY_SET;
        }
    }

    /**
     * @see java.util.Map#values()
     */
    public Collection values() {
        ensureInitialized();
        if (!noResourcesFound) {
            return messages.values();
        } else {
            return Collections.EMPTY_SET;
        }
    }

    /**
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        ensureInitialized();
        if (!noResourcesFound) {
            return messages.entrySet();
        } else {
            return Collections.EMPTY_SET;
        }
    }

    // ------------------------------------------------------ Protected Methods

    private void ensureInitialized() {
        if (messages == null && !noResourcesFound) {

            String resourceKey = baseName + locale.toString();
            if (!NOT_FOUND_CACHE.contains(resourceKey)) {
                if (MESSAGES_CACHE.containsKey(resourceKey)) {
                    messages = (Map) MESSAGES_CACHE.get(resourceKey);

                } else {
                    synchronized (CACHE_LOAD_LOCK) {
                       try {

                           ResourceBundle resources =
                               getBundle(baseName, locale);

                           messages = new HashMap();

                           Enumeration e = resources.getKeys();
                           while (e.hasMoreElements()) {
                               String name = e.nextElement().toString();
                               String value = resources.getString(name);
                               messages.put(name, value);
                           }

                           messages = Collections.unmodifiableMap(messages);

                           if (cache) {
                               MESSAGES_CACHE.put(resourceKey, messages);
                           }

                        } catch (MissingResourceException mre) {
                            NOT_FOUND_CACHE.add(resourceKey);
                            noResourcesFound = true;
                        }
                    }
                }
            } else {
                noResourcesFound = true;
            }
        }
    }

    private ResourceBundle getBundle(String baseName, Locale locale) {
        if (!cache) {
            clearResourceBundleCache(baseName, locale);
        }

        return ResourceBundle.getBundle(baseName, locale);
    }

    private void clearResourceBundleCache(String baseName, Locale locale) {
        if (cacheLastUpdated == 0) {
            cacheLastUpdated = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - cacheLastUpdated < 1000) {
            return;
        }

        cacheLastUpdated = System.currentTimeMillis();

        try {
            ResourceBundle rb = ResourceBundle.getBundle(baseName, locale);
            Class aClass = rb.getClass().getSuperclass();

            Field field = aClass.getDeclaredField("cacheList");
            field.setAccessible(true);
            Map cache = (Map) field.get(null);
            cache.clear();
            field.setAccessible(false);

        } catch (MissingResourceException mre) {
            throw mre;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
