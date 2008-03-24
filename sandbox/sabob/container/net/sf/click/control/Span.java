/*
 * Copyright 2004-2008 Malcolm A. Edgar
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

import net.sf.click.control.Panel;

/**
 *
 * @author Bob Schellink
 */
public class Span extends Panel {
    
    public Span() {
    }
    
    public Span(String name) {
         if(name != null) {
            setName(name);
        }
    }
    
    public Span(String name, String id) {
        this(name);
        setAttribute("id", id);
    }
    
    public final String getTag() {
        return "span";
    }
}
