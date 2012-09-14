/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a classloader object map cache keyed on the current threads
 * classloader.
 *
 * @param <E> the class to cache against the current threads classloader
 */
public class ClassLoaderCache<E> {

    // The cache map keyed by classloader
    private Map<ClassLoader, E> classLoaderMap
        = Collections.synchronizedMap(new HashMap<ClassLoader, E>());

    /**
     * Return the cached variable for the current thread classloader.
     *
     * @return the cached variable for the current thread classloader.
     */
    public E get() {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         return classLoaderMap.get(cl);
    }

    /**
     * Set the cached variable on the current thread classloader.
     *
     * @param e the cached variable for the current thread classloader.
     */
    public void put(E e) {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         classLoaderMap.put(cl, e);
    }

    /**
     * Clear the classloader map cache.
     */
    public void clear() {
        classLoaderMap.clear();
    }

}
