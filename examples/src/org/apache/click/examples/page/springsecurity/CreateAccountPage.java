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
package org.apache.click.examples.page.springsecurity;

import javax.annotation.Resource;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.PasswordField;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.User;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.springsecurity.secure.SecurePage;
import org.apache.click.examples.service.UserService;
import org.apache.click.extras.control.EmailField;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountPage extends BorderPage {

    private Form form = new Form("form");
    private TextField fullNameField = new TextField(User.FULLNAME_PROPERTY, "Full Name", true);
    private EmailField emailField = new EmailField(User.EMAIL_PROPERTY);
    private TextField userNameField = new TextField(User.USERNAME_PROPERTY, true);
    private PasswordField passwordField = new PasswordField("password", true);
    private PasswordField passwordAgainField = new PasswordField("passwordAgain", "Password again", true);
    private HiddenField redirectField = new HiddenField("redirect", String.class);

    @Resource(name="authenticationManager")
    private AuthenticationManager authenticationManager;

    @Resource(name="userService")
    private UserService userService;

    // Constructor ------------------------------------------------------------

    public CreateAccountPage() {
        addControl(form);

        form.setDefaultFieldSize(30);

        form.add(fullNameField);
        form.add(emailField);
        form.add(userNameField);
        form.add(passwordField);
        form.add(passwordAgainField);

        Submit submit = new Submit("create");
        submit.setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onCreate();
            }
        });
        form.add(submit);

        form.add(redirectField);
    }

    // Event Handlers ---------------------------------------------------------

    @Override
    public void onInit() {
        super.onInit();

        if (getContext().isGet()) {
            redirectField.setValue(getContext().getRequestParameter("redirect"));
        }
    }

    public boolean onCreate() {
        if (form.isValid()) {

            String fullName = fullNameField.getValue();
            String email = emailField.getValue();
            String username = userNameField.getValue();
            String password1 = passwordField.getValue();
            String password2 = passwordAgainField.getValue();

            if (!password1.equals(password2)) {
                passwordField.setError("Password and password again do not match");
                return true;
            }

            User user = userService.getUser(username);

            if (user != null) {
                userNameField.setError(getMessage("usernameExistsError"));
                return true;
            }

            user = userService.createUser(fullName, email, username, password1);

            Authentication token = new UsernamePasswordAuthenticationToken(username, password1);
            Authentication result = authenticationManager.authenticate(token);
            SecurityContext securityContext = new SecurityContextImpl();
            securityContext.setAuthentication(result);
            SecurityContextHolder.setContext(securityContext);

            String path = redirectField.getValue();
            if (StringUtils.isNotBlank(path)) {
                setRedirect(path);
            } else {
                setRedirect(SecurePage.class);
            }
        }

        return true;
    }

}
