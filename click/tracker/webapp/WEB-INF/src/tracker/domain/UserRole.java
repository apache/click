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

import java.io.Serializable;

/**
 * Provides UserRole domain object.
 *
 * @author Malcolm Edgar
 */
public class UserRole implements Serializable {
    
    protected User user;
    
    protected String role;
    
    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return toString().hashCode(); 
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (object instanceof UserRole) {
            return toString().equals(object.toString());            
        } else {
            return false;
        }
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return getClass().getName() + "[" +
            "user=" + ((user != null) ? user.getUsername() : "null") +
            "role=" + role +
            "]";
    }

    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}
