package net.sf.click.sandbox.chrisichris.prototype;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.TextField;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A TextField combined with a scriptacoulus autocompleter. While the user
 * enters a value into the TextField an AjaxRequest is done to provide an
 * unordered list for proposed values.
 * <p>
 * Typically the request is send to an AjaxAction which provides the ul. The
 * AjaxAction is provided in the constructor or through
 * {@link #setAjaxAction(AjaxAction)}. There is the default AjaxAction
 * AutocompleteTextField.Action which does this.
 * </p>
 * <p>
 * This class is basicly a generater for script.aculo.us to see a full
 * description with examples see <a
 * href="http://wiki.script.aculo.us/scriptaculous/show/Autocompletion>
 * autocompletion for scriptacoulous</a>
 * </p>
 *
 * @author Christian Essl
 *
 */
public class AutoCompleteTextField extends TextField {

    /**
     * The request-parameter name under which the user entered
     * text of the TextField is send.
     */
    public static final String REQUEST_PARAM = "click_u_sco_auto_value";

    /**
     * The default class of the div which will show the options.
     */
    public static final String DROP_DOWN_DIV_CLASS = "autocomplete";

    /**
     * The default html-id start of the div which will show the options.
     */
    public static final String DROP_DOWN_DIV_ID = "autocomp_dropdown";


    /**
     * tokens which trigger the autocomplete.
     */
    protected String[] tokens;

    /**
     * frequency of the autocomplete.
     */
    protected float frequency = 0.4f;

    /**
     * number of chars to trigger autocomplete.
     */
    protected int minChars = 1;

    /**
     * JS to execute after the options where executed.
     */
    protected String afterUpdateElement;

    /**
     * AjaxAction to send the ajax request to.
     */
    protected AjaxAction ajaxAction;

    /**
     *
     */
    public AutoCompleteTextField() {
        super();
    }

    /**
     * @param name
     * @param required
     * @param ac
     *            the action which renders the ul response
     */
    public AutoCompleteTextField(String name, boolean required, AjaxAction ac) {
        super(name, required);
        this.ajaxAction = ac;
    }

    /**
     * @param name
     * @param label
     * @param required
     * @param ac
     *            the action which renders the ul response
     */
    public AutoCompleteTextField(String name, String label, boolean required,
            AjaxAction ac) {
        super(name, label, required);
        this.ajaxAction = ac;
    }

    /**
     * @param name
     * @param label
     * @param size
     * @param ac
     *            the action which renders the ul response
     */
    public AutoCompleteTextField(String name, String label, int size,
            AjaxAction ac) {
        super(name, label, size);
        this.ajaxAction = ac;
    }

    /**
     * @param name
     * @param label
     */
    public AutoCompleteTextField(String name, String label) {
        super(name, label);
    }

    /**
     * @param name
     * @param label
     * @param ac
     *            the action which renders the ul response
     */
    public AutoCompleteTextField(String name, String label, AjaxAction ac) {
        super(name, label);
        this.ajaxAction = ac;
    }

    /**
     * @param name
     */
    public AutoCompleteTextField(String name) {
        super(name);
    }

    /**
     * @param name
     * @param ac
     *            the action which renders the ul response
     */
    public AutoCompleteTextField(String name, AjaxAction ac) {
        super(name);
        this.ajaxAction = ac;
    }

    // ///////////////////////7
    // code
    /**
     * Set the AjaxAction which should be called to get the autocomplete
     * propsals.
     *
     * @param ajaxAction
     *            The ajaxAction to set.
     */
    public void setAjaxAction(AjaxAction ajaxAction) {
        this.ajaxAction = ajaxAction;
    }

    /**
     * The AjaxAction which should be called to get the auto-complete propsals.
     *
     * @return Returns the ajaxAction.
     */
    public AjaxAction getAjaxAction() {
        return ajaxAction;
    }

    /**
     * Includes prototpye and scriptacoulus JS and scriptacoluus css.
     * @return import string
     * @see net.sf.click.control.Field#getHtmlImports()
     */
    public String getHtmlImports() {

        String ret = DeployControl.getPrototypeImport(getContext());
        ret = ret + DeployControl.getScriptaculousImport(getContext());

        String si = super.getHtmlImports();
        if (si != null) {
            ret = ret + si;
        }
        return ret;
    }

    // //////////////////////////////
    // //////////////////////////////
    /**
     * The JS to execute after the update of the element.
     *
     * @return JS
     */
    public String getAfterUpdateElement() {
        return afterUpdateElement;
    }

    /**
     * The JS to execute after the update of the element.
     *
     * @param afterUpdateElement JS
     */
    public void setAfterUpdateElement(String afterUpdateElement) {
        this.afterUpdateElement = afterUpdateElement;
    }

    /**
     * The frequency to lookup changes in the TextField.
     *
     * @return frequency
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * The frequency to lookup changes in the TextField. Default is 0.4f.
     *
     * @param frequency
     *            the frequency
     */
    public void setFrequency(float frequency) {
        if (frequency < 0f) {
            frequency = 0.4f;
        }
        this.frequency = frequency;
    }

    /**
     * The number of chars entered to start autocompletition.
     *
     * @return number of chars
     */
    public int getMinChars() {
        return minChars;
    }

    /**
     * The number of chars entered to start autocompletition. Default is 1
     *
     * @param minChars
     *            number of chars
     */
    public void setMinChars(int minChars) {
        this.minChars = Math.max(0, minChars);
    }

    /**
     * Tokens for tokenized autocompletion.
     *
     * @see #setTokens(String[])
     * @return tokens set default is null
     */
    public String[] getTokens() {
        return tokens;
    }

    /**
     * Set to enable tokenized autocompletition. Each time a token is entered in
     * the autocompletiton will be triggered to fullfill the token. Default is
     * null (not enabled).
     *
     * @param tokens
     *            the tokens or null
     */
    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }

    /**
     * The html-id of the div which shows the autocompletion selection.
     *
     * @return id
     */
    public String getDropDownDivId() {
        return this.getId() + "-" + DROP_DOWN_DIV_ID;
    }

    /**
     * The html-class of the div which shows the autocompletion options.
     * By default is {@link #DROP_DOWN_DIV_CLASS}
     *
     * @return html class
     */
    public String getDropDownDivClass() {
        return DROP_DOWN_DIV_CLASS;
    }

    /**
     * Overrides to add the autocomplete functionality.
     * @return the JS for autocomplete a input text.
     * @see net.sf.click.control.TextField#toString()
     */
    public String toString() {
        StringBuffer stB = new StringBuffer();
        stB.append(super.toString());

        stB.append(getDropdownDiv());
        stB.append(getAutocompleteJS());
        String ret = stB.toString();
        return ret;
    }

    /**
     * The javascript tag &lt;script&gt; which register an Ajax.Autocompleter
     * for this TextField.
     *
     * @return the JS tag
     */
    public String getAutocompleteJS() {

        if (ajaxAction == null) {
            throw new IllegalStateException("No actionUrl provided for "
                    + " AutoCompleteTextField [" + this.getName() + "]");
        }
        String actionUrl = ajaxAction.getUrl(this.getContext());

        String ret = "new Ajax.Autocompleter(\"" + this.getId() + "\", \""
                + getDropDownDivId() + "\", \"" + actionUrl
                + "\", {paramName : '" + REQUEST_PARAM + "'";
        if (tokens != null && tokens.length > 0) {
            ret += ", tokens : [";
            for (int i = 0; i < tokens.length; i++) {
                String str = StringEscapeUtils.escapeJavaScript(tokens[i]);
                if (i > 0) {
                    ret += ", ";
                }
                ret += "'" + str + "'";
            }
            ret += "]";
        }

        ret += ", frequency : " + getFrequency();

        ret += ", minChars : " + getMinChars();

        if (!StringUtils.isBlank(afterUpdateElement)) {
            ret += ", afterUpdateElement : " + afterUpdateElement;
        }
        ret += "})";

        return ret = Prototype.INSTANCE.javascriptTag(ret);
    }

    /**
     * The div tag which represents the proposals for this autocomplete.
     *
     * @return html div tag.
     */
    public String getDropdownDiv() {
        String ret = "<div id=\"" + getDropDownDivId() + "\" class=\""
                + getDropDownDivClass() + "\"></div>";
        return ret;
    }

    /**
     * A convinience AjaxAction which renders the response for an
     * AutoCompleteTextField. NOTE: The action is executed concurrently. A
     * scriptacoulous autocompleter expects as a response an unordered list of
     * the form:
     *
     * <pre>
     *
     *  &lt;ul&gt;
     *    &lt;li&gt;
     *      Proposed Value &lt;span class=&quot;informal&quot;&gt; description &lt;/span&gt;
     *    &lt;/li&gt;
     *    &lt;li&gt;
     *      Proposed Value &lt;span class=&quot;informal&quot;&gt; description &lt;/span&gt;
     *    &lt;/li&gt;
     *    ...
     *  &lt;/ul&gt;
     *
     * </pre>
     *
     * Override {@link #getData(String)} to provide the data to be returned in
     * the list. Override {@link #getInputString(Object)} to customize the
     * proposed string for each value in the List returned from
     * {@link #getData(String)}. Override {@link #getDescriptionString(Object)}
     * to provide the (optional) additional description text for each value in
     * the list.
     *
     * @author Christian Essl
     *
     */
    public static class Action extends AjaxAction {

        private final static List EMTPY_DATA = Arrays
                .asList(new String[] { "Please provide data in "
                        + "AutoComplete.Action.getData()" });

        /**
         * Calls {@link #makeListString(String)}
         *
         * @see net.sf.click.sandbox.chrisichris.prototype.AjaxAction#doExecute(net.sf.click.Page)
         */
        protected void doExecute(Page page) {
            String paramIn = page.getContext().getRequestParameter(
                    REQUEST_PARAM);
            if (paramIn == null) {
                throw new NullPointerException("No inparameter");
            }
            String listStr = makeListString(paramIn);
            AjaxAction.writeDirectlyToResponse(page, listStr, "text/xml");
        }

        /**
         * Renders the unordered list for the given . To do this calls out to
         * {@link #getData(String)} and with each item in the returned list to
         * {@link #getInputString(Object)} and to
         * {@link #getDescriptionString(Object)}.
         *
         * @param paramIn
         *            the value the user has up to now entered in the
         *            text-field.
         * @return xhtml unordered list
         */
        protected String makeListString(String paramIn) {
            List data = getData(paramIn);
            HtmlStringBuffer stb = new HtmlStringBuffer(data.size() * 40);
            stb.append("<ul>");
            for (Iterator it = data.iterator(); it.hasNext();) {
                Object element = (Object) it.next();
                stb.append("<li>");
                stb.append(getInputString(element));
                String desc = getDescriptionString(element);
                if (desc != null) {
                    stb.append("<span class=\"informal\">");
                    stb.append(desc);
                    stb.append("</span>");
                }

                stb.append("</li>");
            }
            stb.append("</ul>");
            return stb.toString();

        }

        /**
         * A list of data objects for the given param. Each item of the returned
         * list is given to {@link #getInputString(Object)} and
         * {@link #getDescriptionString(Object)} to render the proposed value.
         * By default returns an empty list.
         *
         * @param param
         *            the value currently typed into the AutoCompleteTextField
         * @return list of data objects which match the param
         */
        protected List getData(String param) {
            return EMTPY_DATA;
        }

        /**
         * Renders the value which is proposed to the user to enter in the
         * autocomplete TextField. By default return value.toString()
         *
         * @param value
         *            item from the list returned from {@link #getData(String)}
         * @return
         */
        protected String getInputString(Object value) {
            return value.toString();
        }

        /**
         * Renders the description which is rendered beside the inputString.
         *
         * @param value
         *            item form the list returned form {@link #getData(String)
         * @return description String or null if not description needed
         */
        protected String getDescriptionString(Object value) {
            return null;
        }
    }

}
