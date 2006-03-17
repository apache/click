package net.sf.click.sandbox.chrisichris.prototype;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.click.Context;
import net.sf.click.Page;

import org.apache.commons.collections.FastHashMap;
import org.apache.commons.lang.StringUtils;

/**
 * An Action conceptionally like a Struts Action which can be defined on a Page.
 * <p>
 * An AjaxAction is defined as a static public final field in a Page class. The
 * name of the field is than used as the Action name. If this name is present in
 * the request param 'click_ajax' and the parameter-value starts with * than the
 * named Action gets executed instead of the normal page execution
 * (onInit/onProcess/onGet/onPost).
 * </p>
 * <p>
 * Note that there is only one instance of an AjaxAction per application
 * (defined static) and that this instance is executed multi-threaded. For
 * single threaded execution like Pages use AjaxAction.Listener.
 * </p>
 * <p>
 * To register AjaxActions for a class it is important to call
 * {@link #createActionMap(Class)} in a static initializer. Example:
 * </p>
 *
 * <pre>
 *
 *   public class MyPage extends AjaxPage {
 *     public static final AjaxAction sampleAction = new AjaxAction() {
 *         public void onProcess(Page page) {
 *            //evaluate the html paramst
 *            //render some response
 *         }
 *     }
 *
 *     static{
 *       AjaxAction.createActionMap(MyPage.class);
 *     }
 *
 *     public void onInitPage() {
 *         addModel(&quot;actionUrl&quot;, sampleAction.getUrl(getContext());
 *     }
 *   }
 *
 *   [use the action in html]
 *   &lt;a href=&quot;..../my-page.htm?click_ajax=*sampleAction&quot;&gt;
 *
 *   [or use in a velocity template]
 *   &lt;a href=&quot;$actionUrl&quot;&gt;
 *
 * </pre>
 *
 * @author chris
 *
 */
abstract public class AjaxAction {
    /**
     * If the value of the {@link #AJAX_REQUEST_PARAM} starts with this it
     * points to an AjaxAction. The value is '*'
     */
    public static final String AJAX_ACTION_INDICATOR = "*";

    /**
     * Request-parameter name indicating that the request is an AjaxRequest. The
     * value is 'click_ajax'
     */
    public static final String AJAX_REQUEST_PARAM = "click_ajax";

    /**
     * Map&gt;Class,Map&gt;String,AjaxAction&lt;&lt;.
     */
    private final static FastHashMap CACHE = new FastHashMap();
    static {
        CACHE.setFast(true);
    }

    /**
     * the name of this action.
     */
    private String name;

    /**
     * The class this action is defined on.
     */
    private Class clazz;

    /**
     * A path which maps to the page where the action belongs to.
     */
    private String path;

    /**
     * Default constructor, if used, the Page which defines this action must map
     * unique to a path.
     *
     */
    public AjaxAction() {
        super();
        this.path = null;
    }

    /**
     * Explcitly define the path which maps to the page which defines this
     * action.
     *
     * @param pagePath path to the page containing this action
     */
    public AjaxAction(String pagePath) {
        this.path = pagePath;
    }

    /**
     * The name of the action. It is automatically assigned and starts with '*'
     *
     * @return the name of this action (field name)
     */
    public String getName() {
        return name;
    }

    /**
     * The path defined in the constructor of this action or null if the default
     * constructor was used.
     *
     * @return path to the page containing this action or null if not specified
     * in the constructor
     */
    public String getPath() {
        return path;
    }

    /**
     * The class this action is defined in.
     *
     * @return Page class which defines this action
     */
    public Class getDefiningClass() {
        return clazz;
    }

    /**
     * Executes the action. This will call {@link #doExecute(Page)}
     *
     * @param page
     *            instance of the Page in which this AjaxAction is delcared
     */
    public void execute(Page page) {
        if (page.getClass() != clazz) {
            throw new IllegalArgumentException(
                    "An action can only be called with a Page which"
                    + "is of exactly the same class the action was defined on");
        }
        doExecute(page);
    }

    /**
     * Must be implemented to do the actual execution.
     *
     * @param page
     *            instance of the Page on which the AjaxAction is declared
     */
    abstract protected void doExecute(Page page);

    /**
     * Called after the action is created. This method is called automatically
     * so it generally does not have to be called by user code.
     *
     */
    public void init() {
    }

    /**
     * Returns the url which triggers this action. If the path to the page
     * defining this Action is given in the contructor the path is used.
     * Otherwise {@link Context#getPagePath(Class)} for the definingPage of this
     * action. In this case if there is no unique mapping this method throws an
     * exception and the constructor {@link #AjaxAction(String)} should be used.
     *
     * @param ctxt
     *            the current Context
     * @return url mapping to this action (the url is not servlet encoded)
     */
    public String getUrl(Context ctxt) {
        String url = ctxt.getRequest().getContextPath();
        if (this.path == null) {
            if (this.clazz == null) {
                throw new IllegalStateException(
                        "The action is not initialized. Please call"
                        + "AjaxAction.createActionMap(definingClass) "
                        + "in a static initializer.");
            }
            url = url + ctxt.getPagePath(clazz);
        } else {
            url = url + path;
        }
        String ret = appendToUrl(url, getName());
        return ret;
    }

    /**
     * Creates an ajax url by appending the ajaxParameter with the value of the
     * ajaxName to the url.
     *
     * @param url original url
     * @param ajaxName the value of the ajax parameter
     * @return url + ajax parameter
     */
    public static String appendToUrl(String url, String ajaxName) {
        if (url == null) {
            throw new NullPointerException("No url param");
        }
        ajaxName = StringUtils.defaultString(ajaxName);
        String ret = url + getUrlParamSeparator(url) + AJAX_REQUEST_PARAM + "="
                + ajaxName;
        return ret;
    }

    /**
     * Returns ? or & for appending a parameter to an url depending wheter the
     * url has already a ?.
     *
     * @param url the url to which to append
     * @return '?' or '&'
     */
    public static char getUrlParamSeparator(String url) {
        if (url.indexOf('?') == -1) {
            return '?';
        } else {
            return '&';
        }
    }

    /**
     * Creates a Map&gt;String,AjaxAction&lt; by inspecting all public final static
     * fields of the given class wheter they contain an AjaxAction. The key of
     * the returned map is the '*'+fieldName the value is the AjaxAction.
     *
     * @param cl
     *            the page class which defines the AjaxActions
     * @return Map&gt;String,AjaxAction&lt;
     */
    public static Map createActionMap(Class cl) {
        Map/* <String, AjaxAction> */ret = new HashMap();
        // now create the map
        Field[] fields = cl.getFields();
        for (int i = 0, size = fields.length; i < size; i++) {
            final Field field = fields[i];
            final int modif = field.getModifiers();

            if (AjaxAction.class.isAssignableFrom(field.getType())
                    && Modifier.isStatic(modif)) {

                if (!Modifier.isPublic(modif) || !Modifier.isFinal(modif)) {
                    throw new IllegalStateException(
                            "The AjaxAction defining field [" + field + "] on"
                                    + "class [" + cl
                                    + "] is not public and final");
                }
                if (!Page.class.isAssignableFrom(field.getDeclaringClass())) {
                    throw new IllegalStateException("The class [" + cl
                            + "] which defines an ajaxAction " + "in field ["
                            + field + "] " + "is no Page class");
                }

                final String name = AJAX_ACTION_INDICATOR + field.getName();
                try {
                    AjaxAction action = (AjaxAction) field.get(null);
                    if (action == null) {
                        throw new NullPointerException(
                                "No action is defined for the static field: "
                                        + field);
                    }
                    if (action.name == null) {
                        action.name = name;
                        action.clazz = field.getDeclaringClass();
                        action.init();
                    }

                    ret.put(name, action);
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException(
                            "Please report this is a bug");
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not access field ["
                            + field + "]");
                }

            }
        }
        ret = Collections.unmodifiableMap(ret);
        CACHE.put(cl, ret);
        return ret;
    }

    /**
     * This helper will examine the current request for the
     * {@link #AJAX_REQUEST_PARAM} if it is present and the value starts with
     * {@link #AJAX_ACTION_INDICATOR} it looks up the AjaxAction and returns it.
     *
     * @param page
     *            the page on which this should be executed.
     * @return the AjaxAction found or null
     */
    public static AjaxAction getAjaxAction(Page page) {
        if (page.getContext().isForward()) {
            return null;
        }
        String ajaxName = getAjaxName(page.getContext());

        if (StringUtils.isBlank(ajaxName)) {
            return null;
        }

        ajaxName = ajaxName.trim();

        if (!ajaxName.startsWith(AJAX_ACTION_INDICATOR)) {
            return null;
        }

        Map actions = (Map) CACHE.get(page.getClass());
        if (actions == null) {
            actions = createActionMap(page.getClass());
            if (actions == null) {
                throw new IllegalStateException(
                        "There are no actions for page [" + page.getClass()
                                + "]");
            }
        }
        AjaxAction ac = (AjaxAction) actions.get(ajaxName);
        return ac;
    }

    /**
     * Checks wheter the given request is an AjaxRequest. Sees if the the
     * {@link #AJAX_REQUEST_PARAM} is present
     *
     * @param ctxt current context
     * @return true if the parameter present otherwise false
     */
    public static boolean isAjax(Context ctxt) {
        boolean ret = ctxt.getRequest()
                        .getParameter(AJAX_REQUEST_PARAM) != null;
        return ret;
    }

    /**
     * Returns the value of the {@link #AJAX_REQUEST_PARAM}.
     *
     * @param ctxt current context
     * @return parameter value
     */
    public static String getAjaxName(Context ctxt) {
        return ctxt.getRequestParameter(AJAX_REQUEST_PARAM);
    }

    /**
     * Writes the given value directly to the response.
     *
     * @param page
     *            the currently executing page
     * @param value
     *            the string value to write
     * @param contentType
     *            the content type of the value
     */
    public static void writeDirectlyToResponse(Page page, String value,
            String contentType) {
        if (value == null) {
            throw new NullPointerException("No value param provided");
        }
        if (contentType == null) {
            contentType = "text/xml";
        }
        String charset = page.getContext().getRequest().getCharacterEncoding();
        if (charset != null && contentType.indexOf("charset=") == -1) {
            contentType = contentType + "; charset=" + charset;
        }

        HttpServletResponse response = page.getContext().getResponse();

        response.setContentType(contentType);

        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(value);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Exception occured when writing to the output");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        page.setPath(null);
    }

    /**
     * An AjaxAction which invokes a certain method on the page instance it is
     * defined in. A page instance is bound to the current thread so that the
     * called method is not called from multiple-threads.
     *
     * @author Christian Essl
     *
     */
    public static class Listener extends AjaxAction {
        private final String methodName;

        private Method method;

        /**
         * Constructor where the page in which the AjaxAction is defined maps to
         * a unique htm template.
         *
         * @param methodName
         *            the public void method which is executed on the page
         *            instance
         */
        public Listener(String methodName) {
            if (methodName == null) {
                throw new NullPointerException("No methodName provided");
            }
            this.methodName = methodName;
        }

        /**
         * Constructor where the path to the declaring page is given explicitly.
         *
         * @param pagePath
         *            the path to the Page
         * @param methodName
         *            the public void method which is executed on the page
         *            instance
         */
        public Listener(String pagePath, String methodName) {
            super(pagePath);
            if (methodName == null) {
                throw new NullPointerException("No methodName provided");
            }
            this.methodName = methodName;
        }

        /**
         * Loads the method.
         *
         * @see net.sf.click.sandbox.chrisichris.prototype.AjaxAction#init()
         */
        public void init() {
            Class cl = getClass();
            Method m = null;
            try {
                m = cl.getMethod(methodName, null);
            } catch (Exception e) {
                IllegalStateException ex = new IllegalStateException(
                        "Could not get to method [" + methodName + "]"
                                + "in class [" + cl + "]");
                throw ex;
            }
            if (m.getReturnType() != Void.TYPE) {
                throw new IllegalStateException("The method [" + m
                        + " does not return void.");
            }
            this.method = m;
        }

        /**
         * invokes the method given in the constructor on the current page.
         * @param page the current page
         * @see net.sf.click.sandbox.chrisichris.prototype.AjaxAction#doExecute(net.sf.click.Page)
         */
        protected void doExecute(Page page) {
            try {
                this.method.invoke(page, null);
            } catch (InvocationTargetException ite) {

                Throwable e = ite.getTargetException();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;

                } else if (e instanceof Exception) {
                    String msg = "Exception occured invoking public method: "
                            + method;

                    throw new RuntimeException(msg, e);

                } else if (e instanceof Error) {
                    String msg = "Error occured invoking public method: "
                            + method;

                    new RuntimeException(msg, e);

                } else {
                    String msg = "Error occured invoking public method: "
                            + method;

                    throw new RuntimeException(msg, e);
                }

            } catch (Exception e) {
                String msg = "Exception occured invoking public method: "
                        + method;

                throw new RuntimeException(msg, e);
            }

        }
    }

}
