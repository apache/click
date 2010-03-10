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
package org.apache.click.examples.springsecurity;

import javax.annotation.Resource;

import org.apache.click.examples.domain.User;
import org.apache.click.examples.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Provides a Spring Security (ACEGI) UserDetailsService for loading users.
 */
@Component
public class UserDetailsService implements org.springframework.security.userdetails.UserDetailsService {

    @Resource(name="userService")
    private UserService userService;

    /**
     * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(String)
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {

        User user = userService.getUser(username);

        if (user != null) {
            return new UserDetailsAdaptor(user);

        } else {
            throw new UsernameNotFoundException("UserDetailsService.loadUserByUsername()");
        }
    }

}
