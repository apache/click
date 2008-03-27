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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Bob Schellink
 */
public class ClickPlugin {

    private String pluginName;

    private String pluginPackage;

    private String pluginPackageAsPath;

    private String pluginPath;

    public void onInit() {
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getPluginPackage() {
        return pluginPackage;
    }

    public final String getPluginPackageAsPath() {
        if (pluginPackageAsPath == null) {
            pluginPackageAsPath = getPluginPackage().replace('.', '/');
        }
        return pluginPackageAsPath;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("pluginName", getPluginName())
            .append("pluginPackage", getPluginPackage())
            .append("pluginPath", getPluginPath())
            .toString();
    }
}
