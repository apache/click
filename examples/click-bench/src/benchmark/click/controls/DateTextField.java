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
package benchmark.click.controls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.click.control.TextField;

public class DateTextField extends TextField {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

    public DateTextField(String name) {
        super(name);
    }

    public Object getValueObject() {
        return getDate();
    }

    public void setValueObject(Object object) {
        if (object != null) {
            if (Date.class.isAssignableFrom(object.getClass())) {
                setDate((Date) object);

            } else {
                String msg =
                    "Invalid object class: " + object.getClass().getName();
                throw new IllegalArgumentException(msg);
            }
        }
    }

    public Object getDate() {
        if (value != null && value.length() > 0) {
            try {
                Date date = dateFormat.parse(value);
                return new Date(date.getTime());
            } catch (ParseException pe) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setDate(Date date) {
        if (date != null) {
            value = dateFormat.format(date);
        }
    }
}
