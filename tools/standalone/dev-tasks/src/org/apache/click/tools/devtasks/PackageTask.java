/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.tools.devtasks;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.*;

public class PackageTask extends Task {

    private String packageName;

    private String projectName;

    public PackageTask() {
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setPackageName(String value) {
        packageName = value;
    }

    public String getPackageName() {
        return packageName;
    }

    public void execute() throws BuildException {
        if (projectName == null && projectName.length() == 0) {
            throw new BuildException("projectName property is not defined");
        }
        if (packageName == null && packageName.length() == 0) {
            throw new BuildException("packageName property is not defined");
        }
        try {
            File file = getProject().getBaseDir();
            String path = file.getCanonicalPath() + File.separatorChar + ".." +
                File.separatorChar + projectName + File.separatorChar + "src" +
                File.separatorChar +
                packageName.replace('.', File.separatorChar);
            File packageFile = new File(path);
            if (!packageFile.mkdirs()) {
                throw new BuildException("Unable to create path: " + packageFile);
            }
            getProject().setProperty("package.path",
                                     packageFile.getCanonicalPath());
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }
    }
}
