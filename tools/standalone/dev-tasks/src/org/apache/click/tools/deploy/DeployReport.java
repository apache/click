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

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Provides a entries writer for deployed resources.
 */
class DeployReport {

    /**
     * Write the given entries entries to the given writer.
     *
     * @param source the source folder where the resources are located
     * @param destination the destination folder where the resources are
     * deployed to
     * @param deployed the list of resources that was deployed
     * @param outdated the list of resources that was outdated
     * @param writer the writer to write the entries to
     * @throws IOException if a write error occurs
     */
    public void writeReport(String source, String destination,
        List<DeployReportEntry> deployed, List<DeployReportEntry> outdated,
        Writer writer) throws IOException {

        if (source == null) {
            throw new IllegalArgumentException("source is null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("destination is null");
        }
        if (writer == null) {
            throw new IllegalArgumentException("writer is null");
        }

        if (deployed.size() < 0 && outdated.size() < 0) {
            return;
        }

        writer.append("<div class='container'>\n");
        writer.append("<h4><a name='").append(TaskUtils.getFilename(source)).append("'></a>");
        if (source.endsWith(".jar")) {
            writer.append("Jar: ");
        } else {
            writer.append("Folder: ");
        }
        writer.append(source);
        writer.append("</h4>\n");

        if (deployed.size() > 0) {
            writer.append("<div class='deployedHeader'>Deployed Successfully</div>\n");
            writeReportEntries(source, destination, deployed, writer);
        }
        if (outdated.size() > 0) {
            writer.append("<div class='outdatedHeader'>Outdated</div>\n");
            writeReportEntries(source, destination, outdated, writer);
        }

        writer.append("</div>\n");
    }

    /**
     * Write the given entries entries to the given writer.
     *
     * @param source the source folder where the resources are located
     * @param destination the destination folder where the resources are
     * deployed to
     * @param entries the list of resources that was deployed
     * @param writer the writer to write the entries to
     * @throws IOException if a write error occurs
     */
    private void writeReportEntries(String source, String destination,
        List<DeployReportEntry> entries, Writer writer) throws IOException {

        if (entries.size() > 0) {
            writer.append("<table>\n");
            writer.append("<tr>");
            writer.append("<th>Source - ").append(source).append("</th>");
            writer.append("<th>Destination - ").append(destination).append("</th>");
            writer.append("</tr>\n");
            for (DeployReportEntry entry : entries) {
                writer.append("<tr>");
                writer.append("<td>");
                writer.append(entry.source);
                writer.append("</td>");
                writer.append("<td>");
                writer.append(entry.destination);
                writer.append("</td>");
                writer.append("</tr>\n");
            }
            writer.append("</table>\n");
        }
    }
}
