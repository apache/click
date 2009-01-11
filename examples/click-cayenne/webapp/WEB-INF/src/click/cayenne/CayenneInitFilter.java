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
package click.cayenne;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.WebApplicationContextFilter;
import org.apache.cayenne.map.DataMap;

/**
 * Provides a customized WebApplicationContextFilter to initialize 
 * the Cayenne runtime and creates a demo database schema.
 * 
 * @see WebApplicationContextFilter
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class CayenneInitFilter extends WebApplicationContextFilter {

    /**
     * Initialize the Cayenne web application context filter to initialize
     * the Cayenne runtime and create the demo database schema.
     * 
     * @see #init(FilterConfig)
     */
    public synchronized void init(FilterConfig config) throws ServletException {
        super.init(config);

        try {
            DataDomain cayenneDomain = 
                Configuration.getSharedConfiguration().getDomain();
            DataMap dataMap = cayenneDomain.getMap("HRMap");
            DataNode dataNode = cayenneDomain.getNode("HRDB");

            initDatabaseScema(dataNode, dataMap);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error creating database", e);
        }
    }

    /**
     * Create the demonstration database schema using the given Cayenne
     * DataNode and DataMap.
     * 
     * @param dataNode the Cayenne DataNode
     * @param dataMap the Cayenne DataMap
     * @throws Exception
     */
    private void initDatabaseScema(DataNode dataNode, DataMap dataMap) 
            throws Exception {

        DbGenerator generator = new DbGenerator(dataNode.getAdapter(), dataMap);
        generator.setShouldCreateFKConstraints(true);
        generator.setShouldCreatePKSupport(true);
        generator.setShouldCreateTables(true);
        generator.setShouldDropPKSupport(false);
        generator.setShouldDropTables(false);

        generator.runGenerator(dataNode.getDataSource());
    }
}
