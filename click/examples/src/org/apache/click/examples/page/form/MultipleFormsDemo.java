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
package org.apache.click.examples.page.form;

import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides an example of multiple Forms on one page using submit checks.
 */
public class MultipleFormsDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form1 = new Form("form1");
    private Form form2 = new Form("form2");

    //  Event Handlers --------------------------------------------------------


    /**
     * @see org.apache.click.Page#onSecurityCheck()
     */
    @Override
    public boolean onSecurityCheck() {
        String pagePath = getContext().getPagePath(getClass());

        // call forms onSubmitCheck
        boolean form1SubmitCheckSucceed = form1.onSubmitCheck(this, pagePath);
        boolean form2SubmitCheckSucceed = form2.onSubmitCheck(this, pagePath);

        if (!form1SubmitCheckSucceed) {
            // if form1 failed the submit check, set error message and stop further processing
            getContext().setFlashAttribute("error", "You have made an invalid form submission for '" + form1.getName() + "'");
            return false;

        } else if (!form2SubmitCheckSucceed) {
            // if form2 failed the submit check, set error message and stop further processing
            getContext().setFlashAttribute("error", "You have made an invalid form submission for '" + form2.getName() + "'");
            return false;

        } else {
            // if both forms succeeded the check, continue processing the request
            return true;
        }
    }

    /**
     * @see org.apache.click.Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

        // construct form1
        form1.add(new TextField("name"));
        form1.add(new Submit("save", this, "onForm1Submit"));

        // construct form2
        form2.add(new TextField("name"));
        form2.add(new Submit("save", this, "onForm2Submit"));

        // add form1 and form2 to the page controls.
        addControl(form1);
        addControl(form2);
    }

    public boolean onForm1Submit() {
        if (form1.isValid()) {
            // NOTE: to correctly implement the redirect-after-post pattern,
            // uncomment the lines below.
            //redirectAfterPost();
            //return false;
        }

        return true;
    }

    public boolean onForm2Submit() {
        if (form2.isValid()) {

            // NOTE: to correctly implement the redirect-after-post pattern,
            // uncomment the lines below.
            //redirectAfterPost();
            //return false;
        }

        return true;
    }

    // Public Methods ---------------------------------------------------------

    public void redirectAfterPost() {
        // redirect back to this page after the post
        setRedirect(this.getClass());
    }
}
