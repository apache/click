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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Provides resourceName deployment operations to copying resources from a source
 * jar/folder to a target folder.
 * <p/>
 * This class also tracks which resources were deployed, skipped or outdated.
 * This information can then be used to write a summary report of the resources.
 */
class Deploy {

    // -------------------------------------------------------------- Variables

    /** The outdated report entries. */
    private List<DeployReportEntry> outdated = new ArrayList();

    /** The deployed report entries. */
    private List<DeployReportEntry> deployed = new ArrayList();

    // ------------------------------------------------------ Public Properties

    /**
     * Return the outdated report entries.
     *
     * @return the outdated report entries.
     */
    public List<DeployReportEntry> getOutdated() {
        return outdated;
    }

    /**
     * Return the deployed report entries.
     *
     * @return the deployed report entries.
     */
    public List<DeployReportEntry> getDeployed() {
        return deployed;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Deploy all resources from the jar to the given target folder.
     *
     * @param jar the jar file to extract resources from
     * @param target the target folder to deploy the resources to
     * @return true if resources was deployed, false otherwise
     * @throws IOException if a IO exception occurs
     */
    public boolean deployResourcesInJar(File jar, File target) throws IOException {
        boolean hasDeployed = false;

        // Mark the size of the reportEntries before deployment
        int before1 = deployed.size();
        int before2 = outdated.size();

        // example jar    -> lib/click-core-2.1.0.jar
        // example target -> c:/dev/webapp/
        if (jar == null) {
            throw new IllegalArgumentException("Jar cannot be null");
        }

        JarFile jarFile = null;

        try {

            jarFile = new JarFile(jar);
            JarEntry jarEntry = null;

            // Indicates whether feedback should be logged on the jar being
            // deployed
            boolean logFeedback = true;
            Enumeration<JarEntry> en = jarFile.entries();

            while (en.hasMoreElements()) {
                jarEntry = en.nextElement();

                // jarEntryName example -> META-INF/web/click/table.css
                String jarEntryName = jarEntry.getName();

                String prefix = "META-INF/web/";
                // Only deploy resources from "META-INF/web/"
                int pathIndex = jarEntryName.indexOf(prefix);
                if (pathIndex == 0) {
                    if (logFeedback) {
                        System.out.println("deploy files from jar -> "
                                         + jar.getCanonicalPath());

                        // Only provide feedback once per jar
                        logFeedback = false;
                    }
                    pathIndex += prefix.length();
                    // resourceName example -> click/table.css
                    String resourceName = jarEntryName.substring(pathIndex);
                    int index = resourceName.lastIndexOf('/');

                    File targetDir = new File(target.getPath());
                    if (index != -1) {
                        // resourceDir example -> click
                        String resourceDir =
                            resourceName.substring(0, index);
                        targetDir = new File(targetDir, resourceDir);
                    }

                    InputStream inputStream = null;
                    try {
                        inputStream = jarFile.getInputStream(jarEntry);
                        byte[] resourceData = TaskUtils.toByteArray(inputStream);

                        // Copy resources to web folder
                        deployFile(jarEntryName, resourceData, targetDir);

                    } finally {
                        TaskUtils.close(inputStream);
                    }
                }
            }
        } finally {
            TaskUtils.close(jarFile);
        }

        int after1 = deployed.size();
        int after2 = outdated.size();

        // If new reportEntries was added, set hasDeployeed to true
        if (before1 != after1 || before2 != after2) {
            hasDeployed = true;
        }
        return hasDeployed;
    }

    /**
     * Deploy all resources from the source folder to the given target folder.
     *
     * @param jar the jar file to extract resources from
     * @param target the target folder to deploy the resources to
     * @return true if resources was deployed, false otherwise
     * @throws IOException if a IO exception occurs
     */
    public boolean deployResourcesInDir(File source, File target) throws IOException {
        boolean hasDeployed = false;

        // Mark the size of the reportEntries before deployment
        int before1 = deployed.size();
        int before2 = outdated.size();

        // example source -> c:/source/webapp/WEB-INF/classes
        // example target -> c:/dev/webapp/
        if (source == null) {
            throw new IllegalArgumentException("Jar cannot be null");
        }

        final String prefix = "META-INF/web";

        if (source.exists()) {

            Iterator files = TaskUtils.listFiles(new File(source, prefix), new FilenameFilter() {
                public boolean accept(File file, String name) {
                    if (file.isDirectory()) {
                        return false;
                    }
                    
                    String path = file.getAbsolutePath();
                    path = path.replace('\\', '/');
                    return path.indexOf(prefix) >= 0;
                }
            }).iterator();

            boolean logFeedback = true;
            while (files.hasNext()) {
                // example file -> c:/source/webapp/WEB-INF/classes/META-INF/web/click/table.css
                File file = (File) files.next();

                // Guard against loading folders -> META-INF/web/click/
                if (file.isDirectory()) {
                    continue;
                }

                String fileName = file.getCanonicalPath().replace('\\', '/');

                // Only deploy resources from "META-INF/web/"
                int pathIndex = fileName.indexOf(prefix);
                if (pathIndex != -1) {
                    if (logFeedback) {
                        System.out.println("load files from folder -> " +
                            source.getAbsolutePath());

                        // Only provide feedback once per source
                        logFeedback = false;
                    }
                    pathIndex += prefix.length();

                    fileName = fileName.substring(pathIndex);
                    int index = fileName.lastIndexOf('/');

                    File targetDir = new File(target.getPath());
                    if (index != -1) {
                        // resourceDir example -> click
                        String resourceDir =
                            fileName.substring(0, index);
                        targetDir = new File(targetDir, resourceDir);
                    }

                    InputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(file);
                        byte[] resourceData = TaskUtils.toByteArray(inputStream);

                        // Copy resources to web folder
                        deployFile(fileName, resourceData, targetDir);

                    } finally {
                        TaskUtils.close(inputStream);
                    }
                }
            }
        }

        int after1 = deployed.size();
        int after2 = outdated.size();

        // If new reportEntries was added, set hasDeployeed to true
        if (before1 != after1 || before2 != after2) {
            hasDeployed = true;
        }

        return hasDeployed;
    }

    /**
     * Deploy a resource consisting of the resourceName and resourceData to the
     * target folder.
     *
     * @param resourceName the name of the resource to deploy
     * @param resourceData the resource data as a byte array
     * @param target the target folder to deploy the resource to
     * @throws IOException if a IO exception occurs
     */
    private void deployFile(String resourceName, byte[] resourceData, File target) {

        if (TaskUtils.isBlank(resourceName)) {
            String msg = "resource parameter cannot be empty";
            throw new IllegalArgumentException(msg);
        }

        if (target == null) {
            String msg = "Null targetDir parameter";
            throw new IllegalArgumentException(msg);
        }

        if (resourceData == null) {
            String msg = "Null resourceData parameter";
            throw new IllegalArgumentException(msg);
        }

        try {

            // Create files deployment directory
            if (!target.mkdirs()) {
                if (!target.exists()) {
                    String msg = "could not create deployment directory: "
                        + target.getCanonicalPath();
                    throw new IOException(msg);
                }
            }

            String destination = resourceName;
            int index = resourceName.lastIndexOf('/');
            if (index != -1) {
                destination = resourceName.substring(index + 1);
            }

            File destinationFile = new File(target, destination);

            if (destinationFile.exists()) {

                // Skip directories
                if (!destinationFile.isDirectory()) {

                    InputStream existingResource = new FileInputStream(
                        destinationFile);
                    try {
                        byte[] existingResourceData =
                            TaskUtils.toByteArray(existingResource);

                        boolean contentEquals = TaskUtils.areEqual(resourceData,
                            existingResourceData);

                        if (!contentEquals) {
                            // Indicate that an updated version of the resourceName
                            // is available
                            outdated.add(new DeployReportEntry(resourceName, resourceName.replace("META-INF/web", "")));
                        }

                    } finally {
                        TaskUtils.close(existingResource);
                    }
                }

            } else {

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(destinationFile);
                    fos.write(resourceData, 0, resourceData.length);

                    int lastIndex =
                        destination.lastIndexOf(File.separatorChar);
                    if (lastIndex != -1) {
                        destination =
                            destination.substring(lastIndex + 1);
                    }
                    deployed.add(new DeployReportEntry(resourceName, resourceName.replace("META-INF/web", "")));

                } finally {
                    TaskUtils.close(fos);
                }
            }

        } catch (IOException ioe) {
            String msg =
                "error occured deploying resource " + resourceName
                + ", error " + ioe;
            throw new RuntimeException(msg, ioe);

        } catch (SecurityException se) {
            String msg =
                "error occured deploying resource " + resourceName
                + ", error " + se;
            throw new RuntimeException(msg, se);
        }
    }
}
