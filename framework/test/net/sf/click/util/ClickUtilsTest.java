package net.sf.click.util;

import junit.framework.TestCase;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.TextField;

public class ClickUtilsTest extends TestCase {
    
  public void testCopyToWithFieldSet() {
    String value = "hello";

    // set up the form
    TextField name = new TextField("name");
    name.setValue(value);
    
    FieldSet fieldset = new FieldSet("fieldset");
    fieldset.add(name);
    
    Form form = new Form("sample");
    form.add(fieldset);
    form.setAttribute("name", value);

    // copy form to object
    SampleObject sample = new SampleObject();
    ClickUtils.copyFormToObject(form, sample, true);

    // has the object been configured correctly?
    assertEquals(value, sample.getName());
  }
  
  public static class SampleObject {
      private String name;
      public String getName() {
          return name;
      }
      public void setName(String name) {
          this.name = name;
      }
  }
  
}
