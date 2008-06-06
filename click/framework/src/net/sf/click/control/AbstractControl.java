/*
 * Copyright 2007-2008 Malcolm A. Edgar
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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.MessagesMap;

/**
 * Provides a default implementation of the {@link Control} interface,
 * to make it easier for developers to implement their own controls.
 * <p/>
 * Subclasses are expected to at least override {@link #getTag()}
 * to differentiate the control. However some controls does not map cleanly
 * to a html <em>tag</em>, in which case you can override
 * {@link #render(net.sf.click.util.HtmlStringBuffer)} for complete control
 * over the output.
 * <p/>
 * Below is an example of creating a new control called MyField:
 * <pre class="prettyprint">
 * public class MyField extends AbstractControl {
 *
 *     private String value;
 *
 *     public void setValue(String value) {
 *         this.value = value;
 *     }
 *
 *     public String getValue() {
 *         return value;
 *     }
 *
 *     public String getTag() {
 *         // Return the HTML tag
 *         return "input";
 *     }
 *
 *     public boolean onProcess() {
 *         // Bind the request parameter to the field value
 *         String requestValue = getContext().getRequestParamter(getName());
 *         setValue(requestValue);
 *
 *         // Invoke any listener of MyField
 *         return invokeListener();
 *     }
 * }
 * </pre>
 * By overriding {@link #getTag()} one can specify the html tag to render.
 * <p/>
 * Overriding {@link #onProcess()} allows one to bind the servlet request
 * parameter to MyField value. The {@link #invokeListener()} method is invoked
 * to trigger the action listener callback on MyField.
 * <p/>
 * To view the html rendered by MyField invoke the control's {@link #toString()}
 * method:
 *
 * <pre class="prettyprint">
 * public class Test {
 *     public static void main (String args[]) {
 *         // Create mock context in which to test the control.
 *         MockContext.initContext();
 *
 *         String fieldName = "myfield";
 *         MyField myfield = new MyField(fieldName);
 *         String output = myfield.toString();
 *         System.out.println(output);
 *     }
 * } </pre>
 *
 * Executing the above test results in the following output:
 *
 * <pre class="prettyprint">
 * &lt;input name="myfield" id="myfield"/&gt;</pre>
 *
 * Also see {@link net.sf.click.Control} javadoc for an explanation of the
 * Control execution sequence.
 *
 * @author Bob Schellink
 * @author Malcolm Edgar
 */
public abstract class AbstractControl implements Control {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The control's action listener. */
    protected ActionListener actionListener;

    /** The Field attributes Map. */
    protected Map attributes;

    /** The Field localized messages Map. */
    protected transient Map messages;

    /** The Field name. */
    protected String name;

    /** The control's parent. */
    protected Object parent;

    /**
     * The Map of CSS style attributes.
     *
     * @deprecated use {@link #addStyleClass(String)} and
     * {@link #removeStyleClass(String)} instead.
     */
    protected Map styles;

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    // ---------------------------------------------------- Public Constructors

    /**
     * Create a control with no name defined.
     */
    public AbstractControl() {
    }

    /**
     * Create a control with the given name.
     *
     * @param name the control name
     */
    public AbstractControl(String name) {
        if (name != null) {
            setName(name);
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Returns the controls html tag.
     * <p/>
     * Subclasses should override this method and return the correct tag.
     * <p/>
     * This method returns <tt>null</tt> by default.
     * <p/>
     * Example tags include <tt>table</tt>, <tt>form</tt>, <tt>a</tt> and
     * <tt>input</tt>.
     *
     * @return this controls html tag
     */
    public String getTag() {
        return null;
    }

    /**
     * Return the control's action listener.
     *
     * @return the control's action listener
     */
    public ActionListener getActionListener() {
        return actionListener;
    }

    /**
     * Set the control's action listener.
     *
     * @param listener the control's action listener
     */
    public void setActionListener(ActionListener listener) {
        actionListener = listener;
    }

    /**
     * Return the control HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of link HTML attribute
     * @return the link HTML attribute
     */
     public String getAttribute(String name) {
        if (hasAttributes()) {
            return (String) getAttributes().get(name);
        }
        return null;
    }

    /**
     * Set the control attribute with the given attribute name and value. You would
     * generally use attributes if you were creating the entire Control
     * programmatically and rendering it with the {@link #toString()} method.
     * <p/>
     * For example given the ActionLink:
     *
     * <pre class="codeJava">
     * ActionLink addLink = <span class="kw">new</span> ActionLink(<span class="red">"addLink"</span>, <span class="st">"Add"</span>);
     * addLink.setAttribute(<span class="st">"target"</span>, <span class="st">"_blank"</span>); </pre>
     *
     * Will render the HTML as:
     * <pre class="codeHtml">
     * &lt;a href=".." <span class="st">target</span>=<span class="st">"_blank"</span>&gt;<span class="st">Add</span>&lt;/a&gt; </pre>
     *
     * <b>Note:</b> for <tt>style</tt> and <tt>class</tt> attributes you can
     * also use the methods {@link #setStyle(String, String)} and
     * {@link #addStyleClass(String)}.
     *
     * @see #setStyle(String, String)
     * @see #addStyleClass(String)
     * @see #removeStyleClass(String)
     *
     * @param name the attribute name
     * @param value the attribute value
     * @throws IllegalArgumentException if name parameter is null
     */
    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (value != null) {
            getAttributes().put(name, value);
        } else {
            getAttributes().remove(name);
        }
    }

    /**
     * Return the control's attributes Map.
     *
     * @return the control's attributes Map.
     */
    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap();
        }
        return attributes;
    }

    /**
     * Return true if the control has attributes or false otherwise.
     *
     * @return true if the control has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null) {
            return !attributes.isEmpty();
        } else {
            return false;
        }
    }

    /**
     * Returns true if specified attribute is defined, false otherwise.
     *
     * @param name the specified attribute to check
     * @return true if name is a defined attribute
     */
    public boolean hasAttribute(String name) {
        return hasAttributes() && getAttributes().containsKey(name);
    }

    /**
     * @see net.sf.click.Control#getContext()
     *
     * @return the Page request Context
     */
    public Context getContext() {
        return Context.getThreadLocalContext();
    }

    /**
     * @see Control#getName()
     *
     * @return the name of the control
     */
    public String getName() {
        return name;
    }

    /**
     * @see Control#setName(String)
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        //If control has a parent control, it cannot change its name.
        //To change a fields name, it must be removed from its parent first,
        //and reattached. The reason is that controls added to parents
        //are added to a hashMap keyed on their name. So changing the field's
        //name while its inside the map, will make it irretrievable.
        if (getParent() == null) {
            this.name = name;
        } else {
            throw new IllegalStateException("You cannot change the name of "
                + "a control that is already attached to its parent control. "
                + "To change the name of an attached control, first remove "
                + "the control from its parent, change its name, then re-add it "
                + "again.");
        }
    }

    /**
     * Return the "id" attribute value if defined, or the control name otherwise.
     *
     * @see net.sf.click.Control#getId()
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");
        } else {
            return getName();
        }
    }

    /**
     * Set the HTML id attribute for the control with the given value.
     *
     * @param id the element HTML id attribute value to set
     */
    public void setId(String id) {
        if (id != null) {
            setAttribute("id", id);
        } else {
            getAttributes().remove("id");
        }
    }

    /**
     * Return the localized message for the given key, or null if not found.
     * <p/>
     * This method will attempt to lookup the localized message in the
     * parent's messages, which by resolves to the Page's resource bundle.
     * <p/>
     * If the message was not found, the this method will attempt to look up the
     * value in the <tt>/click-control.properties</tt> message properties file.
     * <p/>
     * If still not found, this method will return null.
     *
     * @param name the name of the message resource
     * @return the named localized message, or null if not found
     */
    public String getMessage(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        String message = null;

        message = ClickUtils.getParentMessage(this, name);

        if (message == null && getMessages().containsKey(name)) {
            message = (String) getMessages().get(name);
        }

        return message;
    }

    /**
     * Return the formatted message for the given resource name
     * and message format argument and for the context request locale.
     *
     * @param name resource name of the message
     * @param arg the message argument to format
     * @return the named localized message for the field
     */
    public String getMessage(String name, Object arg) {
        Object[] args = new Object[] { arg };
        return getMessage(name, args);
    }

    /**
     * Return the formatted package message for the given resource name and
     * message format arguments and for the context request locale.
     *
     * @param name resource name of the message
     * @param args the message arguments to format
     * @return the named localized message for the package
     */
    public String getMessage(String name, Object[] args) {
        if (args == null) {
            throw new IllegalArgumentException("Null args parameter");
        }
        String value = getMessage(name);

        return MessageFormat.format(value, args);
    }

    /**
     * Return a Map of localized messages for the control.
     *
     * @return a Map of localized messages for the control
     * @throws IllegalStateException if the context for the control has not be set
     */
    public Map getMessages() {
        if (messages == null) {
            if (getContext() != null) {
                messages = new MessagesMap(getClass(), CONTROL_MESSAGES);

            } else {
                String msg = "Cannot initialize messages as context not set "
                    + "for link: " + getName();
                throw new IllegalStateException(msg);
            }
        }
        return messages;
    }

    /**
     * @see net.sf.click.Control#getParent()
     *
     * @return the Control's parent
     */
    public Object getParent() {
        return parent;
    }

    /**
     * @see net.sf.click.Control#setParent(Object)
     *
     * @param parent the parent of the Control
     */
    public void setParent(Object parent) {
        this.parent = parent;
    }

   /**
    * @see net.sf.click.Control#onProcess()
    *
    * @return true to continue Page event processing or false otherwise
    */
    public boolean onProcess() {
        return invokeListener();
    }

    /**
     * Set the controls event listener.
     * <p/>
     * The method signature of the listener is:<ul>
     * <li>must hava a valid Java method name</li>
     * <li>takes no arguments</li>
     * <li>returns a boolean value</li>
     * </ul>
     * <p/>
     * An example event listener method would be:
     *
     * <pre class="codeJava">
     * <span class="kw">public boolean</span> onClick() {
     *     System.out.println(<span class="st">"onClick called"</span>);
     *     <span class="kw">return true</span>;
     * } </pre>
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
        this.listener = listener;
        this.listenerMethod = method;
    }

   /**
    * @see net.sf.click.Control#onInit()
    */
    public void onInit() {
    }

    /**
     * @see net.sf.click.Control#onDestroy()
     */
    public void onDestroy() {
    }

    /**
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {

    }

   /**
    * @see net.sf.click.Control#onRender()
    */
    public void onRender() {
    }

    /**
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return the HTML includes statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        return null;
    }

    /**
     * Return the parent page of this control, or null if not defined.
     *
     * @return the parent page of this control, or null if not defined
     */
    public Page getPage() {
        return ClickUtils.getParentPage(this);
    }

    /**
     * Return the control CSS style for the given name.
     *
     * @param name the CSS style name
     * @return the CSS style for the given name
     */
    public String getStyle(String name) {
        if (hasAttribute("style")) {
            String currentStyles = getAttribute("style");
            Map stylesMap = parseStyles(currentStyles);
            return (String) stylesMap.get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the control CSS style name and value pair.
     * <p/>
     * For example given the ActionLink:
     *
     * <pre class="codeJava">
     * ActionLink addLink = <span class="kw">new</span> ActionLink(<span class="red">"addLink"</span>, <span class="st">"Add"</span>);
     * addLink.setStyle(<span class="st">"color"</span>, <span class="st">"red"</span>);
     * addLink.setStyle(<span class="st">"border"</span>, <span class="st">"1px solid black"</span>); </pre>
     *
     * Will render the HTML as:
     * <pre class="codeHtml">
     * &lt;a href=".." <span class="st">style</span>=<span class="st">"color:red;border:1px solid black;"</span>&gt;<span class="st">Add</span>&lt;/a&gt;
     * </pre>
     * To remove an existing style, set the value to <tt>null</tt>.
     *
     * @param name the CSS style name
     * @param value the CSS style value
     */
    public void setStyle(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        String oldStyles = getAttribute("style");

        if (oldStyles == null) {

            if (value == null) {
                //Exit early
                return;
            } else {
                //If value is not null, append the new style and return
                getAttributes().put("style", name + ":" + value + ";");
                return;
            }
        }

        //Create buffer and estimate the new size
        HtmlStringBuffer buffer = new HtmlStringBuffer(
            oldStyles.length() + 10);

        //Parse the current styles into a map
        Map stylesMap = parseStyles(oldStyles);

        //Check if the new style is already present
        if (stylesMap.containsKey(name)) {

            //If the style is present and the value is null, remove the style,
            //otherwise replace it with the new value
            if (value == null) {
                stylesMap.remove(name);
            } else {
                stylesMap.put(name, value);
            }
        } else {
            stylesMap.put(name, value);
        }

        //The styles map might be empty if the last style was removed
        if (stylesMap.isEmpty()) {
            getAttributes().remove("style");
            return;
        }

        //Iterate over the stylesMap appending each entry to buffer
        Iterator it = stylesMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            String styleName = String.valueOf(entry.getKey());
            String styleValue = String.valueOf(entry.getValue());
            buffer.append(styleName);
            buffer.append(":");
            buffer.append(styleValue);
            buffer.append(";");
        }
        getAttributes().put("style", buffer.toString());
    }

    /**
     * Return true if CSS styles are defined.
     *
     * @deprecated use {@link #hasAttribute(String)} instead
     *
     * @return true if CSS styles are defined
     */
    public boolean hasStyles() {
        return (styles != null && !styles.isEmpty());
    }

    /**
     * Return the Map of control CSS styles.
     *
     * @deprecated use {@link #getAttribute(String)} instead
     *
     * @return the Map of control CSS styles
     */
    public Map getStyles() {
        if (styles == null) {
            styles = new HashMap();
        }
        return styles;
    }

    /**
     * Add the CSS class attribute. Null values will be ignored.
     * <p/>
     * For example given the ActionLink:
     *
     * <pre class="codeJava">
     * ActionLink addLink = <span class="kw">new</span> ActionLink(<span class="red">"addLink"</span>, <span class="st">"Add"</span>);
     * addLink.setStyle(<span class="st">"color"</span>, <span class="st">"red"</span>);
     * addLink.setStyle(<span class="st">"border"</span>, <span class="st">"1px solid black"</span>); </pre>
     *
     * Will render the HTML as:
     * <pre class="codeHtml">
     * &lt;a href=".." <span class="st">style</span>=<span class="st">"color:red;border:1px solid black;"</span>&gt;<span class="st">Add</span>&lt;/a&gt; </pre>
     *
     * @param value the class attribute to add
     */
    public void addStyleClass(String value) {
        //If vaule is null, exit early
        if (value == null) {
            return;
        }

        //If any class attributes already exist, check if the specified class
        //exists in the current set of classes.
        if (hasAttribute("class")) {
            String oldStyleClasses = getAttribute("class").trim();

            //Check if the specified class exists in the class attribute set
            boolean classExists = classExists(value, oldStyleClasses);

            if (classExists) {
                 //If the class already exist, exit early
                return;
            }

            //Specified class does not exist so add it with the other class
            //attributes
            getAttributes().put("class", oldStyleClasses + " " + value);

        } else {
            //Since no class attributes exist yet, only add the specified class
            setAttribute("class", value);
        }
    }

    /**
     * Removes the CSS class attribute.
     *
     * @param value the CSS class attribute
     */
    public void removeStyleClass(String value) {
        // If vaule is null, exit early
        if (value == null) {
            return;
        }

        // If any class attributes already exist, check if the specified class
        // exists in the current set of classes.
        if (hasAttribute("class")) {
            String oldStyleClasses = getAttribute("class").trim();

            //Check if the specified class exists in the class attribute set
            boolean classExists = classExists(value, oldStyleClasses);

            if (!classExists) {
                 //If the class does not exist, exit early
                return;
            }

            // If the class does exist, parse the class attributes into a set
            // and remove the specified class
            Set styleClassSet = parseStyleClasses(oldStyleClasses);
            styleClassSet.remove(value);

            if (styleClassSet.isEmpty()) {
                // If there are no more styleClasses left, remove the class
                // attribute from the attributes list
                getAttributes().remove("class");
            } else {
                // Otherwise render the styleClasses.
                // Create buffer and estimate the new size
                HtmlStringBuffer buffer = new HtmlStringBuffer(
                    oldStyleClasses.length() + value.length());

                // Iterate over the styleClassSet appending each entry to buffer
                Iterator it = styleClassSet.iterator();
                while (it.hasNext()) {
                    String entry = (String) it.next();
                    buffer.append(entry);
                    if (it.hasNext()) {
                        buffer.append(" ");
                    }
                }
                getAttributes().put("class", buffer.toString());
            }
        }
    }

    /**
     * Render the control's output to the specified buffer.
     * <p/>
     * If {@link #getTag()} returns null, this method will return an empty
     * string.
     * <p/>
     * @see net.sf.click.Control#render(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        if (getTag() == null) {
            return;
        }
        renderTagBegin(getTag(), buffer);
        renderTagEnd(getTag(), buffer);
    }

    /**
     * Returns the HTML representation of this control.
     * <p/>
     * This method delegates the rendering to the method
     * {@link #render(net.sf.click.util.HtmlStringBuffer)}. The size of buffer
     * is determined by {@link #getControlSizeEst()}.
     *
     * @see Object#toString()
     *
     * @return the HTML representation of this control
     */
    public String toString() {
        if (getTag() == null) {
            return "";
        }
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Perform a action listener callback if an ActionListener is defined,
     * or if a listener object and listener method is defined, otherwise
     * returns true.
     *
     * @see ClickUtils#invokeListener(Object, String)
     *
     * @return true if the invoked listener returns true, or if not listener
     * is defined
     */
    protected boolean invokeListener() {
        if (getActionListener() != null) {
            return getActionListener().onAction(this);

        } else {
            if (listener != null && listenerMethod != null) {
                return ClickUtils.invokeListener(listener, listenerMethod);

            } else {
                return true;
            }
        }
    }

    /**
     * Append all the controls attributes to the specified buffer.
     *
     * @param buffer the specified buffer to append all the attributes
     */
    protected void appendAttributes(HtmlStringBuffer buffer) {
        if (hasAttributes()) {
            buffer.appendAttributes(attributes);
        }
    }

    /**
     * Render the tag and common attributes.
     * <p/>
     * <b>Please note:</b> the tag will not be closed by this method. This
     * enables callers of this method to append extra attributes as needed.
     * <p/>
     * For example the result of calling:
     * <pre class="prettyprint">
     * Field field = new TextField("mytext");
     * HtmlStringBuffer buffer = new HtmlStringBuffer();
     * field.renderTagBegin("div", buffer);
     * </pre>
     * will be:
     * <pre class="prettyprint">
     * &lt;div name="mytext" id="mytext"
     * </pre>
     * Note that the tag is not closed.
     *
     * @param tagName the name of the tag to render
     * @param buffer the buffer to append the output to
     */
    protected void renderTagBegin(String tagName,
      HtmlStringBuffer buffer) {
        if (tagName == null) {
            throw new IllegalStateException("Tag cannot be null");
        }

        buffer.elementStart(tagName);

        String controlName = getName();
        if (controlName != null) {
            buffer.appendAttribute("name", controlName);
        }
        String id = getId();
        if (id != null) {
            buffer.appendAttribute("id", id);
        }

        appendAttributes(buffer);
    }

    /**
     * Closes the specifies tag.
     *
     * @param tagName the name of the tag to close
     * @param buffer the buffer to append the output to
     */
    protected void renderTagEnd(String tagName, HtmlStringBuffer buffer) {
        buffer.elementEnd();
    }

    /**
     * Return the estimated rendered control size in characters.
     *
     * @return the estimated rendered control size in characters
     */
    protected int getControlSizeEst() {
        int size = 0;
        if (getTag() != null && hasAttributes()) {
            //length of the markup -> </> == 3
            //1 * tag.length()
            size += 3 + getTag().length();
            //using 20 as an estimate
            size += 20 * getAttributes().size();
        }
        return size;
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Parse the specified string of style attributes and return a Map
     * of key/value pairs. Invalid key/value pairs will not be added to
     * the map.
     *
     * @param style the string containing the styles to parse
     * @return map containing key/value pairs of the specified style
     * @throws IllegalArgumentException if style is null
     */
    private Map parseStyles(String style) {
        if (style == null) {
            throw new IllegalArgumentException("style cannot be null");
        }

        //LinkHashMap is used to keep the order of the style names. Probably
        //makes no difference to browser but it makes testing easier since the
        //order that styles are added are kept when rendering the control.
        Map stylesMap = new LinkedHashMap();
        StringTokenizer tokens = new StringTokenizer(style, ";");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            int keyEnd = token.indexOf(":");

            //If there is no key/value delimiter or value is empty, continue
            if (keyEnd == -1 || (keyEnd + 1) == token.length()) {
                continue;
            }
            //Check that the value part of the key/value pair not only
            //consists of  a ';' char.
            if (token.charAt(keyEnd + 1) == ';') {
                continue;
            }
            String name = token.substring(0, keyEnd);
            String value = token.substring(keyEnd + 1);
            stylesMap.put(name, value);
        }

        return stylesMap;
    }

    /**
     * Parse the specified string of style attributes and return a Map
     * of key/value pairs. Invalid key/value pairs will not be added to
     * the map.
     *
     * @param styleClasses the string containing the styles to parse
     * @return map containing key/value pairs of the specified style
     * @throws IllegalArgumentException if styleClasses is null
     */
    private Set parseStyleClasses(String styleClasses) {
        if (styleClasses == null) {
            throw new IllegalArgumentException("styleClasses cannot be null");
        }

        //LinkHashMap is used to keep the order of the class names. Probably
        //makes no difference to browser but it makes testing easier since the
        //order that classes were added in, are kept when rendering the control.
        //Thus one can test whether the expected result and actual result match.
        Set styleClassesSet = new LinkedHashSet();
        StringTokenizer tokens = new StringTokenizer(styleClasses, " ");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            styleClassesSet.add(token);
        }

        return styleClassesSet;
    }

    /**
     * Return true if the new value exists in the current value.
     *
     * @param newValue the value of the class attribute to check
     * @param currentValue the current value to test
     * @return true if the classToFind exists in the current value set
     */
    private boolean classExists(String newValue, String currentValue) {
        //To find if a class eg. "myclass" exists, check the following:

        //1. Check if "myclass" is the only value in the string
        //   -> "myclass"
        if (newValue.length() == currentValue.length()
            && currentValue.indexOf(newValue) == 0) {
            return true;
        }

        //2. Check if "myclass" exists at beginning of string
        //   -> "myclass otherclass"
        if (currentValue.startsWith(newValue + " ")) {
            return true;
        }

        //3. Check if "myclass" exists in middle of string
        //   -> "anotherclass myclass otherclass"
        if (currentValue.indexOf(" " + newValue + " ") >= 0) {
            return true;
        }

        //4. Check if "myclass" exists at end of string
        //   -> "anotherclass myclass"
        return (currentValue.endsWith(" " + newValue));
    }
}
