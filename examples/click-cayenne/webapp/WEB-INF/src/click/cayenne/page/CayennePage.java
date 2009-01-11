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
package click.cayenne.page;

import org.apache.click.Page;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.access.DataContext;

/**
 * Provides a base Cayenne page which provides utility methods for retrieving 
 * DataObjects contained in the thread-bound DataContext.  
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class CayennePage extends Page {
    
    /**
     * Return the DataContext for the current thread.
     * 
     * @return the DataContext for the current thread
     */
    public DataContext getDataContext() {
        return DataContext.getThreadDataContext();
    }
    
    /**
     * Return the DataObject for the given class and primary key.
     * 
     * @param aClass the DataObject class
     * @param primaryKey the DataObject primary key
     * @return the DataObject for the given class and primary key
     */
    public DataObject getDataObject(Class aClass, Integer primaryKey) {
        return DataObjectUtils.objectForPK(getDataContext(),  
                                           aClass,
                                           primaryKey.intValue());
    } 
    
    /**
     * Return the DataObject for the given class and primary key.
     * 
     * @param aClass the DataObject class
     * @param primaryKey the DataObject primary key
     * @return the DataObject for the given class and primary key
     */
    public DataObject getDataObject(Class aClass, String primaryKey) {
        int objectPk = Integer.parseInt(primaryKey);
        System.out.println(objectPk + "," + getDataContext() + "," + aClass);
        return DataObjectUtils.objectForPK(getDataContext(),  
                                           aClass,
                                           objectPk);
    } 

}
