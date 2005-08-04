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
package net.sf.click.control;

import net.sf.click.Context;

/**
 * Provides a decorator interface to render given objects.
 * <p/>
 * <b>PLEASE NOTE</b>: the Decorator interface is undergoing preliminary
 * development and is subject to significant change
 *
 * @see Column
 * @see Table
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public interface Decorator {

    /**
     * Returns a rendered and decorated string representation of the given
     * object.
     *
     * @param object the object to render
     * @param context the request context
     * @return a decorated string representation of the given object
     */
    public String render(Object object, Context context);
}
