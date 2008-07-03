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
public class ClickModule {

    private String moduleName;

    private String modulePackage;

    private String modulePackageAsPath;

    private String modulePath;

    public void onInit() {
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModulePackage() {
        return modulePackage;
    }

    public final String getModulePackageAsPath() {
        if (modulePackageAsPath == null) {
            modulePackageAsPath = getModulePackage().replace('.', '/');
        }
        return modulePackageAsPath;
    }

    public String getModulePath() {
        return modulePath;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("moduleName", getModuleName())
            .append("modulePackage", getModulePackage())
            .append("modulePath", getModulePath())
            .toString();
    }
}
