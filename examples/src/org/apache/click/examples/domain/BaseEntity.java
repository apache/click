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
package org.apache.click.examples.domain;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.PersistenceState;

/**
 * Provides an base entity CayenneDataObject class with id getter and isNew
 * methods. This class should be extended by the auto package classes.
 */
public class BaseEntity extends CayenneDataObject {

    private static final long serialVersionUID = 1L;

    /**
     * Convenience method to get an id that may be used by the view. There is
     * no setter as id is managed by Cayenne.
     *
     * @return the primary key of this Entity.
     */
    public Integer getId() {
        return (Integer) DataObjectUtils.pkForObject(this);
    }

    /**
     * Return true if the object is new or transient object.
     *
     * @return true if the object is new or transient object
     */
    public boolean isNew() {
        return getPersistenceState() == PersistenceState.TRANSIENT
                || getPersistenceState() == PersistenceState.NEW;
    }

}
