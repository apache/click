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
package org.apache.click.extras.cayenne;

import java.util.List;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.query.Query;
import org.apache.click.util.DataProvider;

/**
 * Provides a Cayenne Query DataProvider class which will perform a lazily
 * load data.
 */
public class QueryDataProvider<T> implements DataProvider<T> {

    /** The Cayenne query to perform when the getData method is invoked. */
    protected Query query;

    // Constructors -----------------------------------------------------------

    /**
     * Create a Query data provider with the given query.
     *
     * @param query the Cayenne query to execute when the getData method is
     *     invoked
     */
    public QueryDataProvider(Query query) {
        this.query = query;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Return the data list results of the query, performing the query if the
     * data list is null.
     *
     * @see DataProvider#getData()
     *
     * @return the results of the query
     */
    @SuppressWarnings("unchecked")
    public List<T> getData() {
        return BaseContext.getThreadObjectContext().performQuery(query);
    }

}
