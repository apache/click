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
package org.apache.click.examples.service;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import org.apache.click.examples.domain.PostCode;
import org.apache.click.extras.cayenne.CayenneTemplate;
import org.springframework.stereotype.Component;

/**
 * Provides a Postcode Service.
 *
 * @see PostCode
 */
@Component
public class PostCodeService extends CayenneTemplate {

    @SuppressWarnings("unchecked")
    public List<String> getPostCodeLocations(String location) {
        SelectQuery query = new SelectQuery(PostCode.class);

        query.andQualifier(ExpressionFactory.likeIgnoreCaseExp(PostCode.LOCALITY_PROPERTY, location + "%"));

        query.addOrdering(PostCode.LOCALITY_PROPERTY, true);

        query.setFetchLimit(10);

        List list = performQuery(query);

        for (int i = 0; i < list.size(); i++) {
            PostCode postCode = (PostCode) list.get(i);
            String value = postCode.getLocality() + ", " + postCode.getState() + " " + postCode.getPostCode();
            list.set(i, value);
        }

        return (List<String>) list;
    }

    @SuppressWarnings("unchecked")
    public List<PostCode> getPostCodes() {
        SelectQuery query = new SelectQuery(PostCode.class);

        query.setFetchLimit(10);

        return (List<PostCode>) performQuery(query);
    }
}
