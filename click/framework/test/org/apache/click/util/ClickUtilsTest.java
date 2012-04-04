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

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.click.Context;
import org.apache.click.MockContext;
import org.apache.click.Page;
import org.apache.click.control.Checkbox;
import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.control.FileField;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.TextField;
import org.apache.click.fileupload.MockFileItem;
import org.apache.click.servlet.MockRequest;
import org.apache.commons.fileupload.FileItem;

/**
 * Tests for ClickUtils.
 */
public class ClickUtilsTest extends TestCase {

    /*
     * Define some test data.
     */
    private static final java.util.Date DATE_OF_BIRTH;
    private static final String NAME = "john smith";
    private static final Integer ID = new Integer(1234);
    private static final int INT = 98;
    private static final boolean BOOLEAN = true;
    private static final double DOUBLE = -87.23;
    private static final String TELEPHONE = "9877262";
    private static final FileItem FILEITEM = new MockFileItem();

    static {
        Calendar calendar = new GregorianCalendar(2000, 01, 02);
        DATE_OF_BIRTH = calendar.getTime();
    }

    /**
     * Setup a MockContext for each test.
     */
    @Override
    protected void setUp() {
    	MockContext.initContext(Locale.ENGLISH);
    }

    /**
     * Sanity checks for ClickUtils.copyFormToObject.
     */
    public void testCopyFormToObject() {

    	// set up the form
        Form form = new Form("sample");

        TextField idField = new TextField("id");
        form.add(idField);

        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);

        TextField nameField = new TextField("name");
        fieldset.add(nameField);

        TextField dateField = new TextField("dateOfBirth");
        fieldset.add(dateField);

        TextField intField = new TextField("int");
        form.add(intField);

        TextField doubleField = new TextField("double");
        form.add(doubleField);

        Checkbox checkBox = new Checkbox("boolean");
        form.add(checkBox);

        TextField telephoneField = new TextField("telephone");
        form.add(telephoneField);

        HiddenField hidden = new HiddenField("hidden", String.class);
        form.add(hidden);

        FileField fileField = new FileField("file");
        form.add(fileField);

        // Populate fields
        idField.setValueObject(ID);
        nameField.setValue(NAME);
        dateField.setValueObject(DATE_OF_BIRTH);
        intField.setValue(String.valueOf(INT));
        doubleField.setValue(String.valueOf(DOUBLE));
        checkBox.setChecked(BOOLEAN);
        telephoneField.setValue(TELEPHONE);
        fileField.setValueObject(FILEITEM);

        // copy form to object
        SampleObject sampleObject = new SampleObject();
        ClickUtils.copyFormToObject(form, sampleObject, true);

        // has the object been configured correctly?
        assertEquals(new Integer(idField.getValue()), sampleObject.getId());
        assertEquals(nameField.getValue(), sampleObject.getName());

        //NOTE the dateField was NOT copied to the sampleObject's Date property.
        //Use org.apache.click.extras.control.DateField in the extras project, to
        //copy a Date property.
        assertEquals(null, sampleObject.getDateOfBirth());
        assertEquals(telephoneField.getValueObject().toString(), sampleObject.getTelephone());
        assertEquals(new Integer(intField.getValue()).intValue(), sampleObject.getInt());
        assertEquals(new Double(doubleField.getValue()).doubleValue(), sampleObject.getDouble());
        assertEquals(checkBox.isChecked(), sampleObject.isBoolean());
        assertSame(fileField.getFileItem(), sampleObject.getFile());

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

    /**
     * Sanity checks for ClickUtils.copyObjectToForm.
     */
    public void testCopyObjectToForm() {
        // set up the form
        Form form = new Form("sample");

        TextField idField = new TextField("id");
        form.add(idField);

        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);

        TextField nameField = new TextField("name");
        fieldset.add(nameField);

        TextField dateField = new TextField("dateOfBirth");
        fieldset.add(dateField);

        TextField intField = new TextField("int");
        form.add(intField);

        TextField doubleField = new TextField("double");
        form.add(doubleField);

        Checkbox checkBox = new Checkbox("boolean");
        form.add(checkBox);

        HiddenField hidden = new HiddenField("hidden", String.class);
        form.add(hidden);

        FileField fileField = new FileField("file");
        form.add(fileField);

        // Populate object
        SampleObject sampleObject = new SampleObject();
        sampleObject.setId(ID);
        sampleObject.setName(NAME);
        sampleObject.setDateOfBirth(DATE_OF_BIRTH);
        sampleObject.setInt(INT);
        sampleObject.setDouble(DOUBLE);
        sampleObject.setBoolean(BOOLEAN);
        sampleObject.setFile(FILEITEM);

        // copy object to form
        ClickUtils.copyObjectToForm(sampleObject, form, true);

        // has the form been configured correctly?
        assertEquals(sampleObject.getId(), new Integer(idField.getValue()));
        assertEquals(sampleObject.getName(), nameField.getValue());
        assertEquals(sampleObject.getDateOfBirth().toString(), dateField.getValue());
        assertEquals(new Integer(intField.getValue()).intValue(), sampleObject.getInt());
        assertEquals(new Double(doubleField.getValue()).doubleValue(), sampleObject.getDouble());
        assertEquals(checkBox.isChecked(), sampleObject.isBoolean());
        assertNull(fileField.getValueObject());

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

    /**
     * Test that the map are populated from field values.
     *
     * CLK-278.
     */
    public void testCopyMapToForm() {

        Integer id = new Integer(9876);
        String name = "Rocky Balboa";
        Integer age = new Integer(61);
        String stateCode = "NSW";
        String street = "12 Short street";

        // Setup the map
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("name", name);
        map.put("age", age);
        map.put("address.street", street);
        map.put("address.state.code", stateCode);

        // Setup the form and fields
        Form form = new Form("form");
        TextField idField = new TextField("id");
        form.add(idField);

        // Create fieldset
        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);
        TextField nameField = new TextField("name");
        fieldset.add(nameField);

        TextField ageField = new TextField("age");
        form.add(ageField);
        TextField streetField = new TextField("address.street");
        form.add(streetField);
        TextField stateCodeField = new TextField("address.state.code");
        form.add(stateCodeField);

        // Copy the map values into the fields
        form.copyFrom(map, true);

        // Test that values were copied
        assertEquals(id, new Integer(idField.getValue()));
        assertEquals(name, nameField.getValue());
        assertEquals(age, new Integer(ageField.getValue()));
        assertEquals(street, streetField.getValue());
        assertEquals(stateCode, stateCodeField.getValue());
    }

    /**
     * Test that field value are copied to map.
     *
     * CLK-239.
     */
    public void testCopyFormToMap() {

        Integer id = new Integer(9876);
        String name = "Rocky Balboa";
        Integer age = new Integer(61);
        String stateCode = "NSW";
        String street = "12 Short street";

        // Setup the map with no values
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", null);
        map.put("name", null);
        map.put("age", null);
        map.put("address.street", null);
        map.put("address.state.code", null);

        // Setup the form and fields with initial values
        Form form = new Form("form");
        TextField idField = new TextField("id");
        idField.setValue(id.toString());
        form.add(idField);

        // Create fieldset
        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);
        TextField nameField = new TextField("name");
        nameField.setValue(name);
        fieldset.add(nameField);

        TextField ageField = new TextField("age");
        ageField.setValue(age.toString());
        form.add(ageField);
        TextField streetField = new TextField("address.street");
        streetField.setValue(street);
        form.add(streetField);
        TextField stateCodeField = new TextField("address.state.code");
        stateCodeField.setValue(stateCode);
        form.add(stateCodeField);

        // Copy the fields values back into the map
        form.copyTo(map, true);

        // Test that values were copied
        String copiedId = map.get("id").toString();
        assertEquals(id, new Integer(copiedId));
        assertEquals(name, map.get("name"));
        String copiedAge = map.get("age").toString();
        assertEquals(age, new Integer(copiedAge));
        assertEquals(street, map.get("address.street"));
        assertEquals(stateCode, map.get("address.state.code"));
    }

    /**
     * Test that null objects on the path are properly resolved and instantiated.
     */
    public void testCopyToNullNestedObject() {
        final String lineOne = "55 Dunkley Avenue";
        final String code = "NSW";
        final boolean active = false;
        final Boolean registered = Boolean.TRUE;

        Form form = new Form();

        TextField idField = new TextField("address.id");
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

    /**
     * Sanity checks for ClickUtils.toLabel.
     */
    public void testToLabel() {
        assertEquals("Customer", ClickUtils.toLabel("customer"));
        assertEquals("Customer Number", ClickUtils.toLabel("customerNumber"));
        assertEquals("Card PIN", ClickUtils.toLabel("cardPIN"));
    }

    /**
     * Sanity checks for ClickUtils.toMD5Hash.
     */
    public void testToMD5Hash() {
        assertEquals("5f4dcc3b5aa765d61d8327deb882cf99",
                     ClickUtils.toMD5Hash("password"));
    }

    /**
     * Sanity checks for ClickUtils.getParentMessages.
     */
    public void testGetParentMessages() {
        TextField textField = new TextField("test");

        Map<String, String> map = ClickUtils.getParentMessages(textField);
        assertNotNull(map);
        assertTrue(map.isEmpty());
        assertTrue(map == Collections.EMPTY_MAP);

        Page page = new Page();
        page.addControl(textField);

        Map<String, String> map2 = ClickUtils.getParentMessages(textField);
        assertNotNull(map2);
        assertEquals(1, map2.size());
        assertFalse(map2 == Collections.EMPTY_MAP);

        Page page2 = new Page();

        Form form = new Form("form");
        page2.addControl(form);

        TextField textField2 = new TextField("test");
        form.add(textField2);

        Map<String, String> map3 = ClickUtils.getParentMessages(textField2);
        assertNotNull(map3);
        assertEquals(1, map3.size());
        assertFalse(map3 == Collections.EMPTY_MAP);
    }

    /**
     * Sanity check for ClickUtils.decodeURL.
     */
    public void testDecodeURL() {

        String path = "value+with%20spaces";
        String value = ClickUtils.decodeURL(path);

        String expected = "value with spaces";
        assertEquals(expected, value);
    }

    /**
     * Sanity check for ClickUtils.encodeURL.
     */
    public void testEncodeURL() {
        String expected = "1000";
        String value = ClickUtils.encodeURL(expected);
        assertEquals(expected, value);

        // Java URLEncoder.encode uses HTML's application/x-www-form-urlencoded
        // MIME format, thus spaces are encoded to +, not %20
        String path = "value with spaces";
        value = ClickUtils.encodeURL(path);

        expected = "value+with+spaces";
        assertEquals(expected, value);
    }

    /**
     * Sanity check for ClickUtils.encodeUrl.
     */
    public void testEncodeUrl() {
        String expected = "1000";
        String value = ClickUtils.encodeUrl(expected, Context.getThreadLocalContext());
        assertEquals(expected, value);

        // Java URLEncoder.encode uses HTML's application/x-www-form-urlencoded
        // MIME format, thus spaces are encoded to +, not %20
        String path = "value with spaces";
        value = ClickUtils.encodeUrl(path, Context.getThreadLocalContext());

        expected = "value+with+spaces";
        assertEquals(expected, value);
    }

    /**
     * Sanity checks for ClickUtils.escapeHtml.
     */
    public void testEscape() {
        String value1 = "";
        String value2 = ClickUtils.escape(value1);
        assertEquals(value1, value2);
        assertTrue(value1 == value2);

        String value3 = "1234567890abcdefghijklmnopqrstuvwzyz";
        String value4 = ClickUtils.escape(value3);
        assertEquals(value3, value4);
        assertTrue(value3 == value4);

        assertEquals("&quot;", ClickUtils.escape("\""));
        assertEquals("&amp;", ClickUtils.escape("&"));
        assertEquals("&lt;", ClickUtils.escape("<"));
        assertEquals("&gt;", ClickUtils.escape(">"));
        assertEquals(" ", ClickUtils.escape(" "));

        assertEquals("\u00e0", ClickUtils.escape("à"));
        assertEquals("à", ClickUtils.escape("à"));
        assertEquals("\u00e0", ClickUtils.escape("\u00e0"));

        assertFalse(ClickUtils.requiresEscape((char) 0));
        assertTrue(ClickUtils.requiresEscape((char) 34));
        assertTrue(ClickUtils.requiresEscape((char) 38));
        assertTrue(ClickUtils.requiresEscape((char) 39));
        assertTrue(ClickUtils.requiresEscape((char) 60));
        assertTrue(ClickUtils.requiresEscape((char) 62));
        assertFalse(ClickUtils.requiresEscape((char) 63));
    }

    /**
     * Sanity checks for ClickUtils.escapeHtml.
     */
    public void testEscapeHtml() {
        String value1 = "";
        String value2 = ClickUtils.escapeHtml(value1);
        assertEquals(value1, value2);
        assertTrue(value1 == value2);

        String value3 = "1234567890abcdefghijklmnopqrstuvwzyz";
        String value4 = ClickUtils.escapeHtml(value3);
        assertEquals(value3, value4);
        assertTrue(value3 == value4);

        assertEquals("&quot;", ClickUtils.escapeHtml("\""));
        assertEquals("&amp;", ClickUtils.escapeHtml("&"));
        assertEquals("&lt;", ClickUtils.escapeHtml("<"));
        assertEquals("&gt;", ClickUtils.escapeHtml(">"));
        assertEquals(" ", ClickUtils.escapeHtml(" "));
        assertEquals("&agrave;", ClickUtils.escapeHtml("\u00e0"));

        assertFalse(ClickUtils.requiresHtmlEscape((char) 0));
        assertTrue(ClickUtils.requiresHtmlEscape((char) 34));
        assertTrue(ClickUtils.requiresHtmlEscape((char) 184));
        assertFalse(ClickUtils.requiresHtmlEscape((char) 999999));
    }

    /**
     * Sanity checks for ClickUtils.getMimeType.
     */
    public void testGetMimeType() {
        // Test mime type based on filename
        assertEquals("application/vnd.ms-excel", ClickUtils.getMimeType("worksheet.xls"));

        assertEquals("application/vnd.ms-excel", ClickUtils.getMimeType("WORKSHEET.XLS"));

        // Test mime type based on extension
        assertEquals("application/json", ClickUtils.getMimeType("json"));

        assertEquals("application/json", ClickUtils.getMimeType(".JSON"));

        try {
        	assertNull(ClickUtils.getMimeType("broken.xxx"));
        } catch (Exception e) {
        	assertTrue(false);
        }
    }

    /**
     * Sanity checks for encoding and decoding a password in a cookie.
     */
    public void testCookiePassword() {
        String username = "username";
        String password = "password";

        String cookie = ClickUtils.encodePasswordCookie(username, password, 12);

        String[] result = ClickUtils.decodePasswordCookie(cookie, 12);

        assertEquals(username, result[0]);
        assertEquals(password, result[1]);

        result = ClickUtils.decodePasswordCookie(cookie, 21);

        assertFalse(username.equals(result[0]));
        assertFalse(password.equals(result[1]));
    }

    /**
     *
     */
    public void testBindAndValidateForm() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        request.setParameter("form_name", "form");
        request.setParameter("firstName", "steve");

final String formError = "error";
        Form form = new Form("form") {

            @Override
            public void validate() {
                setError(formError);
            }
        };

        TextField field = new TextField("firstName");
        form.add(field);

        // assert field has no value
        assertEquals("", field.getValue());

        boolean valid = ClickUtils.bindAndValidate(form);
        assertFalse(valid);

        // assert that the field value is bound
        assertEquals("steve", field.getValue());

        // assert that the form error was reset, leaving form in a valid state
            assertEquals(null, form.getError());
        assertTrue(form.isValid());
    }

    /**
     * Test that Clickutils#saveState saves the state to the session.
     *
     * CLK-715
     */
    public void testSaveState() {
        String pagePath = "/page.htm";
        MockContext context = MockContext.initContext(pagePath);

        // Setup Form
        Form form = new Form("form");
        Field nameField  = new TextField("name");
        nameField.setValue("Steve");
        form.add(nameField);

        // Save form state to the session
        ClickUtils.saveState(form, form.getName(), context);

        // Test that page state is stored in session under the Page path
        assertNotNull(context.getSessionAttribute(pagePath));
    }

    /**
     * Test that ClickUtils#restoreState restore the state from the session.
     *
     * CLK-715
     */
    public void testRestoreState() {
        String pagePath = "/page.htm";
        MockContext context = MockContext.initContext(pagePath);

        // Setup Form and Fields
        Form form = new Form("form");
        Field nameField  = new TextField("name");
        form.add(nameField);

        Map pageStateMap = new HashMap();

        // Page state is stored in session under the Page path
        context.setSessionAttribute(pagePath, pageStateMap);

        Map formStateMap = new HashMap();
        formStateMap.put("name", "Steve");

        // Controls are stored in the Page Map under their names
        pageStateMap.put(form.getName(), formStateMap);

        // Restore form state
        ClickUtils.restoreState(form, form.getName(), context);

        assertEquals("Steve", nameField.getValue());
    }

    /**
     * Test that ClickUtil#removeState removes the state from the session.
     *
     * CLK-715
     */
    public void testRemoveState() {
        String pagePath = "/page.htm";
        MockContext context = MockContext.initContext(pagePath);

        // Setup Form
        Form form = new Form("form");

        Map pageStateMap = new HashMap();

        // Page state is stored in session under the Page path
        context.setSessionAttribute(pagePath, pageStateMap);
        assertNotNull(context.getSessionAttribute(pagePath));

        Map formStateMap = new HashMap();

        // Controls are stored in the Page Map under their names
        pageStateMap.put(form.getName(), formStateMap);

        // Remove form state
        ClickUtils.removeState(form, form.getName(), context);

        // Test that form state has been removed from the session
        assertNull(pageStateMap.get(form.getName()));

        // Since formState was the only state in the page map, test that
        // pageMap is also cleared from the session
        assertNull(context.getSessionAttribute(pagePath));
    }
}
