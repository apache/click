package net.sf.click.sandbox.chrisichris.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.Table;
import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.Format;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.PageImports;
import net.sf.click.util.SessionMap;

/**
 * Base for controls which are composed of other Controls.
 * <p>
 * Similar to Page Controls and Models can be added to this control. The add
 * methods are protected to make encapsulated composites. An exactly same
 * version with public methods is
 * {@link net.sf.click.sandbox.chrisichris.control.Container}.
 * <p>
 * Like Page a Composite has {@link #onInit()} and {@link #onRender()} (like
 * Page.onPost(), Page.onGet()) methods. To guarantee that this methods are
 * called in the right order and only once a Composite has three consecutive
 * states:
 * <ol>
 * <li>{@link #NOT_INITIALIZED_STATE}: after construction and befor onInit()
 * is called</li>
 * <li>{@link #INITIALIZED_STATE}: after onInit() was called. move to with
 * {@link #initialize()}. This state is at least reached in onProcess() </li>
 * <li>{@link #READY_FOR_RENDER_STATE} after onRender() was called. move to
 * with {@link #prepareForRender()}. To this state must be moved in
 * toString(). </li>
 * </ol>
 * <p>
 * Controls and models can be added to the Composite from anywhere and any
 * time (ie a constructor or from the outside). It is not necessary to add them
 * in onInit() or onRender(), but recommended. The onInit() and onRender()
 * methods are hook methods where the composite makes sure that they are called
 * at the right time (the Compositie is set up - ie the Context is set) and that
 * they are only called once.
 * <p>
 * Normally instead of using {@link net.sf.click.control.Form} and {@link net.sf.click.control.ActionLink}
 * the controls {@link net.sf.click.sandbox.chrisichris.control.BaseForm} and
 * {@link net.sf.click.sandbox.chrisichris.control.ChildActionLink} should be used as 
 * child-controls for composites. 
 * <p>
 * To use the Form and ActionLink controls note the following:
 * In Click framework-action-controls like {@link net.sf.click.control.Form} and
 * {@link net.sf.click.control.ActionLink} must have a unique name within a Page. So that two or more
 * instances of a Composite can be used in one Page it is important to ensure that such child controls always
 * have unique names. One way to ensure this is to prefix the 'normal' control-name with the
 * id of the composite ({@link #getId()}). The id is only availabel in onInit(). Such controls should tahn
 * also be added to the model with their normal-name. {@link #addControl(Control)} will only add with
 * the default name: ie
 * <pre>
 *   onInit() {
 *     String id = getId();
 *     form = new Form(id+"_form");
 *     addControl(form);
 *     addModel("form",form);
 * </pre>
 * <p>
 * The {@link #getId()} method returns by default the the id path of all parent controls
 * separeted by _. This is
 * different to the normal click-controls. To keep this also for child controls
 * use {@link net.sf.click.sandbox.chrisichris.control.BaseForm} and
 * {@link net.sf.click.sandbox.chrisichris.control.ChildActionLink} instead of
 * the corresponding click controls.
 * <p>
 * A Composite can be set lazy (by default off). In this case the composite will
 * only initialize and process its child-controls if the
 * {@link net.sf.click.control.ActionLink#ACTION_LINK} request-param is present
 * and the value of the param starts with the id of the Composite. This is
 * useful if you have many (or expensive composites) on a Page which are not
 * allways rendered. Ie in a tab-control or with AJAX. Both the
 * {@link net.sf.click.sandbox.chrisichris.control.BaseForm} and
 * {@link net.sf.click.sandbox.chrisichris.control.ChildActionLink} controls
 * follow this contract. Click framwork-action-controls can not be used with this feature. 
 * 
 * <p>
 * Rendering: before rendering {@link #prepareForRender()} must be called it is called
 * in the {@link #render()} method, which is the method to render the composite.
 * A composite is rendered with the {@link #render()} method and <b>is not rendered
 * with toString()</b>. The reason for this is that in the eclipse debugger toString() is
 * used to show a value. In this case onrender gets called before onprocess which is not
 * desirable. 
 * <p>
 * The default implemantation of render()
 * renders a template from the current-class. To change this
 * behaviour override render() and call {@link #prepareForRender()} before
 * rendering from within toString().
 * 
 * @author Christian
 * 
 */
public class Composite implements Control {

    /**
     * The names says it all.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Initial State in which a composite is after construction.
     */
    public static final int NOT_INITIALIZED_STATE = 1;

    /**
     * After {@link #onInit()} was called
     */
    public static final int INITIALIZED_STATE = 2;

    /**
     * After {@link #onRender()} was called.
     */
    public static final int READY_FOR_RENDER_STATE = 3;

    /**
     * the context.
     */
    protected Context context;

    /**
     * The html id.
     */
    private String id;

    /**
     * The name.
     */
    private String name;

    /**
     * The parent (either Page or Control)
     */
    private Object parent;

    /**
     * The listener object.
     */
    private Object listener;

    /**
     * The listener method.
     */
    private String listenerMethod;

    /**
     * The current State
     */
    private int state = NOT_INITIALIZED_STATE;

    /**
     * Child controls.
     */
    private List controls;

    /**
     * The model.
     */
    private Map model;

    /**
     * Wheter lazy init or not.
     */
    private boolean lazyInit;

    // ###############################
    // constructors
    public Composite() {

    }

    public Composite(String name) {
        setName(name);
    }

    // #####################################
    // Properties
    /**
     * Return the Page request Context of the Control.
     * 
     * @return the Page request Context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set the Page request Context of the Control.
     * 
     * @param context
     *            the Page request Context
     * @throws IllegalArgumentException
     *             if the Context is null
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new NullPointerException("no context param");
        }
        this.context = context;

        if (hasControls()) {
            for (Iterator it = getControls().iterator(); it.hasNext();) {
                Control ctrl = (Control) it.next();
                ctrl.setContext(context);
            }
        }
    }

    /**
     * Return the HTML head element import string. This method returns null.
     * <p/> Override this method to specify JavaScript and CSS includes for the
     * HTML head element. For example:
     * 
     * <pre class="codeJava">
     *    &lt;span class=&quot;kw&quot;&gt;protected static final&lt;/span&gt; String HTML_IMPORT =
     *        &lt;span class=&quot;st&quot;&gt;&quot;&lt;script type=\&quot;text/javascript\&quot; src=\&quot;{0}/click/custom.js\&quot;&gt;&lt;/script&gt;&quot;&lt;/span&gt;;
     *   
     *    &lt;span class=&quot;kw&quot;&gt;public&lt;/span&gt; String getHtmlImports() {
     *        String[] args = { getContext().getRequest().getContextPath() };
     *        &lt;span class=&quot;kw&quot;&gt;return&lt;/span&gt; MessageFormat.format(HTML_IMPORTS, args);
     *    }
     * </pre>
     * 
     * <b>Note</b> multiple import lines should be separated by a <tt>'\n'</tt>
     * char, as the {@link net.sf.click.util.PageImports} will parse multiple
     * import lines on the <tt>'\n'</tt> char and ensure that imports are not
     * included twice.
     * 
     * @return the HTML head import statements for the control stylesheet and
     *         JavaScript files
     */
    public String getHtmlImports() {
        if (!hasControls()) {
            return null;
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer(80);
        List controls = getControls();
        for (Iterator it = controls.iterator(); it.hasNext();) {
            Control control = (Control) it.next();

            if (control instanceof Form) {
                buffer.append(((Form) control).getHtmlImportsAll());
            } else if (control instanceof Table) {
                String im = control.getHtmlImports();
                if (im != null) {
                    buffer.append(im);
                }
                List list = ((Table) control).getControls();

                for (int j = 0, size = list.size(); j < size; j++) {
                    Control tableControl = (Control) list.get(j);
                    im = tableControl.getHtmlImports();
                    if (im != null) {
                        buffer.append(im);
                    }
                }
            } else {
                String im = control.getHtmlImports();
                if (im != null) {
                    buffer.append(im);
                }
            }
        }
        String ret = buffer.toString();
        return ret;
    }

    /**
     * Return HTML element identifier attribute "id" value.
     * 
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
        if (this.id != null) {
            return this.id;
        } else {
            String id = ControlUtils.getId(this);
            if (id != null) {
                this.id = id;
            }
            return id;
        }
    }

    /**
     * Set the controls event listener. <p/> The method signature of the
     * listener is:
     * <ul>
     * <li>must hava a valid Java method name</li>
     * <li>takes no arguments</li>
     * <li>returns a boolean value</li>
     * </ul>
     * <p/> An example event listener method would be:
     * 
     * <pre class="codeJava">
     *    &lt;span class=&quot;kw&quot;&gt;public boolean&lt;/span&gt; onClick() {
     *        System.out.println(&lt;span class=&quot;st&quot;&gt;&quot;onClick called&quot;&lt;/span&gt;);
     *        &lt;span class=&quot;kw&quot;&gt;return true&lt;/span&gt;;
     *    }
     * </pre>
     * 
     * @param listener
     *            the listener object with the named method to invoke
     * @param method
     *            the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
        this.listener = listener;
        this.listenerMethod = method;
    }

    /**
     * Return the localized messages <tt>Map</tt> of the Control.
     * 
     * @return the localized messages <tt>Map</tt> of the Control
     */
    public Map getMessages() {
        if (getParent() == null) {
            return null;
        }

        if (getParent() instanceof Page) {
            return ((Page) getParent()).getMessages();
        }
        return ((Control) getParent()).getMessages();
    }

    /**
     * Return the name of the Control. Each control name must be unique in the
     * containing Page model or the containing Form.
     * 
     * @return the name of the control
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Control. Each control name must be unique in the
     * containing Page model or the containing Form.
     * 
     * @param name
     *            of the control
     * @throws IllegalArgumentException
     *             if the name is null
     */
    public void setName(String name) {
        if (!ControlUtils.isValidName(name)) {
            throw new IllegalArgumentException("name param is not valid: "
                    + name);
        }

        this.name = name;
    }

    /**
     * Return the parent of the Control.
     * 
     * @return the parent of the Control
     */
    public Object getParent() {
        return parent;
    }

    /**
     * Set the parent of the Control.
     * 
     * @param parent
     *            the parent of the Control
     */
    public void setParent(Object parent) {
        this.parent = parent;
    }

    /**
     * Convinience method to cast {@link #getParent()} to a control or return
     * null;
     * 
     * @return the parent control or null if no parent or the parent is a Page
     */
    public Control getParentControl() {
        Object parent = getParent();
        if (parent instanceof Control) {
            return (Control) parent;
        }
        return null;
    }

    /**
     * Convinience method to find the Page parent.
     * 
     * @return the Page this control is directly or indireclty a child of or
     *         null.
     */
    public Page getPage() {
        Object parent = getParent();
        while (parent != null) {
            if (parent instanceof Page) {
                return (Page) parent;
            } else if (parent instanceof Control) {
                parent = ((Control) parent).getParent();
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Wheter this control should only call {@link #onInit()} from
     * {@link #onProcess()} if the request-parameter
     * {@link ActionLink#ACTION_LINK} is present and starts with the id of this
     * control.
     * 
     * @return the lazy init status (default is false)
     */
    public boolean isLazyInit() {
        return lazyInit;
    }

    /**
     * See {@link #isLazyInit()}
     * 
     * @param lazy
     *            the lazy init status (default is false)
     */
    public void setLazyInit(boolean lazy) {
        this.lazyInit = lazy;
    }

    // ------Model ------------
    /**
     * Returns the model with the given name or null.
     * 
     * @param name
     *            the name
     * @return model object or null
     */
    protected Object getModel(String name) {
        if (model == null) {
            return null;
        }
        return model.get(name);
    }

    /**
     * The model map.
     * 
     * @return map of the model.
     */
    protected Map getModel() {
        if (model == null) {
            model = new HashMap();
        }
        return model;
    }

    // ------------------------------------
    // cutom methods

    // #################################
    // State management

    /**
     * The state of the control. A composite has form construction to render
     * three consecutive only states {@link #NOT_INITIALIZED_STATE},
     * {@link #INITIALIZED_STATE}, {@link #READY_FOR_RENDER_STATE}.
     * 
     * @return {@link #NOT_INITIALIZED_STATE}, {@link #INITIALIZED_STATE} or
     *         {@link #READY_FOR_RENDER_STATE}
     */
    public int getState() {
        return state;
    }

    /**
     * Wheter {@link #onInit()} has already be called.
     * 
     * @return true if onInit() was called
     */
    public boolean isInitialized() {
        boolean ret = state >= INITIALIZED_STATE;
        return ret;
    }

    /**
     * Makes sure that the Composite is initialized (in
     * {@link #INITIALIZED_STATE}. After Context is set this method can be at
     * any time called and gurantees that {@link #onInit()} is allways only
     * called once. {@link #onProcess()} calls this method before processing the
     * child controls. <p/> This method calls {@link #onInit()} if not already
     * in {@link #INITIALIZED_STATE} or {@link #READY_FOR_RENDER_STATE}. After
     * this method returns succesful the Composite will be in
     * {@link #INITIALIZED_STATE}. <p/>
     * 
     * @throws IllegalStateException
     *             if the context is not yet set.
     */
    public void initialize() throws IllegalStateException {
        if (!isInitialized()) {
            if (getContext() == null) {
                throw new IllegalStateException(
                        "The context has not been set yet");
            }
            onInit();
            state = INITIALIZED_STATE;
        }
    }

    /**
     * Hook method to setup this Composite similar to {@link Page#onInit()} - ie
     * add child Controls. This method is called by {@link #initialize()}.
     * Never call this method directly. The context and the parent is set when
     * this method is called. This implementation is empty.
     */
    protected void onInit() {
    }

    /**
     * Brings this method in the {@link #READY_FOR_RENDER_STATE}. This method
     * must be called before the Composite is rendered (typically from
     * toString()). This method makes sure that the Composite is initialized (by
     * calling {@link #initialize()} and than calls {@link #onRender()}. The
     * method makes also sure that the {@link #onRender()} is only called once.
     * This method can be called any time (repeatately) after the Context is
     * set.
     * 
     * @throws java.lang.IllegalStateException
     *             if the context is not set yet.
     */
    public void prepareForRender() throws IllegalStateException {
        if (!isInitialized()) {
            initialize();
        }
        if (!isReadyForRender()) {
            onRender();
            state = READY_FOR_RENDER_STATE;
        }
    }

    /**
     * Hook method to prepare the model for rendering. Similar to Page.onGet()
     * and Page.onPost(). This implementation is empty.
     * @see #prepareForRender()
     */
    protected void onRender() {
    }
    

    /**
     * Returns true if the onRender() method of this Composite has been called.
     * 
     * @return true if the Composite is in the {@link #READY_FOR_RENDER_STATE}
     */
    public boolean isReadyForRender() {
        return state >= READY_FOR_RENDER_STATE;
    }

    // ###########################################
    // processing

    /**
     * If the control is processable ({@link #isProcessable()}) it will first
     * initialize the control ({@link #initialize()}) and than process all the
     * child controls. After processing of the child controls the listener is
     * invoked.
     * 
     * @return true if not processed or all child controls and the listener
     *         returned true otherwise false.
     */
    public boolean onProcess() {
        if (isProcessable()) {
            initialize();
            if (hasControls()) {
                boolean continueProcessing = true;
                for (Iterator it = getControls().iterator(); it.hasNext();) {
                    Control ctrl = (Control) it.next();
                    continueProcessing = ctrl.onProcess();
                    if (!continueProcessing) {
                        return false;
                    }
                }
            }
        }
        if (listener != null && listenerMethod != null) {
            return ClickUtils.invokeListener(listener, listenerMethod);
        }
        return true;
    }

    /**
     * Wheter this control should be initialized and processed. Used by
     * {@link #onProcess()}.
     * 
     * @return
     */
    protected boolean isProcessable() {
        if (!isLazyInit()) {
            return true;
        }
        if (getContext() == null) {
            throw new IllegalStateException("Context has not been set");
        }

        // we must process multipart (because we can not resolve it here)
        if (getContext().isMultipartRequest()
                && getContext().getMultiPartFormData() == Collections.EMPTY_MAP) {
            return true;
        }

        // Only process only if the ACTION_LINK parameter does start with our id
        // means this was send by a child of us.
        String actionName = getContext().getRequestParameter(
                ActionLink.ACTION_LINK);

        if (actionName == null || !actionName.startsWith(getId())) {
            return false;
        } else {
            return true;
        }
    }

    // ###################################
    // child control

    /**
     * Adds the given Control as child of this control and to the model. The
     * control must not have yet a parent. The controls name must not be blank
     * and must not contain whitespace or the '_' charcter.
     * 
     * @param control
     *            the control
     */
    protected void addControl(Control control) {
        if (control == null) {
            throw new NullPointerException("no control param");
        }
        if (control.getParent() != null) {
            throw new IllegalArgumentException(
                    "The control has already a parent.");
        }
        if (!ControlUtils.isValidName(control.getName())) {
            throw new IllegalArgumentException("controls name ["
                    + control.getName() + "] is not valid");
        }

        addModel(control.getName(), control);

        getControls().add(control);

        control.setParent(this);
        if (getContext() != null) {
            control.setContext(getContext());
        }

    }

    /**
     * Removes the given control from this and the model.
     * 
     * @param ctrl
     *            the control to remove.
     * @return wheter the control was removed (was a child of this)
     */
    protected boolean removeControl(Control ctrl) {
        if (ctrl != null && ctrl.getParent() == this) {
            boolean removed = !hasControls() ? false : getControls().remove(
                    ctrl);
            if (removed) {
                Object mO = getModel(ctrl.getName());
                if (mO == ctrl) {
                    removeModel(ctrl.getName());
                }
                ctrl.setParent(null);
                return true;
            } else {
                throw new IllegalStateException(
                        "The ctrl has this as parent set but is no child of this");
            }
        }
        return false;
    }

    /**
     * Removes the control with the given name.
     * 
     * @param name
     *            name of the control
     * @return wheter a control whith this name was removed.
     */
    protected boolean removeControl(String name) {
        return removeControl(getControl(name));

    }

    /**
     * Returns the child control with the given name or null.
     * 
     * @param name
     *            name of the control
     * @return the control or null.
     */
    protected Control getControl(String name) {
        if (!hasControls()) {
            for (Iterator it = getControls().iterator(); it.hasNext();) {
                Control ctrl = (Control) it.next();
                if (ctrl.getName().equals(name)) {
                    return ctrl;
                }
            }
        }
        return null;
    }

    /**
     * The list of controls. The list is modifiable but should not be modified
     * generally.
     * 
     * @return list of child controls (never null).
     */
    protected List getControls() {
        if (controls == null) {
            return controls = new ArrayList();
        }
        return controls;
    }

    /**
     * Wheter the Composite has child-controls.
     * 
     * @return true if there are child-controls.
     */
    protected boolean hasControls() {
        return controls != null && !controls.isEmpty();
    }

    // ################################################
    // Public methods

    // ---- Control impl
    /**
     * The on deploy event handler, which provides classes the opportunity to
     * deploy static resources when the Click application is initialized. <p/>
     * For example:
     * 
     * <pre class="codeJava">
     *    &lt;span class=&quot;kw&quot;&gt;public void&lt;/span&gt; onDeploy(ServletContext servletContext) &lt;span class=&quot;kw&quot;&gt;throws&lt;/span&gt; IOException {
     *        ClickUtils.deployFile
     *            (servletContext, &lt;span class=&quot;st&quot;&gt;&quot;/com/mycorp/control/custom.js&quot;&lt;/span&gt;, &lt;span class=&quot;st&quot;&gt;&quot;click&quot;&lt;/span&gt;);
     *    }
     * </pre>
     * 
     * @param servletContext
     *            the servlet context
     * @throws IOException
     *             if a resource could not be deployed
     */
    public void onDeploy(ServletContext servletContext) {
    }

    /**
     * Add a Model object. 
     * 
     * @param name
     *            the name of the model (must not be null or blank)
     * @param value
     *            the value 
     * @throws IllegalArgumentException
     *             if an model with the given name already exists and the value
     *             is not null
     * @throws NullPointerException
     *             if the name is null or blank (empty or only whitespace)
     */
    protected void addModel(String name, Object value)
            throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isBlank(name)) {
            throw new NullPointerException("No name param");
        }
        
        if (model == null) {
            model = new HashMap();
        }
        
        if (model.containsKey(name)) {
            throw new IllegalArgumentException("A model with the name [" + name
                    + "] already exists");
        }
        model.put(name, value);
    }

    /**
     * Removes the given model.
     * 
     * @param name
     *            of the model
     * @return the removed model or null if no model with the given name
     */
    protected Object removeModel(String name) {
        if (model != null) {
            return model.remove(name);
        }
        return null;
    }

    // ---------- rendering code

    /**
     * Adds similar to Page the request, response, session, context, messages
     * and format of the page to the model. (Useful if you want to render the
     * model)
     * 
     * @param model
     *            the map to add the vars to.
     */
    public void addRenderingContext(Map model) {
        ControlUtils.populateModelMap(model,getPage(),getMessages());
    }

    /**
     * Used to render the control. Contract-Note: This method must call
     * {@link #prepareForRender()} before rendering the control.
     * 
     * <p/> This implementation adds the context vars
     * {@link #addRenderingContext(Map)} to the model and than renders the model
     * with {@link Context#renderTemplate(Class, Map)} with this.getClass().
     * 
     */
    public String render() {
        prepareForRender();
        addRenderingContext(getModel());
        String ret = getContext().renderTemplate(this.getClass(), getModel());
        return ret;

    }

    /** <b>Not used to render</b>. For rendering use {@link #render()}.
     *  By default returns the class name of this composite.
     */
    public String toString() {
        return this.getClass().getName()+"name: ["+getName()+"] id: ["+getId()+"]";
    }

    // ---------------------------------------
    // private methods

}
