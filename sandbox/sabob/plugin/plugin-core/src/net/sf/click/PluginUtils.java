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
package net.sf.click;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;

/**
 *
 * @author Bob Schellink
 */
public class PluginUtils {

    public static Map findAllJars(ServletContext context) {
        Map map = new HashMap();
        Set jars = context.getResourcePaths("/WEB-INF/lib");
        if (jars == null) {
            return map;
        }
        for (Iterator it = jars.iterator(); it.hasNext();) {
            String jar = (String) it.next();
            if (canAddJar(jar)) {
                map.put(normalizeJarName(jar), jar);
            }
        }
        return map;
    }

    public static String normalizeJarName(String path) {
        if (path == null) {
            return null;
        }
        int start = path.lastIndexOf("/");
        if (start < 0) {
            return path;
        }
        return path.substring(start + 1);
    }

    public static boolean canAddJar(String path) {
        if (path == null) {
            return false;
        }
        // Skip click-core.jar
        if (path.indexOf("click-core") >= 0) {
            return false;
        }
        return true;
    }

    public static String extractJarNameFromURL(URL url) {
        if (url == null) {
            return null;
        }
        String urlStr = url.toString();
        int end = urlStr.indexOf(".jar!");
        if (end < 0) {
            return null;
        }
        int start = urlStr.lastIndexOf("/", end);
        if (start < 0) {
            return null;
        }
        return urlStr.substring(start + 1, end + 4);
    }
}
