/*
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

import java.net.URL;
import net.sf.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Bob Schellink
 */
public class PathLookupAlgorithm {
    
    private ClickApp clickApp;
    
    public PathLookupAlgorithm(ClickApp clickApp) {
        this.clickApp = clickApp;
    }
    
    public String getPagePath(Class pageClass) {
        return constructPathFromClass(pageClass);
    }
    
    public String constructPathFromClass(Class pageClass) {
        String className = getSimpleName(pageClass);
        String pageDir = calcPageDir(pageClass);
        String result = null;
        
        result = constructPathFromClass(pageDir, className);
        
        if(result != null) {
            return result;
        }
        
        //Not found? Try with/without 'Page'
        if(className.endsWith("Page")) {
            //Chop off the 'Page' string and try again
            String noPageClassName = className.substring(0, className.lastIndexOf("Page"));
            result = constructPathFromClass(pageDir, noPageClassName);
        } else {
            //Append the 'Page' string and try again
            String pageClassName = className + "Page";
            result = constructPathFromClass(pageDir, pageClassName);
        }
        
        return result;
    }
    
    /**
     * This method strips of the pagesPackage from the packageName.
     * Thus if clickApp.pagesPackage is 'com.mycorp', then the packageName
     * 'com.mycorp.contacts' becomes 'contacts'
     */
    public String calcPageDir(Class clazz) {
        int indexOfClassName = clazz.getName().lastIndexOf('.');
        String pageDir = "/";
        
        //If the clazz does not have a package return the root path
        if(indexOfClassName < 0) {
            return pageDir;
        }
        
        //Note the addition of the '.' after the package name below. This
        //ensures to check for a legal package instead of a false positive
        //like 'com.mycorp.con' which would also have qualified if the
        //package name was 'com.mycorp.contacts'.
        
        //The '+ 1' in the substring argument ensures the packageName
        //ends with '.' ie 'com.mycorp.'
        final String packageName = clazz.getName().substring(0, indexOfClassName + 1);
        
        //If the pagesPackage is not specified return the converted packageName
        if(clickApp.getPagesPackage() == null || clickApp.getPagesPackage().length() == 0) {
            return convertToAbsoluteDir(packageName);
        }

        //Also append a '.' at the end of the pagesPackage
        final String pagesPackage = clickApp.getPagesPackage() + ".";
        
        //Check that pagesPackage is a substring of packageName
        if(packageName.startsWith(pagesPackage)) {
            
            //Check that the pagesPackage and packageName is not equal
            if(packageName.length() != pagesPackage.length()) {
                //Subtract the pagesPackage from the specified class package
                pageDir = packageName.substring(clickApp.getPagesPackage().length() + 1);
            }
        } else {
            pageDir = packageName;
        }
        return convertToAbsoluteDir(pageDir);
    }

    /**
     * Prefix the name with a '/' and change any '.' to '/'
     */
    public String convertToAbsoluteDir(String packageName) {
        packageName = ensurePathStartsWithSlash(packageName);
        return packageName.replace('.', '/');
    }
    
    public String getSimpleName(Class clazz) {
        String name = clazz.getName();
        
        //Return the name without any package
        if (name.lastIndexOf('.') > 0) {
            name = name.substring(name.lastIndexOf('.') + 1);
        }
        
        //For inner classes rereplace '$' with '.'
        return name.replace('$', '.');
    }
    
    protected String constructPathFromClass(String pageDir, String className) {
        String result = _constructPathFromClass(pageDir, className + ".htm");
        if(result == null) {
            result = _constructPathFromClass(pageDir, className + ".jsp");
        }
        return result;
    }
    
    private String _constructPathFromClass(String pageDir, String className) {
        pageDir = ensurePathStartsWithSlash(pageDir);
        
        //The 'path from class' lookup strategy is as follows
        //1. do not change the classname, just do lookup
        String path = pageDir + className;
        URL resource = tryAndFindEntryForPath(path);
        if(resource != null) {
            return path;
        }
        
        //2. lower case the first character
        String lowercase = Character.toString(Character.toLowerCase(className.charAt(0)));
        path = pageDir + lowercase + className.substring(1);
        resource = tryAndFindEntryForPath(path);
        if(resource != null) {
            return path;
        }
        
        //3. normalize camel case class to path tokenized on '-'
        path = pageDir + camelCaseToPath(className, "-");
        resource = tryAndFindEntryForPath(path);
        if(resource != null) {
            return path;
        }
        
        //3. normalize camel case class to path tokenized on '-'
        path = pageDir + camelCaseToPath(className, "_");
        resource = tryAndFindEntryForPath(path);
        if(resource != null) {
            return path;
        }
        return null;
    }
    
    protected String camelCaseToPath(String camelString, String token) {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        char[] chars = camelString.toCharArray();
        int length = chars.length;
        
        //Append first char
        buffer.append(Character.toLowerCase(chars[0]));
        
        for(int i = 1; i < length; i++) {
            if(Character.isUpperCase(chars[i])) {
                buffer.append(token);
                buffer.append(Character.toLowerCase(chars[i]));
            } else {
                buffer.append(chars[i]);
            }
        }
        return buffer.toString();
    }
    
    protected URL tryAndFindEntryForPath(String path) {
        try {
            //path = ensurePathStartsWithSlash(path);
            URL resource = clickApp.getServletContext().getResource(path);
            return resource;
        } catch (Exception ex) {
        }
        return null;
    }
    
    protected String ensurePathStartsWithSlash(String path) {
        if(path.charAt(0) != '/') {
            return '/' + path;
        } else {
            return path;
        }
    }
}
