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

/**
 *
 * @author Bob Schellink
 */
public class ModuleUtils {

    public static String extractJarPathFromURL(URL url) {
        if (url == null) {
            return null;
        }
        String urlStr = url.toString();
        int end = urlStr.indexOf(".jar!");
        if (end < 0) {
            return null;
        }
        int start = urlStr.lastIndexOf("/WEB-INF/lib", end);
        if (start < 0) {
            return null;
        }
        return urlStr.substring(start, end + 4);
    }
}
