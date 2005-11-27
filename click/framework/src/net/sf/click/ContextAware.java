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
package net.sf.click;

/**
 * Provides the interface for Context aware objects. Context aware object have
 * their Context dependency injected by their containing object.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public interface ContextAware {

    /**
     * Return the request Context.
     *
     * @return the request Context
     */
    public Context getContext();

    /**
     * Set the request Context.
     *
     * @param context the request Context
     */
    public void setContext(Context context);
}
