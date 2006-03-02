package net.sf.click.extras.control;

import org.apache.commons.lang.StringUtils;

import net.sf.click.control.Button;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

public class ActionButton extends Button {

    private static final long serialVersionUID = 1L;

    /** The action button parameter name: &nbsp; <tt>actionButton</tt>. */
    public static final String ACTION_BUTTON = "actionButton";

    /** The value parameter name: &nbsp; <tt>value</tt>. */
    public static final String VALUE = "value";

    // ----------------------------------------------------- Instance Variables

    /** The button is clicked. */
    protected boolean clicked;

    // ----------------------------------------------------------- Constructors

    public ActionButton(String name) {
        super(name);
    }

    public ActionButton(String name, String label) {
        super(name, label);
    }

    /**
     * Create an ActionButton for the given name, listener object and listener
     * method.
     *
     * @param name the action button name
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public ActionButton(String name, Object listener, String method) {
        setName(name);
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    /**
     * Create an ActionButton for the given name, label, listener object and
     * listener method.
     *
     * @param name the action link name
     * @param label the action link label
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public ActionButton(String name, String label, Object listener,
            String method) {

        setName(name);
        setLabel(label);
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    public ActionButton() {
        super();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Returns true if the ActionButton was clicked, otherwise returns false.
     *
     * @return true if the ActionButton was clicked, otherwise returns false.
     */
    public boolean isClicked() {
        return clicked;
    }

    /**
     * Return the ActionButton onclick attribute for the given value.
     * This method will encode the URL with the session ID if required using
     * <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @param value the ActionButton value parameter
     * @return the ActionButton JavaScript href attribute
     */
    public String getOnClick(Object value) {
        String uri = getContext().getRequest().getRequestURI();

        StringBuffer url =
            new StringBuffer(uri.length() + getName().length() + 40);

        url.append(uri);
        url.append("?");
        url.append(ACTION_BUTTON);
        url.append("=");
        url.append(getName());
        if (value != null) {
            url.append("&amp;");
            url.append(VALUE);
            url.append("=");
            url.append(value);
        }

        return "javascript:document.location.href='"
               + getContext().getResponse().encodeURL(url.toString())
               + "'";
    }

    /**
     * Return the ActionButton anchor &lt;a&gt; tag href attribute value.
     *
     * @return the ActionButton anchor &lt;a&gt; tag HTML href attribute value
     */
    public String getOnClick() {
        return getOnClick(getValue());
    }

    /**
     * Returns the action link <tt>Double</tt> value if the action link was
     * processed and has a value, or null otherwise.
     *
     * @return the action link <tt>Double</tt> value if the action link was processed
     */
    public Double getValueDouble() {
        if (getValue() != null) {
            return Double.valueOf(getValue());
        } else {
            return null;
        }
    }

    /**
     * Returns the ActionButton <tt>Integer</tt> value if the ActionButton was
     * processed and has a value, or null otherwise.
     *
     * @return the ActionButton <tt>Integer</tt> value if the action link was processed
     */
    public Integer getValueInteger() {
        if (getValue() != null) {
            return Integer.valueOf(getValue());
        } else {
            return null;
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method will set the {@link #isClicked()} property to true if the
     * ActionButton was clicked, and if an action callback listener was set
     * this will be invoked.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        clicked =
            getName().equals(getContext().getRequestParameter(ACTION_BUTTON));

        if (clicked) {
            setValue(getContext().getRequestParameter(VALUE));

            if (listener != null && listenerMethod != null) {
                return ClickUtils.invokeListener(listener, listenerMethod);

            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    /**
     * Return a HTML rendered Button string. Note the button label is rendered
     * as the HTML "value" attribute.
     *
     * @see Object#toString()
     *
     * @return a HTML rendered Button string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(40);

        buffer.elementStart("input");

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("value", getLabel());
        buffer.appendAttribute("title", getTitle());

        String onClickAction = " \"onclick=\"" + getOnClick() + "\"";
        buffer.append(onClickAction);

        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }

        buffer.elementEnd();

        return buffer.toString();
    }
}
