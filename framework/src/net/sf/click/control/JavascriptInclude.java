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
public class JavascriptInclude extends AbstractControl {
    
    private HtmlStringBuffer include = new HtmlStringBuffer();

    public static final int TOP = 0;
    public static final int BOTTOM = 1;

    private int position = BOTTOM;

    private boolean unique = false;

    public JavascriptInclude() {
        this(null, BOTTOM, false);
    }

    public JavascriptInclude(String include) {
        this(include, BOTTOM, false);
    }
    
    public JavascriptInclude(String include, int position) {
        this(include, position, false);
    }

     public JavascriptInclude(String include, boolean unique) {
        this(include, BOTTOM, unique);
    }

    public JavascriptInclude(String include, int position, boolean unique) {
        if (include != null) {
            this.include.append(include);
        }
        this.unique = unique;
        setAttribute("type", "text/javascript");
    }

    public String getTag() {
        return "script";
    }

    public void append(String include) {
        this.include.append(include);
    }

    public HtmlStringBuffer getInclude() {
        return include;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart(getTag());
        appendAttributes(buffer);
        buffer.closeTag();
        buffer.append(getInclude());
        buffer.elementEnd(getTag());
    }
}
