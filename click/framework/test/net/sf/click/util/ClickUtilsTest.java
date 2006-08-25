package net.sf.click.util;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
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
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.IntegerField;
import net.sf.click.extras.control.LongField;

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

    public void testCopyFormToObject() {
        // set up the form
        Form form = new Form("sample");
        form.setContext(new MockContext());
        
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
        form.setContext(new MockContext());
        TextField codeField = new TextField("address.state.code");
        codeField.setValue("NSW");
        form.add(codeField);
        form.copyTo(user, true);
        assertEquals("NSW", user.getAddress().getState().getCode());
    }

    public void testCopyObjectToForm() {
        // set up the form
        Form form = new Form("sample");
        form.setContext(new MockContext());
        
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
        form.setContext(new MockContext());
        TextField codeField = new TextField("address.state.code");
        form.add(codeField);
        form.copyFrom(user, true);
        assertEquals("NSW", codeField.getValueObject());
    }
    
    public void testCopyToNullNestedObject() {
        final String lineOne = "55 Dunkley Avenue";
        final String code = "NSW";
        final boolean active = false;
        final Boolean registered = Boolean.TRUE;
        
        Form form = new Form();
        form.setContext(new MockContext());
        
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
        Context context = new MockContext(Locale.ENGLISH);
        
        TextField textField = new TextField("test");
        textField.setContext(context);
        
        Map map = ClickUtils.getParentMessages(textField);
        assertNotNull(map);
        assertTrue(map.isEmpty());
        assertTrue(map == Collections.EMPTY_MAP);
        
        Page page = new Page();
        page.setContext(context);
        page.addControl(textField);
        
        Map map2 = ClickUtils.getParentMessages(textField);
        assertNotNull(map2);
        assertEquals(1, map2.size());
        assertFalse(map2 == Collections.EMPTY_MAP);
        
        Page page2 = new Page();
        page2.setContext(context);
        
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
        Context context = new MockContext(Locale.ENGLISH);
        String value = ClickUtils.encodeUrl("1000", context);
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
    }
    
}
