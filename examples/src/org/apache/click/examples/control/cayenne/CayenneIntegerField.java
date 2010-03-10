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
package org.apache.click.examples.control.cayenne;

import org.apache.click.extras.control.IntegerField;

/**
 * This IntegerField class preserves its value when it is copied from
 * an entity property that is null.
 */
public class CayenneIntegerField extends IntegerField {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new field for the specified name and label.
     *
     * @param name the field name
     * @param label the field label
     */
    public CayenneIntegerField(String name, String label) {
        super(name, label);
    }

    /**
     * #setValueObject is invoked when a Form attempts to bind an Object to its
     * fields.
     *
     * When a bean property with value 'null' is bound to IntegerField,
     * setValueObject will nullify the Integer value. This is not always
     * desirable as sometimes we want the value to be preserved.
     *
     * Here we override #setValueObject and explicitly check if the passed in
     * object is null. If it is we return without changing the value object.
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object == null) {
            return;
        }
        super.setValueObject(object);
    }
}
