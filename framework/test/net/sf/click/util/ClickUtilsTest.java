package net.sf.click.util;

import junit.framework.TestCase;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.IntegerField;
import net.sf.click.control.TextField;

public class ClickUtilsTest extends TestCase {
    
  public void testCopyFormToObject() {
      
    final String name = "hello";
    final Integer id = new Integer(1234); 

    // set up the form
    Form form = new Form("sample");
    
    IntegerField idField = new IntegerField("id");
    idField.setValue(id);
    form.add(idField);
    
    FieldSet fieldset = new FieldSet("fieldset");
    form.add(fieldset);

    TextField nameField = new TextField("name");
    nameField.setValue(name);
    fieldset.add(nameField);
    
    // copy form to object
    SampleObject sampleObject = new SampleObject();
    ClickUtils.copyFormToObject(form, sampleObject, true);

    // has the object been configured correctly?
    assertEquals(idField.getInteger(), sampleObject.getId());
    assertEquals(nameField.getValue(), sampleObject.getName());
  }
  
  public void testCopyObjectToForm() {
      
      final String name = "hello";
      final Integer id = new Integer(1234); 
      
      SampleObject sampleObject = new SampleObject();
      sampleObject.setId(id);
      sampleObject.setName(name);
      
      
      // set up the form
      Form form = new Form("sample");
      
      IntegerField idField = new IntegerField("id");
      form.add(idField);
      
      FieldSet fieldset = new FieldSet("fieldset");
      form.add(fieldset);
      
      TextField nameField = new TextField("name");
      fieldset.add(nameField);
      
      // copy object to form
      ClickUtils.copyObjectToForm(sampleObject, form, true);
      
      // has the object been configured correctly?
      assertEquals(sampleObject.getId(), idField.getInteger());
      assertEquals(sampleObject.getName(), nameField.getValue());
  }
  
  public static class SampleObject {
      private Integer id;
      private String name;
      
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
  }
  
}
