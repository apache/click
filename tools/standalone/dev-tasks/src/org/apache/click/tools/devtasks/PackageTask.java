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
