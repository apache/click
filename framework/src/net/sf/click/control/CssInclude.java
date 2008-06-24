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

import net.sf.click.util.HtmlStringBuffer;

/**
 *
 * @author Bob Schellink
 */
public class CssInclude extends AbstractControl {

    private HtmlStringBuffer include = new HtmlStringBuffer();
    
    private boolean unique;
    
    public CssInclude() {
        this(null);
    }

    public CssInclude(String include) {
        if (include != null) {
            this.include.append(include);
        }
        setAttribute("type", "text/css");
        setAttribute("rel", "stylesheet");
    }

    public String getTag() {
        return "style";
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public void append(String include) {
        this.include.append(include);
    }

    public HtmlStringBuffer getInclude() {
        return include;
    }

    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart(getTag());
        appendAttributes(buffer);
        buffer.closeTag();
        buffer.append(getInclude());
        buffer.elementEnd(getTag());
    }
}
