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
package trackman.domain;

/**
 * Provides an abstract Classification domain object.
 *
 * @author Malcolm Edgar
 */
public abstract class Classification {
   
    protected long id;
    
    protected long sortOrder;
    
    protected String description;
    
    protected boolean active = true;
    
    public String toString() {
        return getClass().getName() + "[" +
            "id=" + id +
            ",sortOrder=" + sortOrder +
            ",description=" + description +
            ",active=" + active +
            "]";
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(long sortOrder) {
        this.sortOrder = sortOrder;
    }
}