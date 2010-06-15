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
package org.apache.click.util;

public class Address {
    private Integer id;
    private String lineOne;
    private String lineTwo;
    private String lineThree;
    private State state;
    private boolean active;
    private Boolean registered;
    
    public Boolean isRegistered() {
        return registered;
    }
    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLineOne() {
        return lineOne;
    }
    public void setLineOne(String lineOne) {
        this.lineOne = lineOne;
    }
    public String getLineThree() {
        return lineThree;
    }
    public void setLineThree(String lineThree) {
        this.lineThree = lineThree;
    }
    public String getLineTwo() {
        return lineTwo;
    }
    public void setLineTwo(String lineTwo) {
        this.lineTwo = lineTwo;
    }
}
