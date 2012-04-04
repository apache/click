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

import org.apache.commons.fileupload.FileItem;

public class SampleObject {
    private Integer id;
    private String name;
    private java.util.Date dateOfBirth;
    private boolean _boolean;
    private int _int;
    private double _double;
    private String telephone;
    private boolean active;
    private FileItem file;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(java.util.Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean isBoolean() {
        return _boolean;
    }

    public void setBoolean(boolean b) {
        _boolean = b;
    }

    public double getDouble() {
        return _double;
    }

    public void setDouble(double d) {
        _double = d;
    }

    public int getInt() {
        return _int;
    }

    public void setInt(int i) {
        _int = i;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setFile(FileItem file){
    	this.file = file;
    }

    public FileItem getFile(){
    	return this.file;
    }

}
