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
package org.apache.click.tools.deploy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.jar.JarFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * Provides an Ant task to deploy Click resources from a given source folder to
 * a target folder.
 * <p/>
 * An HTML report is generated which summarizes resources that was deployed and
 * resources that are outdated.
 */
public class DeployTask extends Task {

    // -------------------------------------------------------------- Variables

    /** Directory where filenames can be found. */
    private File dir;

    /** Directory where filenames should be deployed to. */
    private File toDir;

    /** The include filter to use when scanning {@link #dir}. */
    private String includes;

    /** The exclude filter to use when scanning {@link #dir}. */
    private String excludes;

    /** FileSet that specifies jars and folders to check for filenames. */
    private FileSet fileSet;

    // ----------------------------------------------------------- Constructors

    /**
     * Creates a default DeployTask instance.
     */
    public DeployTask() {
        fileSet = new FileSet();
    }

    // ------------------------------------------------------ Public properties

    /**
     * Set the directory consisting of JARs and folders where filenames are
     * deployed from.
     *
     * @param dir the directory consisting of JARs and folders where filenames
     * are deployed from.
     */
    public void setDir(File dir) {
        this.dir = dir;
    }

    /**
     * Set the target directory where filenames are deployed to.
     *
     * @param toDir the target directory where filenames are deployed to.
     */
    public void setToDir(File toDir) {
        this.toDir = toDir;
    }

    /**
     * Set the Ant <tt>excludes</tt> pattern for the source {@link #dir}.
     *
     * @param excludes the Ant <tt>excludes</tt> pattern for the source
     * {@link #dir}.
     */
    public void setExcludes(String excludes) {
        this.excludes = excludes;
        fileSet.setExcludes(excludes);
    }

    /**
     * Set the Ant <tt>includes</tt> pattern for the source {@link #dir}.
     *
     * @param includes the Ant <tt>includes</tt> pattern for the source
     * {@link #dir}.
     */
    public void setIncludes(String includes) {
        this.includes = includes;
        fileSet.setIncludes(includes);
    }

    /**
     * Return the Ant <tt>includes</tt> pattern for the source {@link #dir}.
     *
     * @return the Ant <tt>includes</tt> pattern for the source {@link #dir}.
     */
    public String getIncludes() {
        return includes;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Execute the task.
     *
     * @throws BuildException if the build fails
     */
    public void execute() throws BuildException {
        if(dir == null) {
            throw new BuildException("dir attribute must be set!");
        }
        if(!dir.exists()) {
            throw new BuildException("dir does not exist!");
        }
        if(!dir.isDirectory()) {
            throw new BuildException("dir is not a directory!");
        }

        if(toDir == null) {
            throw new BuildException("todir attribute must be set!");
        }
        if(!toDir.exists()) {
            if (!toDir.mkdirs()) {
                throw new BuildException("Could not create todir '" + toDir + "'!");
            }
            System.out.println("todir '" + toDir + "' created.");
        }
        if(!toDir.isDirectory()) {
            throw new BuildException("todir is not a directory!");
        }

        fileSet.setDir(dir);
        fileSet.setDefaultexcludes(true);
        if (getIncludes() == null) {
            // Set default standard includes
            fileSet.setIncludes("**/*.jar, classes");
        }
        DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
        String files[] = directoryScanner.getIncludedFiles();
        String dirs[] = directoryScanner.getIncludedDirectories();
        String resources[] = (String[]) TaskUtils.addAll(files, dirs);

        deployResources(resources);
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Deploy the resources specified by the given filenames.
     *
     * @param filenames the filenames for the resources to deploy.
     */
    private void deployResources(String filenames[]) {
        Writer reportWriter = null;
        try {

            InputStream is = TaskUtils.getResourceAsStream("/report-template.html", DeployTask.class);
            String template = null;

            if (is == null) {
                System.out.println("The report template 'report-template.html' could not be found on the classpath. No report will be generated.");
            } else {
                template = TaskUtils.toString(is);
            }

            DeployReport report = new DeployReport();

            StringWriter writer = new StringWriter();
            for (int i = 0; i < filenames.length; i++) {
                String filename = filenames[i];
                String path = getCurrentPath();
                File file = new File(dir, filename);

                Deploy deploy = new Deploy();

                boolean deployed = false;

                if (filename.indexOf(".jar") >= 0) {
                    deployed = deploy.deployResourcesInJar(file, toDir);
                } else {
                    deployed = deploy.deployResourcesInDir(file, toDir);
                }

                if (deployed) {
                    report.writeReport(path + filename, toDir.getCanonicalPath(),
                        deploy.getDeployed(), deploy.getOutdated(), reportWriter);
                }
            }

            if (template != null) {
                String reportContent = template;
                reportContent = reportContent.replace("{0}", dir.getCanonicalPath());
                reportContent = reportContent.replace("{1}", toDir.getCanonicalPath());
                reportContent = reportContent.replace("{2}", toHtml(filenames));

                String reportEntries = writer.toString();

                // If no report entries were made, print a success message
                if (TaskUtils.isBlank(reportEntries)) {
                    reportContent = reportContent.replace("{3}", "<h3 class='success'>All resources are successfully deployed</h3>");
                } else {
                    reportContent = reportContent.replace("{3}", writer.toString());
                }

                File reportFile = new File("report.html");
                reportWriter = new FileWriter(reportFile);
                reportWriter.append(reportContent);
                System.out.println("See report: " + reportFile.getCanonicalPath());
            }

        } catch(IOException ioe) {
            throw new BuildException(ioe.getClass().getName() + ":" + ioe.getMessage(), ioe);

        } finally {
            TaskUtils.close(reportWriter);
        }
    }

    /**
     * Returns the current path.
     *
     * @return the current path
     * @throws IOException if the path cannot be looked up
     */
    private String getCurrentPath() throws IOException {
        String path = dir.getCanonicalPath();
        return path.endsWith(File.separator) ? path : path + File.separator;
    }

    /**
     * Return the HTML representation of the given filenames.
     *
     * @param filenames the filenames to represent as HTML
     * @return return HTML representation of the given filenames
     * @throws IOException if an IO exception occurs
     */
    private String toHtml(String[] filenames) throws IOException {
        StringBuilder buffer = new StringBuilder();

        if (filenames != null && filenames.length > 0) {
            String path = getCurrentPath();

            for (String filename : filenames) {
                if (filename.endsWith(".jar")) {
                    JarFile jarFile = new JarFile(new File(path, filename));
                    if (jarFile.getEntry("META-INF/web") != null) {
                        render(buffer, filename);
                    }
                } else {
                    File file = new File(path, filename);
                    file = new File(file, "META-INF/web");
                    if (file.exists()) {
                        render(buffer, filename);
                    }
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Render the HTML represenation of the given filename.
     *
     * @param buffer the buffer to render the HTML representation to
     * @param filename the filename to render
     */
    private void render(StringBuilder buffer, String filename) {
        buffer.append("<li><a href='#");
        buffer.append(TaskUtils.getFilename(filename));
        buffer.append("'>");
        buffer.append(filename);
        buffer.append("</a></li>");
    }
}
