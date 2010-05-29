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

import junit.framework.TestCase;

/**
 * HtmlStringBuffer test.
 */
public class HtmlStringBufferTest extends TestCase {

    /**
     * Sanity checks for HtmlStringBuffer.
     */
    public void test() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        
        buffer.elementStart("input");
        buffer.appendAttribute("type", "text");
        buffer.appendAttributeEscaped("value", "bl'ah\"s");
        buffer.elementEnd();
        assertEquals("<input type=\"text\" value=\"bl&#039;ah&quot;s\"/>", buffer.toString());
        
        buffer = new HtmlStringBuffer();
        buffer.appendAttribute("onclick", "if (i < 3) alert('too small');");
        String value = " onclick=\"if (i < 3) alert('too small');\"";
        assertEquals(value, buffer.toString());

        buffer = new HtmlStringBuffer();
        buffer.appendAttribute("onClick", "if (i < 3) alert('too small');");
        value = " onClick=\"if (i < 3) alert('too small');\"";
        assertEquals(value, buffer.toString());
        
        buffer = new HtmlStringBuffer();
        buffer.appendAttributeEscaped("test", "if (i < 3) alert('too small');");
        value = " test=\"if (i &lt; 3) alert(&#039;too small&#039;);\"";
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
        value = "<textarea id=\"textarea-id\" rows=\"2\" cols=\"12\" class=\"field-input\">This is the car&#039;s way home today</textarea>";
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
