/*
 * Copyright 2005 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tracker.domain;

import java.util.Date;
import java.util.Set;

/**
 * Provides User domain object.
 *
 * @author Malcolm Edgar
 */
public class User {
    
    protected String username;
    
    protected String password;
    
    protected String email;
    
    protected String telephone;
    
    protected String mobile;
    
    protected String fax;
    
    protected boolean isActive = true;
    
    protected String createdBy;
    
    protected Date createdAt;
    
    protected String updatedBy;
    
    protected Date updatedAt;
    
    protected Set roles;
    
    public String toString() {
        return getClass().getName() + "[" +
            "username=" + username +
            ",password=" + password +
            ",email=" + email +
            ",telephone=" + telephone +
            ",mobile=" + mobile +
            ",fax=" + fax +
            ",isActive=" + isActive +
            ",createdBy=" + createdBy +
            ",createdAt=" + createdAt +
            ",updatedBy=" + updatedBy +
            ",updatedAt=" + updatedAt +
            "]";
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getMobile() {
        return mobile;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set getRoles() {
        return roles;
    }
    
    public void setRoles(Set roles) {
        this.roles = roles;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
