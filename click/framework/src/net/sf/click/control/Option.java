/*
 * Copyright 2005 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.control;

import java.util.List;

//---------------------------------------------------------- Inner Classes

/**
 * Provides a select Option element: &nbsp; &lt;option&gt;&lt;/option&gt;.
 * <p/>
 * The Option class uses an immutable design so Option instances can be
 * shared by multiple Pages in the multi-threaded Servlet environment.
 * This enables Option instances to be cached as static variables.
 * <p/>
 * The example below caches Option and OptionGroup instances in a
 * static List to provide a reusable InvestmentSelect control.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> InvestmentSelect <span class="kw">extends</span> Select {
 *
 *     <span class="kw">static final</span> List INVESTMENT_OPTIONS = <span class="kw">new</span> ArrayList();
 *
 *     <span class="kw">static</span> {
 *         OptionGroup property = <span class="kw">new</span> OptionGroup(<span class="st">"Property"</span>);
 *         property.add(<span class="kw">new</span> Option(<span class="st">"Commerical Property"</span>, <span class="st">"Commercial"</span>));
 *         property.add(<span class="kw">new</span> Option(<span class="st">"Residential Property"</span>, <span class="st">"Residential"</span>));
 *         INVESTMENT_OPTIONS.add(property);
 *
 *         OptionGroup securities = <span class="kw">new</span> OptionGroup(<span class="st">"Securities"</span>);
 *         securities.add(<span class="kw">new</span> Option(<span class="st">"Bonds"</span>));
 *         securities.add(<span class="kw">new</span> Option(<span class="st">"Options"</span>));
 *         securities.add(<span class="kw">new</span> Option(<span class="st">"Stocks"</span>));
 *         INVESTMENT_OPTIONS.add(securities);
 *     }
 *
 *     <span class="kw">public</span> InvestmentSelect(String label) {
 *         <span class="kw">super</span>(label);
 *         setOptionList(INVESTMENT_OPTIONS);
 *     }
 * }
 *
 * <span class="kw">public class</span> InvestmentsPage <span class="kw">extends</span> Page {
 *
 *     Form form;
 *     Select investmentsSelect;
 *
 *     <span class="kw">public void</span> onInit() {
 *         form = new Form(<span class="st">"form"</span>, getContext());
 *         addControl(form);
 *
 *         investmentsSelect = <span class="kw">new</span> InvestmentsSelect(<span class="st">"Investments"</span>);
 *         investmentsSelect.setMutliple(<span class="kw">true</span>);
 *         investmentsSelect(7);
 *         form.add(investmentsSelect);
 *     }
 *
 *     ..
 * } </pre>
 *
 * Rendered HTML:
 * <table class="htmlExample"><tr><td>
 * <table class='form'><tr>
 * <td align='left'><label >Investments</label></td>
 * <td align='left'><select name='investments' size='7' multiple><optgroup label='Property'><option value='Commerical Property'>Commercial</option><option value='Residential Property'>Residential</option></optgroup><optgroup label='Securities'><option value='Bonds'>Bonds</option><option selected value='Options'>Options</option><option value='Stocks'>Stocks</option></optgroup></select></td>
 * </tr>
 * <tr><td colspan='2'>&nbsp;</td></tr>
 * <tr align='left'><td colspan='2'>
 * <input type='submit' value='Submit'/>
 * </td></tr>
 * </table>
 * </td></tr></table>
 *
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.6">OPTION</a>
 *
 * @see Select
 * @see OptionGroup
 *
 * @author Malcolm Edgar
 */
public class Option {

    // ------------------------------------------------- Instance Variables

    /** The Options display label */
    protected final String label;

    /** The Option value. */
    protected final String value;

    // ------------------------------------------------------- Constructors

    /**
     * Create an Option with the given value and display label.
     *
     * @param value the Option value
     * @param label the Option display label
     */
    public Option(Object value, String label) {
        this.value = value.toString();
        this.label = label;
    }

    /**
     * Create an Option with the given value. The value will also be used
     * for the display label.
     *
     * @param value the Option value and display label
     */
    public Option(String value) {
        this(value, value);
    }

    // -------------------------------------------------- Public Attributes

    /**
     * Return the Option display label.
     *
     * @return the Option display label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Return the Option value.
     *
     * @return the Option value
     */
    public String getValue() {
        return value;
    }

    // ----------------------------------------------------- Public Methods

    /**
     * Return a HTML rendered Option string.
     *
     * @param select the parent Select
     * @return rendered HTML Option string
     */
    public String renderHTML(Select select) {
        StringBuffer buffer = new StringBuffer(48);

        if (select.isMultiple()) {

            if (!select.getMultipleValues().isEmpty()) {

                // Search through selection list for matching value
                List values = select.getMultipleValues();
                boolean found = false;
                for (int i = 0, size = values.size(); i < size; i++) {
                    String value = values.get(i).toString();
                    if (getValue().equals(value)) {
                        buffer.append("<option selected value='");
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    buffer.append("<option value='");
                }

            } else {
                buffer.append("<option value='");
            }

        } else {
            if (getValue().equals(select.getValue())) {
                buffer.append("<option selected value='");
            } else {
                buffer.append("<option value='");
            }
        }

        buffer.append(getValue());
        buffer.append("'>");
        buffer.append(getLabel());
        buffer.append("</option>");

        return buffer.toString();
    }
}
