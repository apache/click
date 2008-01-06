package net.sf.click.util;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.click.Context;
import net.sf.click.MockContext;
import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.TextField;

public class ClickUtilsTest extends TestCase {

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

    protected void setUp() {
    	MockContext.initContext(Locale.ENGLISH);
    }

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
        assertEquals(new Integer(idField.getValue()), sampleObject.getId());
        assertEquals(nameField.getValue(), sampleObject.getName());
        
        //NOTE the dateField was NOT copied to the sampleObject's Date property.
        //Use net.sf.click.extras.control.DateField in the extras project, to 
        //copy a Date property.
        assertEquals(null, sampleObject.getDateOfBirth());
        assertEquals(telephoneField.getValueObject().toString(), sampleObject.getTelephone());
        assertTrue(sampleObject.getInt() == new Integer(intField.getValue()).intValue());
        assertTrue(sampleObject.getDouble() == new Double(doubleField.getValue()).doubleValue());
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
        assertEquals(sampleObject.getId(), new Integer(idField.getValue()));
        assertEquals(sampleObject.getName(), nameField.getValue());
        assertEquals(sampleObject.getDateOfBirth().toString(), dateField.getValue());
        assertTrue(sampleObject.getInt() == new Integer(intField.getValue()).intValue());
        assertTrue(sampleObject.getDouble() == new Double(doubleField.getValue()).doubleValue());
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
        
        Map map = new HashMap();
        map.put("name", "malcolm");
        form = new Form();
        TextField nameField2 = new TextField("name");
        form.add(nameField2);
        form.copyFrom(map, true);
        assertEquals("malcolm", nameField2.getValue());
    }

    /**
     * CLK-278.
     * 
     * Test that the map are populated from field values.
     */
    public void testCopyMapToForm() {

        Integer id = new Integer(9876);
        String name = "Rocky Balboa";
        Integer age = new Integer(61);
        String stateCode = "NSW";
        String street = "12 Short street";
        
        // Setup the map
        Map map = new HashMap();
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
     * CLK-239.
     * 
     * Test that field value are copied to map.
     */
    public void testCopyFormToMap() {
        
        Integer id = new Integer(9876);
        String name = "Rocky Balboa";
        Integer age = new Integer(61);
        String stateCode = "NSW";
        String street = "12 Short street";

        // Setup the map with no values
        Map map = new HashMap();
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

    public void testToLabel() {
        assertEquals("Customer", ClickUtils.toLabel("customer"));
        assertEquals("Customer Number", ClickUtils.toLabel("customerNumber"));
        assertEquals("Card PIN", ClickUtils.toLabel("cardPIN"));
    }
    
    public void testToMD5Hash() {
        assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", 
                     ClickUtils.toMD5Hash("password"));
    }
    
    public void testGetParentMessages() {
        TextField textField = new TextField("test");
        
        Map map = ClickUtils.getParentMessages(textField);
        assertNotNull(map);
        assertTrue(map.isEmpty());
        assertTrue(map == Collections.EMPTY_MAP);
        
        Page page = new Page();
        page.addControl(textField);
        
        Map map2 = ClickUtils.getParentMessages(textField);
        assertNotNull(map2);
        assertEquals(1, map2.size());
        assertFalse(map2 == Collections.EMPTY_MAP);
        
        Page page2 = new Page();
        
        Form form = new Form("form");
        page2.addControl(form);
        
        TextField textField2 = new TextField("test");
        form.add(textField2);

        Map map3 = ClickUtils.getParentMessages(textField2);
        assertNotNull(map3);
        assertEquals(1, map3.size());
        assertFalse(map3 == Collections.EMPTY_MAP);        
    }

    public void testEncodeUrl() {
        String value = ClickUtils.encodeUrl("1000", Context.getThreadLocalContext());
        assertEquals("1000", value);
    }

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
        assertEquals("&gt;", ClickUtils.escapeHtml(">"));
        assertEquals(" ", ClickUtils.escapeHtml(" "));
        assertEquals("&agrave;", ClickUtils.escapeHtml("à"));
        
        assertFalse(ClickUtils.requiresEscape((char) 0));
        assertTrue(ClickUtils.requiresEscape((char) 34));
        assertTrue(ClickUtils.requiresEscape((char) 184));
        assertFalse(ClickUtils.requiresEscape((char) 999999));
    }

    public void testGetMimeType() {
        assertEquals("application/vnd.ms-excel", ClickUtils.getMimeType("worksheet.xls"));

        assertEquals("application/vnd.ms-excel", ClickUtils.getMimeType("WORKSHEET.XLS"));
        
        try {
        	assertNull(ClickUtils.getMimeType("broken.xxx"));
        } catch (Exception e) {
        	assertTrue(false);
        }
    }
    
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
    
}
