package net.sf.click.util;

import junit.framework.TestCase;

public class HtmlStringBufferTest extends TestCase {

    public void test() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        
        buffer.elementStart("input");
        buffer.appendAttribute("type", "text");
        buffer.appendAttribute("value", "bl'ah\"s");
        buffer.elementEnd();      
        assertEquals("<input type=\"text\" value=\"bl'ah&quot;s\"/>", buffer.toString());
        
        buffer = new HtmlStringBuffer();
        buffer.appendAttribute("onclick", "if (i < 3) alert('too small');");
        String value = " onclick=\"if (i < 3) alert('too small');\"";
        assertEquals(value, buffer.toString());

        buffer = new HtmlStringBuffer();
        buffer.appendAttribute("onClick", "if (i < 3) alert('too small');");
        value = " onClick=\"if (i < 3) alert('too small');\"";
        assertEquals(value, buffer.toString());
        
        buffer = new HtmlStringBuffer();
        buffer.appendAttribute("test", "if (i < 3) alert('too small');");
        value = " test=\"if (i &lt; 3) alert('too small');\"";
        assertEquals(value, buffer.toString());
        
        buffer = new HtmlStringBuffer();
        
        buffer.elementStart("textarea");
        buffer.appendAttribute("id", "textarea-id");
        buffer.appendAttribute("rows", 2);
        buffer.appendAttribute("cols", 12);
        buffer.appendAttribute("class", "field-input");
        buffer.closeTag();
        buffer.appendEscaped("This is the car's way home today");
        buffer.elementEnd("textarea");
        value = "<textarea id=\"textarea-id\" rows=\"2\" cols=\"12\" class=\"field-input\">This is the car's way home today</textarea>";
        assertEquals(value, buffer.toString());
        
        buffer = new HtmlStringBuffer();
        buffer.append("<");
        assertEquals("<", buffer.toString());
        
        buffer = new HtmlStringBuffer();
        buffer.appendAttributeReadonly();
        assertEquals(" readonly=\"readonly\"", buffer.toString());
        
        buffer = new HtmlStringBuffer();
        buffer.appendAttributeDisabled();
        assertEquals(" disabled=\"disabled\"", buffer.toString());
    }

}
