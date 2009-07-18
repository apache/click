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

/**
 * Provides a report entry.
 */
class DeployReportEntry {

    /** Specify the destination path of a resource. */
    public String destination;

    /** Specify the source path of a resource. */
    public String source;

    /**
     * Default constructor for the given source and destination.
     *
     * @param source path of a resource
     * @param destination path of a resource
     */
    public DeployReportEntry(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }
}
