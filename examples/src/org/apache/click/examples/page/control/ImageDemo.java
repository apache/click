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
package org.apache.click.examples.page.control;

import org.apache.click.control.Form;
import org.apache.click.control.ImageSubmit;
import org.apache.click.control.Label;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.Bindable;

/**
 * Provides an ImageSubmit control example.
 *
 * @author Malcolm Edgar
 */
public class ImageDemo extends BorderPage {

    private ImageSubmit colorSubmit;

    @Bindable protected Form buttonsForm = new Form();
    @Bindable protected Form form = new Form();

    public ImageDemo() {
        // Buttons Form
        ImageSubmit editSubmit = new ImageSubmit("edit", "/assets/images/edit.gif");
        editSubmit.setListener(this, "onEditClick");
        editSubmit.setTitle("Edit");
        buttonsForm.add(editSubmit);

        ImageSubmit deleteSubmit = new ImageSubmit("delete", "/assets/images/delete.gif");
        deleteSubmit.setListener(this, "onDeleteClick");
        deleteSubmit.setTitle("Delete");
        buttonsForm.add(deleteSubmit);

        // Colors Form
        form.add(new Label("label", "<b>Color Chooser</b>"));

        colorSubmit = new ImageSubmit("submit", "/assets/images/colors.gif");
        colorSubmit.setListener(this, "onColorClick");
        form.add(colorSubmit);
    }

    public boolean onEditClick() {
        addModel("buttonMsg", "Edit");
        return true;
    }

    public boolean onDeleteClick() {
        addModel("buttonMsg", "Delete");
        return true;
    }

    public boolean onColorClick() {
        int x = colorSubmit.getX();
        int y = colorSubmit.getY();

        String color = "no color";

        if (x > 3 && x < 31) {
            if (y > 3 && y < 31) {
                color = "Red";
            } else if (y > 44 && y < 71) {
                color = "Green";
            }
        } else if (x > 44 && x < 71) {
            if (y > 3 && y < 31) {
                color = "Blue";
            } else if (y > 44 && y < 71) {
                color = "White";
            }
        }

        String colorMsg = "<b>" + color + "</b>. <p/> " +
                          "[ x=" + x + ", y=" + y + " ]";

        addModel("colorMsg", colorMsg);

        return true;
    }
}
