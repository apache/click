/*
 * Copyright 2005-2006 Malcolm A. Edgar
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

import java.io.Serializable;
import java.util.List;

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a select Option element: &nbsp; &lt;option&gt;&lt;/option&gt;.
 * <p/>
 * The Option class uses an immutable design so Option instances can be
 * shared by multiple Pages in the multi-threaded Servlet environment.
 * This enables Option instances to be cached as static variables.
 *
 * <h3>Option Example</h3>
 *
 * The example below caches Option and OptionGroup instances in a
 * static List to provide a reusable InvestmentSelect control.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> InvestmentSelect <span class="kw">extends</span> Select {
 *
 *     <span class="kw">private static final</span> List INVESTMENT_OPTIONS = <span class="kw">new</span> ArrayList();
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
 *     <span class="kw">public</span> Form form = <span class="kw">new</span> Form();
 *
 *     <span class="kw">private</span> Select investmentsSelect = <span class="kw">new</span> InvestmentsSelect(<span class="st">"investments"</span>);;
 *
 *     <span class="kw">public</span> InvestmentsPage() {
 *         investmentsSelect.setMutliple(<span class="kw">true</span>);
 *         investmentsSelect(7);
 *         form.add(investmentsSelect);
 *
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"  OK  "</span>));
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
 * <input type='submit' value='  OK  '/>
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
public class Option implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The empty select empty option. */
    public static final Option EMPTY_OPTION = new Option("", "");

    // ----------------------------------------------------- Instance Variables

    /** The Options display label. */
    protected final String label;

    /** The Option value. */
    protected final String value;

    // ----------------------------------------------------------- Constructors

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

    // ------------------------------------------------------ Public Attributes

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

    // --------------------------------------------------------- Public Methods

    /**
     * Return a HTML rendered Option string.
     *
     * @param select the parent Select
     * @return rendered HTML Option string
     */
    public String renderHTML(Select select) {

        HtmlStringBuffer buffer = new HtmlStringBuffer(48);

        buffer.elementStart("option");

        if (select.isMultiple()) {

            if (!select.getSelectedValues().isEmpty()) {

                // Search through selection list for matching value
                List values = select.getSelectedValues();
                for (int i = 0, size = values.size(); i < size; i++) {
                    String value = values.get(i).toString();
                    if (getValue().equals(value)) {
                        buffer.appendAttribute("selected", "selected");
                        break;
                    }
                }

            }

        } else {
            if (getValue().equals(select.getValue())) {
                buffer.appendAttribute("selected", "selected");
            }
        }

        buffer.appendAttribute("value", getValue());
        buffer.closeTag();

        buffer.appendEscaped(getLabel());

        buffer.elementEnd("option");

        return buffer.toString();
    }
}
