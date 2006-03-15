package net.sf.click.sandbox.chrisichris.prototype;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NameParser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Button;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A utility class which creates JS script for the prototype Ajax calls. It does nothing
 * processing specific just create JS and can be used to setup parts of the call in the page and
 * parts in the template. It has verious properties for all the prototype supported properties and a bit more.
 * @author Christian Essl
 *
 */
public class PrototypeAjax extends Prototype implements Cloneable{
    public static final String DEFAULT_PARAM_NAME = ActionLink.VALUE;

    /**
     * Header send by the server if the AJAX response
     * has JSON content and this is evaluated
     */
    public static final String JSON_SERVER_HEADER = "X-JSON";

    /**
     * if the content-type of the AJAX-response is "text/javascript"
     * the script will be evaluated 
     */
    public static final String CONTENT_TYPE_JS = "text/javascript";

    private String url;
    /////////////////////////
    //options
    
    
    
    
    
    /**
     * Event of running AJAX-Request
     */
    private static final String ON_UNINITIALIZED = "onUninitialized";
    
    /**
     * Event of running AJAX-Request
     */
    private static final String ON_LOADING = "onLoading";
    
    /**
     * Event of running AJAX-Request
     */
    private static final String ON_LOADED = "onLoaded";

    /**
     * Event of running AJAX-Request
     */
    private static final String ON_INTERACTIVE = "onInteractive";

    /**
     * Event of running AJAX-Request
     */
    private static final String ON_COMPLETE = "onComplete";

    /**
     * JS function to execute when the AJAX request is complete
     * the funciton will get the transport (with responseText) and wheter json is send or not
     * onSucess
     */
    private static final String ON_SUCCESS = "onSuccess";
    
    /**
     * JS function to execute when the AJAX request is complete
     * the funciton will get the transport (with responseText) and wheter json is send or not
     * onFailure
     */
    private static final String ON_FAILURE = "onFailure";
    
    
    /**
     * JS function which is executed if an exception has occured
     */
    private static final String ON_EXCEPTION = "onException";


    /**
     * JS function to execute when the AJAX request is complete
     * the funciton will get the transport (with responseText) and wheter json is send or not
     * the key is either a response code (202), Sucess, Failure or one of the REUEST_EVENT constants
     * the value is the JS function to execute
     * (putAll to options with 'on'+key - directly put in options not as sub-map)
     */
    private Map/*<Integer,String>*/ completeCode = new HashMap();
    
    /**
     * The request method, "post or get" default is get
     */
    private String method = "get";

    /**
     * wheter asychronouse AJAX
     */
    private boolean asynchronous = true;

    /**
     * option wheter scripts shold be evaluated if
     * contained in response
     */
    private boolean evalScripts = true;
    
    /**
     * in case of update where to insert the received content.
     */
    private String insertion;
    
    /**
     * The request parameters
     */
    private Map/*<String,String>*/ parameters = new HashMap();
    
    
    /**
     * JS expression of the element which to submit
     */
    private String postWith;
    
    /**
     * The id of a form or other html elemtn which contains form_elemnts if this
     * is given the parameters will be set to this
     */
    private String postForm;
    
    /**
     * The container to use in case of success for update
     * passed in as as value in container map key = success
     * See ln 750
     */
    String containerSuccess;
    
    /**
     * The container to use in case of failure for update
     * passed in as value in container map key = failure
     * 
     */
    String containerFailure;

    /**
     * The requestHeaders 
     * PROTOTYPE needs an array where the first is the name
     * the second is the value (line 679)
     * Put as list in options
     */
    private Map/*<String,String>*/ requestHeaders = new HashMap();
    
    protected Map/*<String,String>*/ options = new HashMap();
    

    /////////////////////////////////
    //code generation strings
    
    /**
     * Text to be shown in a confirm dialog before AJAX is done
     */
    private String confirm;
    
    /**
     * Condition for AJAX call
     */
    private String condition;
    
    /**
     * JS code executed directly after the AJAX call
     */
    private String after;
    
    /**
     * JS code executed directly before AJAX call
     */
    private String before;
    
    /**
     * Element id of a progress image which is shown when the 
     * AJAX request is executing.
     */
    private String progressImage;
    
    public PrototypeAjax() {
        super();
    }
    
    public PrototypeAjax(String url) {
        this.setUrl(url);
    }
    
    private void putOption(String name,String value) {
        if(value == null) {
            options.remove(name);
        } else {
            options.put(name, value);
        }
    }
    
    /**
     * @return Returns the postWith.
     */
    public String getPostWith() {
        return postWith;
    }
    
    /**
     * A JavaScript String expression which is appended to the
     * the request parameters. The resulting String takes the form of name=value...
     * @param js string expression
     * @return this
     */
    public PrototypeAjax setPostWith(String js) {
        postWith = js;
        return this;
    }
    
    /**
     * Sets the {@link #setPostWith(String)} parameter
     * to "Form.serialize(this.form)". Meaning that all
     * the form-fields except buttons are send as parameters
     * of the form where the AJAX link or button is included
     * 
     * @return this
     */
    public PrototypeAjax setPostWithCurrentForm() {
        return setPostWith("Form.serialize(this.form)");
    }

    /**
     * The html elementId of the element which should be updated
     * with the succes and failure result of the AJAX call.
     * @param elementId the elementId. 
     * 
     * @return this
     */
    public PrototypeAjax setUpdate(String elementId) {
        this.containerFailure = elementId;
        this.containerSuccess = elementId;
        return this;
    }
    
    /**
     * The html elementId of the element which should be updated
     * whith the result of the ajax call in case of succes.
     * @param elementId html elementId
     * @return this
     */
    public PrototypeAjax setUpdateSuccess(String elementId) {
        this.containerSuccess = elementId;
        return this;
    }
    
    /**
     * The html elementId of the element which should be updated
     * with the result of the ajax call in case of failure.
     * @param elementId html id
     * @return this
     */
    public PrototypeAjax setUpdateFailure(String elementId) {
        this.containerFailure = elementId;
        return this;
    }
 
    /**
     * @return Returns the updateFailure element-id.
     */
    public String getUpdateFailure() {
        return containerFailure;
    }
    
    /**
     * @return Returns the updateSuccess element-id.
     */
    public String getUpdateSuccess() {
        return containerSuccess;
    }
 
    /**
     * Wheter the request should be send asynchronour or not default
     * is true
     * @return the value
     */
    public boolean isAsynchronous() {
        return asynchronous;
    }


    /**
     * Wheter the request should be send asynchronour or not default
     * is true
     * @param asynchronous true = asychnronouse
     * @return this
     */
    public PrototypeAjax setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
        return this;
    }


    /**
     * Returns the JS function to call in case of the given
     * http code returned from the server
     * @param code a code ie 201
     * @return the JS to call
     */
    public String getOnCode(Integer code) {
        return (String)completeCode.get(code);
    }


    /**
     * Register the given JS function to be called if the server
     * response has the given result code.
     * @param code the result code ie 201
     * @param js a JavaScript function
     * @return this
     */
    public PrototypeAjax setOnCode(int code, String js) {
        if(code < 0 || code > 999) {
            throw new IllegalArgumentException("No valid code");
        }
        if(js == null) {
            this.completeCode.remove(new Integer(code));
        } else {
            this.completeCode.put(new Integer(code), js);
        }
        return this;
    }


    /**
     * The http request method. Default is get
     * @return "get" or "post"
     */
    public String getMethod() {
        return method;
    }

    /**
     * The http request method. Default is get
     * @param method if post it is post otherwise set to get
     * @return this
     */
    public PrototypeAjax setMethod(String method) {
        if (method == null) {
            method = "get";
        } else {
            method = method.toLowerCase().trim();
            if(!"post".equals(method)) {
                method = "get";
            }
        }
        this.method = method;
        return this;
    }

    /**
     * In case of {@link #setUpdate(String)} where to insert the
     * result.
     * @return the insertion.
     */
    public String getInsertion() {
        return insertion;
    }
    
    /**
     * In case of {@link #setUpdate(String)} where to insert the
     * result. This is one of the PrototypeControl.INSERT constants.
     * Default is to replace the content.
     * @param str one of the PrototypeControl.INSERT_ constants or null.
     * @return this
     */
    public PrototypeAjax setInsertion(String str) {
        this.insertion = insertion;
        return this;
    }

    /**
     * JS code to execute in case an exception happens in one of
     * the other event handlers
     * @return js function
     */
    public String getOnException() {
        return (String) options.get(ON_EXCEPTION);
    }


    /**
     * JS code to execute in case an exception happens in one of
     * the other event handlers
     * @param onException the JS 
     * @return this
     */
    public PrototypeAjax setOnException(String onException) {
        putOption(ON_EXCEPTION, onException);
        return this;
    }

    /**
     * The JS code to execute in case a failure response is reported
     * by the server.
     * @return the coe
     */
    public String getOnFailure() {
        return (String)options.get(ON_FAILURE);
    }

    /**
     * The JS code to execute in case a failure response is reported
     * by the server.
     * @param onFailure the JS code
     * @return this
     */
    public PrototypeAjax setOnFailure(String onFailure) {
        putOption(ON_FAILURE,onFailure);
        return this;
    }

    /**
     * The JS code to execute in case of succes of the response
     * @return the code
     */
    public String getOnSuccess() {
        return (String) options.get(ON_SUCCESS);
    }

    /**
     * The JS code to execute in case of succes of the response
     * @param onSucess JS code
     * @return this
     */
    public PrototypeAjax setOnSuccess(String onSucess) {
        putOption(ON_SUCCESS,onSucess);
        return this;
    }

    
    
    /**
     * Returns all the parameters set as String
     * @return
     */
    public String getParametersString() {
        StringBuffer ret = new StringBuffer();
        if(parameters != null && !parameters.isEmpty()) {
            boolean first = true;
            for (Iterator it = parameters.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry/*< String, String>*/ para = (Map.Entry) it.next();
                if(!first) {
                    ret.append("+'&'+");
                }
                ret.append("encodeURIComponent('");
                ret.append(para.getKey());
                ret.append("')+'='+encodeURIComponent(");
                ret.append(para.getValue());
                ret.append(')');
                first = false;
            }
        }
        return ret.toString();
    }
    
    /**
     * Returns the options representaiton of all Parameters,
     * @return
     */
    public String getParametersOption() {
        String paramStr = getParametersString();
        StringBuffer stB = new StringBuffer();
        boolean append = false;
        if(!StringUtils.isBlank(paramStr)) {
            stB.append(paramStr);
            append = true;
        }
        
        if(postForm != null) {
            if(append) {
                stB.append(" + '&' + ");
            }
            stB.append("Form.serialize(document.getElementById('");
            stB.append(postForm);
            stB.append("'))");
            append = true;
        } 
        if (postWith != null) {
            if(append) {
                stB.append(" + '&' + ");
            }
            stB.append(postWith);
        }
        return stB.toString();
    }
    
    /**
     * Returns the map of parameters to be set
     * @return map
     */
    public Map/*<String,String>*/ getParamters() {
        if(parameters == null) {
            parameters = new HashMap/*<String, String>*/();
        }
        return parameters;
    }

    /**
     * returns the value of the given paramter
     * @param name para name
     * @return value or null if not set
     */
    public String getParameter(String name) {
        if(parameters == null) {
            return null;
        }
        return (String) parameters.get(name);
    }

    /**
     * Adds the given parameter string. If value is null the paramter is
     * removed. The value is the plain string which will be send as a parameter
     * the string will be URLEncoded.
     * @param name of the parameter
     * @param value String value of the paramter or null
     * @return this
     */
    public PrototypeAjax addParameter(String name,String value) {
        if(name == null) {
            throw new NullPointerException("No name");
        }
        name = name.trim();
        if(value == null) {
            if(parameters != null) {
                parameters.remove(name);
            }
        }else{
            if(parameters == null) {
                parameters = new HashMap/*<String, String>*/();
            }
            value = StringEscapeUtils.escapeJavaScript(value);
            value = "'"+value+"'";
            parameters.put(name, value);
        }
        return this;
    }
    
    /**
     * Adds a parameter to request, where the value is at request sendtime
     * caculated as JS. 
     * @param name of the parameter (normal string)
     * @param valueJS JS to caculate the value of the parameter
     * @return this
     */
    public PrototypeAjax addJSParameter(String name, String valueJS) {
        if(name == null) {
            throw new NullPointerException("No name param");
        }
        name = name.trim();
        if(valueJS == null) {
            if(parameters != null) {
                parameters.remove(name);
            }
        }else{
            if(parameters == null) {
                parameters = new HashMap/*<String, String>*/();
            }
            parameters.put(name, valueJS);
        }
        return this;
    }
    

    
    /**
     * Clears all parameters.
     * @return
     */
    public PrototypeAjax clearParameters(){
        parameters = null;
        return this;
    }
    
    /**
     * Returns the request-header value with the given name
     * @param name name of header
     * @return value of header
     */
    public String getRequestHeader(String name) {
        return (String)requestHeaders.get(name);
    }

    /**
     * Adds the given request header
     * @param name of request header
     * @param value of request header if null the header is removed if present
     * @return
     */
    public PrototypeAjax addRequestHeader(String name, String value) {
        if(name == null) {
            throw new NullPointerException("No name param");
        }
        
        if (value == null) {
            this.requestHeaders.remove(name);
        } else {
            this.requestHeaders.put(name, value);
        }
        return this;
    }

    /**
     * JS code executed when the AJAX Request is completed.
     * @return code
     */
    public String getOnComplete() {
        String ret = (String) options.get(ON_COMPLETE);
        if(progressImage != null) {
            String prS = "Element.hide('"+progressImage+"');";
            if(ret == null) {
                return prS;
            } else {
                ret = prS+ret;
            }
        }
        return ret;
    }

    /**
     * JS code to execute when the AJAX Request is completed.
     * @param onComplete JS code
     * @return this
     */
    public PrototypeAjax setOnComplete(String onComplete) {
        putOption(ON_COMPLETE, onComplete);
        return this;
    }

    /**
     * JS code to execute when the AJAX Request enters interactive
     * state
     * @return code
     */
    public String getOnInteractive() {
        return (String) options.get(ON_INTERACTIVE);
    }

    /**
     * JS code to execute when AJAX Request enters interactive state
     * @param onInteractive js code
     * @return this
     */
    public PrototypeAjax setOnInteractive(String onInteractive) {
        putOption(ON_INTERACTIVE,onInteractive);
        return this;
    }


    /**
     * JS code to execute when AJAX Request enters loaded state
     * @return code
     */
    public String getOnLoaded() {
        return (String)options.get(ON_LOADED);
    }

    /**
     * JS code to execute when the AJAX Request enters loaded state
     * @param onLoaded js code
     * @return this
     */
    public PrototypeAjax setOnLoaded(String onLoaded) {
        putOption(ON_LOADED, onLoaded);
        return this;
    }

    /**
     * JS code to execute when the AJAX Request enters loading state.
     * @return js code
     */
    public String getOnLoading() {
        return (String)options.get(ON_LOADING);
    }

    /**
     * JS code to execute when the AJAX Request enter loading state.
     * @param onLoading js code
     * @return this
     */
    public PrototypeAjax setOnLoading(String onLoading) {
        putOption(ON_LOADING, onLoading);
        return this;
    }

    /**
     * JS code to execute when the AJAX request is in uninitalized state
     * @return the code
     */
    public String getOnUninitailzed() {
        return (String)options.get(ON_UNINITIALIZED);
    }

    /**
     * JS code to execute when the AJAX request is in unintiatlized state
     * @param onUninitailzed js code
     * @return this
     */
    public PrototypeAjax setOnUninitailzed(String onUninitailzed) {
        putOption(ON_UNINITIALIZED, onUninitailzed);
        return this;
    }

    /**
     * The id of the form or any other html element which contains input elements
     * which will be submit as the part of this request.
     * @return Returns id of the html element.
     */
    public String getPostForm() {
        return postForm;
    }
    
    /**
     * The id of the form or any other html element which contains input elements
     * which will be submit as the part of this request.
     * @param elementId html element id
     * @return this
     */
    public PrototypeAjax setPostForm(String elementId) {
        postForm = elementId;
        return this;
    }

    /**
     * JS Code to execute right after the AJAX request was initiated and before onloading.
     * @return JS code
     */
    public String getAfter() {
        return after;
    }

    /**
     * JS Code to execute right after the AJAX request was initiated and before onloading.
     * @param after JS code
     * @return this
     */
    public PrototypeAjax setAfter(String after) {
        this.after = after;
        return this;
    }

    /**
     * JS Code to execute before the AJAX request is initiated (used ie to check wheter
     * a Ajax request should be made at all)
     * @return JS Code
     */
    public String getBefore() {
        return before;
    }

    /**
     * JS Code to execute before the AJAX request is initiated (used ie to check wheter
     * a Ajax request should be made at all)
     * @param before JS code
     * @return this
     */
    public PrototypeAjax setBefore(String before) {
        this.before = before;
        return this;
    }

    /**
     * JS boolean expression which describes wheter an AJAX call should be made at all.
     * @return JS code
     */
    public String getCondition() {
        return condition;
    }

    /**
     * JS boolean expression which describes wheter an AJAX call should be made at all.
     * @param condition the Js expression
     * @return this
     */
    public PrototypeAjax setCondition(String condition) {
        this.condition = condition;
        return this;
    }


    /**
     * Text to be shown in a confirm dialog before a request is done.
     * @return dialog text
     */
    public String getConfirm() {
        return confirm;
    }

    /**
     * Text to be shown in a confirm dialog before a request is done.
     * @param confirm
     * @return
     */
    public PrototypeAjax setConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }


    /**
     * Elemetn id of an image which is shown while the AJAX request is running
     * @return Returns the progressImage.
     */
    public String getProgressImage() {
        return progressImage;
    }
    
    /**
     * Element id of an image which is shown while the AJAX request is running.
     * @param elementId
     * @return
     */
    public PrototypeAjax setProgressImage(String elementId){
        this.progressImage = elementId;
        return this;
    }
    
    
    /**
     * Creates the JS options without paranteses
     * @return
     */
    public String createJSOptions() {
        StringBuffer stB = new StringBuffer(options.size()*15);
        
        //general things
        stB.append("asynchronous : ");
        stB.append(asynchronous ? "true, " : "false, ");
        
        stB.append("method : '");
        stB.append(method);
        stB.append("', ");
        
        if(insertion != null ){
            stB.append("insertion : ");
            stB.append(insertion);
            stB.append(", ");
        }
        
        stB.append("evalScripts : ");
        stB.append(evalScripts ? "true" : "false");
        
        //callbacks
        for (Iterator it = options.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry/*<String,String>*/ option = (Map.Entry/*<String,String>*/) it.next();
	
            String name = (String) option.getKey();
            //build callbacks
            if (name.startsWith("on")) {
                stB.append(", ");
                stB.append(name);
                stB.append(" : function(request,joson){");
                if(!ON_COMPLETE.equals(name)){
                    stB.append(option.getValue());
                }
                stB.append("}");
            }
        }
        
        //onComplete
        String onComplete = getOnComplete();
        if(onComplete != null) {
            stB.append(", onComplete : function(request,joson){");
            stB.append(onComplete);
            stB.append("}");
        }
        
        //complete code functions
        for (Iterator it = completeCode.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry/*<Integer,String>*/ option = (Map.Entry/*<Integer,String>*/) it.next();
            Integer code = (Integer) option.getKey();
            stB.append(", ");
            stB.append("on");
            stB.append(code);
            stB.append(" : function(request,joson){");
            stB.append(option.getValue());
            stB.append("}");
        }
        
        
        
        
        //requestHeaders
        if(!requestHeaders.isEmpty()) {
            stB.append(", ");
            stB.append(" requestHeaders : ");
            stB.append("[");
            boolean isFirst2 = true;
            for (Iterator it = requestHeaders.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry/*<String,String>*/ entry = (Map.Entry/*<String,String>*/) it.next();
                if(!isFirst2) {
                    stB.append(", ");
                } else {
                    isFirst2 = false;
                }
                stB.append("'");
                stB.append(entry.getKey());
                stB.append("', ");
                stB.append("'");
                stB.append(entry.getValue());
                stB.append("'");
            }
            stB.append("]");
        }
        
        //parameters
        stB.append(", ");
        stB.append("parameters: ");
        stB.append(getParametersOption());
        return stB.toString();
    }
    
    /**
     * create the JS for the AJAX call
     * @return
     */
    public String toJS() {
        String url = getUrl();
        if(url == null) {
            throw new IllegalStateException("No url set");
        }
        StringBuffer stB = new StringBuffer();
        
        if(containerFailure == null && containerSuccess == null) {
            stB.append("new Ajax.Request(");
        } else {
            stB.append("new Ajax.Updater({");
            if (containerSuccess != null) {
                stB.append("success : '");
                stB.append(containerSuccess);
                stB.append('\'');
            }
            
            if( containerFailure != null) {
                if(containerSuccess != null) {
                    stB.append(", ");
                }
                stB.append("failure : '");
                stB.append(containerFailure);
                stB.append('\'');
            }
            stB.append("}, ");
            
        }
        //the url
        stB.append("'");
        stB.append(url);
        stB.append("', {");
        
        //the params
        String params = createJSOptions();
        stB.append(params);
        stB.append("})");
        
        String function = stB.toString();
        
        if(progressImage != null) {
            function = "Element.show('"+progressImage+"');"+function;
        }
        
        if(before != null) {
            function = before +";"+function;
        }
        
        if(after != null) {
            function = function +"; "+after;
        }
        
        if(condition != null) {
            function = "if ("+condition+") {" + function +";}";
        }
        
        if(confirm != null) {
            function = "if (confirm('"+StringEscapeUtils.escapeJavaScript(confirm)+"')) {"+function+";}";
        }
        
        return function;
        
    }

    /**
     * Retursn "eval(request.responseText)" which can be called in onComplete to evaluate
     * a javascript returned in case of multiple updates
     * @return
     */
    public String evaluateRemoteResponse(){
        return "eval(request.responseText)";
    }
    
    /**
     * returns the toJS()+;return false; typcially used in in onClick attributes.
     * @return
     */
    public String onClickJS() {
        return toJS()+"; return false;";
    }
    
    /**
     * returns a blank new instance where the url is set to this url
     * @return
     */
    public PrototypeAjax newInstance(){
        PrototypeAjax req = new PrototypeAjax();
        return req;
    }
    
    /** returns a deep copy of this instance (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        PrototypeAjax req;
        try {
            req = (PrototypeAjax) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Please report this should not happen");
        }
        req.completeCode = new HashMap(completeCode);
        req.options = new HashMap(options);
        req.requestHeaders = new HashMap(requestHeaders);
        return req;
    }
    
    public PrototypeAjax setOnActionLinkValue(ActionLink aL,Object value){
        setUrl(aL.getHref(value));
        aL.setAttribute("onClick", onClickJS());
        return this;
    }
    
    public PrototypeAjax setOnActionLink(ActionLink aL) {
        setOnActionLinkValue(aL,null);
        return this;
    }
    
    

    
    /** returns an empty string
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "";
    }



    /**
     * Gets the url this request leads to
     * @return
     */
    public String getUrl() {
        return url;
    }


    /**
     * Sets the url as string
     * @param url
     * @return
     */
    public PrototypeAjax setUrl(String url) {
        this.url = url;
        return this;
    }

}
