/*
 * Copyright 2005 Malcolm A. Edgar
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
package examples.page;

import net.sf.click.Page;

/**
 * Provides a page border template. This Page returns the template 
 * <tt>"border.htm"</tt>, and sets the Page model values <tt>$title</tt> and
 * <tt>$srcPath</tt>.
 *
 * @author Malcolm Edgar
 */
public class BorderedPage extends Page {
    
    /**
     * Create a BorderedPage and set the model attributes <tt>$title</tt> and
     * <tt>$srcPath</tt>.
     * <ul>
     * <li><tt>$title</tt> &nbsp; - &nbsp; the Page title from classname</li>
     * <li><tt>$srcPath</tt> &nbsp; - &nbsp; the Page Java source path</li>
     * </ul>
     */
    public BorderedPage() {
        String className = getClass().getName();
        
        String shortName = className.substring(className.lastIndexOf('.') + 1);
        StringBuffer title = new StringBuffer(shortName.length() + 1);
        title.append(shortName.charAt(0));
        for (int i = 1; i < shortName.length(); i++) {
            char aChar = shortName.charAt(i);
            if (Character.isUpperCase(aChar)) {
                title.append(' ');
            }
            title.append(aChar);
        }
        addModel("title", title);       

        String srcPath = className.replace('.', '/') + ".java";
        addModel("srcPath", srcPath);
    }

    /**
     * Returns the name of the border template: &nbsp; <tt>"border.htm"</tt>
     * 
     * @see Page#getTemplate()
     */
    public String getTemplate() {
        return "border.htm";
    }   
}
