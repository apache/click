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
package org.apache.click.extras.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.control.Checkbox;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.TextField;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.DoubleField;
import org.apache.click.extras.control.IntegerField;
import org.apache.click.extras.control.LongField;
import org.apache.click.util.ClickUtils;

public class ExtrasClickUtilsTest extends TestCase {

    private static final java.util.Date DATE_OF_BIRTH;
    private static final String NAME = "john smith";
    private static final Integer ID = new Integer(1234);
    private static final int INT = 98;
    private static final boolean BOOLEAN = true;
    private static final double DOUBLE = -87.23;
    private static final String TELEPHONE = "9877262";
    
    static {
        Calendar calendar = new GregorianCalendar(2000, 01, 02);
        DATE_OF_BIRTH = calendar.getTime();
    }

    @Override
    protected void setUp() {
    	MockContext.initContext(Locale.ENGLISH);
    }

    public void testCopyFormToObject() {

    	// set up the form
        Form form = new Form("sample");
        
        IntegerField idField = new IntegerField("id");
        form.add(idField);

        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);

        TextField nameField = new TextField("name");
        fieldset.add(nameField);
        
        DateField dateField = new DateField("dateOfBirth");
        fieldset.add(dateField); 
        
        IntegerField intField = new IntegerField("int");
        form.add(intField);

        DoubleField doubleField = new DoubleField("double");
        form.add(doubleField);
               
        Checkbox checkBox = new Checkbox("boolean");
        form.add(checkBox);  
        
        LongField telephoneField = new LongField("telephone");
        form.add(telephoneField);
        
        HiddenField hidden = new HiddenField("hidden", String.class);
        form.add(hidden);

        // Populate fields
        idField.setValueObject(ID);
        nameField.setValue(NAME);
        dateField.setValueObject(DATE_OF_BIRTH);
        intField.setValue(String.valueOf(INT));
        doubleField.setValue(String.valueOf(DOUBLE));
        checkBox.setChecked(BOOLEAN);
        telephoneField.setValue(TELEPHONE);

        // copy form to object
        SampleObject sampleObject = new SampleObject();
        ClickUtils.copyFormToObject(form, sampleObject, true);

        // has the object been configured correctly?
        assertEquals(idField.getInteger(), sampleObject.getId());
        assertEquals(nameField.getValue(), sampleObject.getName());
        assertEquals(dateField.getValueObject(), sampleObject.getDateOfBirth());
        assertEquals(telephoneField.getValueObject().toString(), sampleObject.getTelephone());
        assertTrue(sampleObject.getInt() == intField.getInteger().intValue());
        assertTrue(sampleObject.getDouble() == doubleField.getDouble().doubleValue());
        assertTrue(sampleObject.isBoolean() == checkBox.isChecked());
        
        // Test object path copying
        
        User user = new User();
        user.setAddress(new Address());
        user.getAddress().setState(new State());
        
        form = new Form();
        TextField codeField = new TextField("address.state.code");
        codeField.setValue("NSW");
        form.add(codeField);
        form.copyTo(user, true);
        assertEquals("NSW", user.getAddress().getState().getCode());
    }

    public void testCopyObjectToForm() {
        // set up the form
        Form form = new Form("sample");
        
        IntegerField idField = new IntegerField("id");
        form.add(idField);

        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);

        TextField nameField = new TextField("name");
        fieldset.add(nameField);
        
        DateField dateField = new DateField("dateOfBirth");
        fieldset.add(dateField); 
        
        IntegerField intField = new IntegerField("int");
        form.add(intField);
                
        DoubleField doubleField = new DoubleField("double");
        form.add(doubleField);
               
        Checkbox checkBox = new Checkbox("boolean");
        form.add(checkBox);
        
        HiddenField hidden = new HiddenField("hidden", String.class);
        form.add(hidden);
        
        // Populate object
        SampleObject sampleObject = new SampleObject();
        sampleObject.setId(ID);
        sampleObject.setName(NAME);
        sampleObject.setDateOfBirth(DATE_OF_BIRTH);
        sampleObject.setInt(INT);
        sampleObject.setDouble(DOUBLE);
        sampleObject.setBoolean(BOOLEAN);

        // copy object to form
        ClickUtils.copyObjectToForm(sampleObject, form, true);

        // has the form been configured correctly?
        assertEquals(sampleObject.getId(), idField.getInteger());
        assertEquals(sampleObject.getName(), nameField.getValue());
        assertEquals(sampleObject.getDateOfBirth(), dateField.getDate());
        assertTrue(sampleObject.getInt() == intField.getInteger().intValue());
        assertTrue(sampleObject.getDouble() == doubleField.getDouble().doubleValue());
        assertTrue(sampleObject.isBoolean() == checkBox.isChecked());
        
        // Test object path copying
        
        User user = new User();
        user.setAddress(new Address());
        user.getAddress().setState(new State());
        user.getAddress().getState().setCode("NSW");
        
        form = new Form();
        TextField codeField = new TextField("address.state.code");
        form.add(codeField);
        form.copyFrom(user, true);
        assertEquals("NSW", codeField.getValueObject());
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "malcolm");
        form = new Form();
        TextField nameField2 = new TextField("name");
        form.add(nameField2);
        form.copyFrom(map, true);
        assertEquals("malcolm", nameField2.getValue());
    }
    
    public void testCopyToNullNestedObject() {
        final String lineOne = "55 Dunkley Avenue";
        final String code = "NSW";
        final boolean active = false;
        final Boolean registered = Boolean.TRUE;
        
        Form form = new Form();
        
        IntegerField idField = new IntegerField("address.id");
        form.add(idField);
        TextField lineOneField = new TextField("address.lineOne");
        lineOneField.setValue(lineOne);
        form.add(lineOneField);
        Checkbox activeField = new Checkbox("address.active");
        activeField.setChecked(active);
        form.add(activeField);
        Checkbox registeredField = new Checkbox("address.registered");
        registeredField.setValueObject(registered);
        form.add(registeredField);
        TextField codeField = new TextField("address.state.code");
        codeField.setValue(code);
        form.add(codeField);
        
        User user = new User();        
        form.copyTo(user, true);
        
        assertNull(user.getAddress().getId());
        assertEquals(lineOne, user.getAddress().getLineOne());        
        assertEquals(active, user.getAddress().isActive()); 
        assertEquals(registered, user.getAddress().isRegistered());
        assertEquals(code, user.getAddress().getState().getCode());        
    }

}
