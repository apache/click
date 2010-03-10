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
package org.apache.click.examples.control.html;

import org.apache.click.Control;
import org.apache.click.control.AbstractContainer;
import org.apache.click.control.Field;

/**
 * This control provides feedback for a Field.
 * <p/>
 * If the field is required, FeedbackBorder will add a "required" css class.
 * <p/>
 * If the field is invalid, FeedbackBorder will display the field error message.
 */
public class FeedbackBorder extends AbstractContainer {

    private static final long serialVersionUID = 1L;

    public Control insert(Control control, int index) {

        // Enforce rule that only 1 control can be added
        if (getControls().size() > 0) {
            throw new IllegalStateException(
                "Only one control is allowed on FeedbackBorder.");
        }
        if (control == null) {
            throw new IllegalArgumentException("Control cannot be null");
        }

        // Lets assume only fields are allowed
        if (!(control instanceof Field)) {
            throw new IllegalArgumentException(
                "Only fields are allowed on FeedbackBorder.");
        }

        // Always insert the control in the first position
        super.insert(control, 0);

        return control;
    }

    @Override
    public void onRender() {
        super.onRender();

        Field field = (Field) getControls().get(0);

        // Add required css class
        if (field.isRequired()) {
            field.addStyleClass("required");
            super.insert(new Html("<span class=\"required\">&nbsp;</span>"),
                getControls().size());
        }

        // If field is invalid, add error message
        if (!field.isValid()) {
            super.insert(new Html("<span class=\"error\">" + field.getError() +
                "</span>"), getControls().size());
        }
    }
}
