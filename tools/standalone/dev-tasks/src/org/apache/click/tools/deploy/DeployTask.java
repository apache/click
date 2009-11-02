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
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Default FileSet associated with the DeployTask element that specifies
     * jars and folders to check for filenames.
     */
    private FileSet defaultFileSet;

    /** List of nested FileSets. */
    private List<FileSet> fileSets = new ArrayList<FileSet>();

    /**
     * Buffer containing a listing of all the jar and folder sources that
     * contained deployable resources.
     */
    private StringBuilder deployableSourceListing = new StringBuilder();

    /** The report content. */
    private Writer reportContent = new StringWriter();

    // ----------------------------------------------------------- Constructors

    /**
     * Creates a default DeployTask instance.
     */
    public DeployTask() {
        defaultFileSet = new FileSet();
    }

    // ------------------------------------------------------ Public properties

    /**
     * Add the given fileSet to the list of {@link #fileSets}.
     *
     * @param fileSet the fileSet to add to the list of {@link #fileSets}.
     */
    public void addFileSet(FileSet fileSet) {
        if (!fileSets.contains(fileSet)) {
            fileSets.add(fileSet);
        }
    }

    /**
     * Return the list of the {@link #fileSets}.
     *
     * @return the list of fileSets
     */
    public List<FileSet> getFileSets() {
        return fileSets;
    }

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
        defaultFileSet.setExcludes(excludes);
    }

    /**
     * Set the Ant <tt>includes</tt> pattern for the source {@link #dir}.
     *
     * @param includes the Ant <tt>includes</tt> pattern for the source
     * {@link #dir}.
     */
    public void setIncludes(String includes) {
        this.includes = includes;
        defaultFileSet.setIncludes(includes);
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
    @Override
    public void execute() throws BuildException {

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

        if (!isDir(dir) && getFileSets().isEmpty()) {
            throw new BuildException("no input dirs specified.");
        }

        if (dir != null && dir.exists()) {
            defaultFileSet.setDir(dir);
            getFileSets().add(defaultFileSet);
            defaultFileSet.setDefaultexcludes(true);
            if (TaskUtils.isBlank(getIncludes())) {
                // Set default standard includes
                defaultFileSet.setIncludes("**/*.jar, classes");
            }
        }

        for (FileSet fileSet : getFileSets()) {
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(
                getProject());
            String files[] = directoryScanner.getIncludedFiles();
            String dirs[] = directoryScanner.getIncludedDirectories();
            String resources[] = (String[]) TaskUtils.addAll(files, dirs);
            deployResources(fileSet.getDir(), resources);
        }

        writeReport();
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Deploy the resources specified by the given filenames.
     *
     * @param filenames the filenames for the resources to deploy.
     */
    private void deployResources(File dir, String filenames[]) {
        try {
            DeployReport report = new DeployReport();

            for (int i = 0; i < filenames.length; i++) {
                String filename = filenames[i];
                String path = getCanonicalPath(dir);
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
                        deploy.getDeployed(), deploy.getOutdated(), reportContent);
                }
            }

            appendResourceListing(dir, filenames);

        } catch(IOException ioe) {
            throw new BuildException(ioe.getClass().getName() + ":" + ioe.getMessage(), ioe);
        }
    }

    /**
     * Write the deploy report to a report file.
     *
     * @throws BuildException if the build fails
     */
    private void writeReport() {
        Writer reportWriter = null;
        try {
            InputStream is = TaskUtils.getResourceAsStream("/report-template.html", DeployTask.class);
            String template = null;

            if (is == null) {
                System.out.println("The report template 'report-template.html' could not be found on the classpath. No report will be generated.");
            } else {
                template = TaskUtils.toString(is);
            }

            if (template != null) {
                String reportContent = template;
                reportContent = reportContent.replace("{0}", toDir.getCanonicalPath());
                reportContent = reportContent.replace("{1}", getDeployableSourceListingAsHtml());
                reportContent = reportContent.replace("{2}", getReportContentAsHtml());

                File reportFile = getUniqueReportFile();
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
     * Return a unique file where the report can be written to.
     *
     * @return the file the report can be written to
     */
    private File getUniqueReportFile() {
        String reportName = "deployed";
        File reportFile = new File(reportName + ".html");

        // Find unique report name
        int count = 0;
        while (reportFile.exists()) {
            count++;
            reportFile = new File(reportName + "-" + count + ".html");
        }
        return reportFile;
    }

    /**
     * Returns the canonical path of the given dir.
     *
     * @param dir the dir from which to return the canonical path of
     * @return the canonical path of the given dir
     * @throws IOException if the canonical path cannot be looked up
     */
    private String getCanonicalPath(File dir) throws IOException {
        String path = dir.getCanonicalPath();
        return path.endsWith(File.separator) ? path : path + File.separator;
    }

    /**
     * Return the HTML representation of the given filenames.
     * @param filenames the filenames to represent as HTML
     *
     * @param filenames the filenames to represent as HTML
     * @return return HTML representation of the given filenames
     * @throws IOException if an IO exception occurs
     */
    private void appendResourceListing(File dir, String[] filenames) throws IOException {
        StringBuilder buffer = new StringBuilder();

        if (filenames != null && filenames.length > 0) {
            String path = getCanonicalPath(dir);

            for (String filename : filenames) {
                if (filename.endsWith(".jar")) {
                    JarFile jarFile = new JarFile(new File(path, filename));
                    if (jarFile.getEntry("META-INF/resources") != null) {
                        render(buffer, filename);
                    } else if (jarFile.getEntry("META-INF/web") != null) {
                        render(buffer, filename);
                    }
                } else {
                    File file = new File(path, filename);
                    File resourceFile = new File(file, "META-INF/resources");
                    if (resourceFile.exists()) {
                        render(buffer, filename);
                    } else {
                        resourceFile = new File(file, "META-INF/web");
                        if (resourceFile.exists()) {
                            render(buffer, filename);
                        }
                    }
                }
            }
        }

        deployableSourceListing.append(buffer.toString());
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

    /**
     * Return true if the given file is a directory, false otherwise.
     *
     * @param file file to check if its a directory
     * @return true if the given file is a directory, false otherwise
     */
    private boolean isDir(File file) {
        if (file == null || !file.exists() || !file.isDirectory()) {
            return false;
        }
        return true;
    }

    /**
     * Return the HTML representation of the report content generated by the
     * deployed resources.
     *
     * @return the HTML representation of the report content generated by the
     * deployed resources
     */
    private String getReportContentAsHtml() {
        String result = reportContent.toString();

        // If no report entries were made, print a feedback message
        if (TaskUtils.isBlank(result)) {

            if (deployableSourceListing.length() == 0) {
                // If no deployable sources were found, print a warning message
                result = "<h3 class='warning'>No deployable resources were found</h3>";
            } else {
                // Otherwise we assume that all resources are already deployed
                // in the target folder
                result = "<h3 class='success'>All resources are successfully deployed</h3>";
            }
        }
        return result;
    }

    /**
     * Return the HTML representation of the list of deployable sources.
     *
     * @return the HTML representation of the list of deployable sources
     */
    private String getDeployableSourceListingAsHtml() {
        String listingAsString = deployableSourceListing.toString();
        if (listingAsString.length() == 0) {
            listingAsString = "<li><span style=\"color:blue\">No jars or folders were found with deployable resources.</span></li>";
        }
        return listingAsString;
    }
}
