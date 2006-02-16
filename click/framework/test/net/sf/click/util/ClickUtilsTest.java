package net.sf.click.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import net.sf.click.control.Checkbox;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
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
        assertTrue(sampleObject.getBoolean() == checkBox.isChecked());
        
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
        assertTrue(sampleObject.getBoolean() == checkBox.isChecked());
        
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
    }

    public void testToLabel() {
        assertEquals("Customer", ClickUtils.toLabel("customer"));
        assertEquals("Customer Number", ClickUtils.toLabel("customerNumber"));
        assertEquals("Card PIN", ClickUtils.toLabel("cardPIN"));
    }

    public static class SampleObject {
        private Integer id;
        private String name;
        private java.util.Date dateOfBirth;
        private boolean _boolean;
        private int _int;
        private double _double;
        private String telephone;

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
        
        public boolean getBoolean() {
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
    }

    public static class User {
        private Integer id;
        private String name;
        private Address address;
        private String telephone;
        
        public Address getAddress() {
            return address;
        }
        public void setAddress(Address address) {
            this.address = address;
        }
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
        public String getTelephone() {
            return telephone;
        }
        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }
    }

    public static class Address {
        private Integer id;
        private String lineOne;
        private String lineTwo;
        private String lineThree;
        private State state;
        
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
    
    public static class State {
        private String code;

        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        } 
    }

}
