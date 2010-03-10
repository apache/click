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

import org.apache.click.control.AbstractControl;
import org.apache.click.control.Field;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * This control associates an HTML <em>label</em> with a target field.
 */
public class FieldLabel extends AbstractControl {

    private static final long serialVersionUID = 1L;

    private Field target;

    private String label;

    public FieldLabel(Field target) {
        this(target, ClickUtils.toLabel(target.getName()), null);
    }

    public FieldLabel(Field target, String label) {
        this(target, label, null);
    }

    public FieldLabel(Field target, String label, String accessKey) {
        this.target = target;
        this.label = label + ":";
        if (accessKey != null) {
            setAttribute("accesskey", accessKey);
        }
    }

    public String getTag() {
        return "label";
    }

    // Override render to produce a html label for example:
    // <label for="firstname">Firstname:</label>
    public void render(HtmlStringBuffer buffer) {
        // Open tag: <label
        buffer.elementStart(getTag());

        // Set attribute to target field's id
        setAttribute("for", target.getId());

        // Render all the labels attributes
        appendAttributes(buffer);

        // Close tag: <label for="firstname">
        buffer.closeTag();

        // Add label text: <label for="firstname">Firstname:
        buffer.append(label);

        // Close tag: <label for="firstname">Firstname:</label>
        buffer.elementEnd(getTag());
    }

}
