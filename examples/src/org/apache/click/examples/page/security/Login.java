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
package org.apache.click.examples.page.security;

import javax.annotation.Resource;

import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.PasswordField;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.User;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.examples.service.UserService;
import org.apache.click.extras.control.PageSubmit;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Provides a user authentication login Page.
 */
@Component
public class Login extends BorderPage {

    private static final long serialVersionUID = 1L;
    private Form form = new Form("form");
    private HiddenField redirectField = new HiddenField("redirect", String.class);

    private TextField usernameField = new TextField("username", true);
    private PasswordField passwordField = new PasswordField("password", true);

    @Resource(name="userService")
    private UserService userService;

    // Constructor ------------------------------------------------------------

    public Login() {
        // Add form to page
        addControl(form);

        // Setup form
        usernameField.setMaxLength(20);
        usernameField.setMinLength(5);
        usernameField.setFocus(true);
        form.add(usernameField);

        passwordField.setMaxLength(20);
        passwordField.setMinLength(5);
        form.add(passwordField);

        form.add(new Submit("ok", " OK ", this, "onOkClicked"));
        form.add(new PageSubmit("cancel", HomePage.class));

        form.add(redirectField);
    }

    // Event Handlers ---------------------------------------------------------

    @Override
    public void onInit() {
        super.onInit();

        String username = null;

        if (getContext().isPost()) {
            username = getContext().getRequestParameter("username");

        } else {
            username = getContext().getCookieValue("username");
            if (username != null) {
                usernameField.setValue(username);
                usernameField.setFocus(false);
                passwordField.setFocus(true);
            }
        }
    }

    public boolean onOkClicked() {
        if (form.isValid()) {
            User user = new User();
            form.copyTo(user);

            if (userService.isAuthenticatedUser(user)) {

                user = userService.getUser(user.getUsername());
                getContext().setSessionAttribute("user", user);

                getContext().setCookie("username",
                                       user.getUsername(),
                                       Integer.MAX_VALUE);

                String redirectValue = redirectField.getValue();
                if (StringUtils.isNotBlank(redirectValue)) {
                    setRedirect(redirectValue);

                } else {
                    setRedirect(Secure.class);
                }

            } else {
                form.setError(getMessage("authentication-error"));
            }
        }

        return true;
    }

}
