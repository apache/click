package net.sf.click.sandbox.chrisichris.prototype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A utility class which creates JS script for the prototype Ajax calls. It does
 * nothing processing specific just create JS and can be used to setup parts of
 * the call in the page and parts in the template. It has verious properties for
 * all the prototype supported properties and a bit more.
 *
 * @author Christian Essl
 *
 */
public class AjaxRequest   {

    /**
     * Header send by the server if the AJAX response has JSON content and this
     * is evaluated.
     */
    public static final String JSON_SERVER_HEADER = "X-JSON";

    /**
     * if the content-type of the AJAX-response is "text/javascript" the script
     * will be evaluated.
     */
    public static final String CONTENT_TYPE_JS = "text/javascript";

    private String url;

    // ///////////////////////
    // options



    /**
     * JS function to execute when the AJAX request has returned succesful. the funciton
     * will get the transport (with responseText) and wheter json is send or not
     * onSucess
     */
    protected String onSuccess;

    /**
     * JS function to execute when AJAX request did not success. The function
     * 
     */
    protected String onFailure;

    /**
     * JS function to execute when the AJAX request was processed (either onSuccess or onFailure).
     */
    protected String onComplete;
    /**
     * wheter asychronouse AJAX.
     */
    protected boolean asynchronous = true;

    /**
     * The request parameters.
     */
    protected Map/* <String,String> */parameters = new HashMap();

    /**
     * JS expression of the element which to submit.
     */
    protected String postWith;

    /**
     * The id of a form or other html elemtn which contains form_elemnts if this
     * is given the parameters will be set to this.
     */
    protected String postForm;


    // ///////////////////////////////
    // code generation strings

    /**
     * Text to be shown in a confirm dialog before AJAX is done.
     */
    protected String confirm;

    /**
     * Condition for AJAX call (JS boolean expression).
     */
    protected String condition;

    /**
     * JS code executed directly after the AJAX call.
     */
    protected String after;

    /**
     * JS code executed directly before AJAX call.
     */
    protected String before;

    /**
     * Element id of a progress image which is shown when the AJAX request is
     * executing.
     */
    protected String progressImage;

    /**
     * Must set url before use.
     *
     */
    public AjaxRequest() {
        super();
    }

    /**
     * Constructor with aciton url.
     * @param url url to send the action to.
     */
    public AjaxRequest(String url) {
        this.setUrl(url);
    }

    /**
     * Gets the url this request is send to.
     *
     * @return actionurl
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url as string.
     *
     * @param url url to send the request to
     * @return this
     */
    public AjaxRequest setUrl(String url) {
        this.url = url;
        return this;
    }



    /**
     * Wheter the request should be send asynchronous or not default is true.
     *
     * @return the value
     */
    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * Wheter the request should be send asynchronous or not default is true.
     *
     * @param asynchronous
     *            true = asychnronouse
     * @return this
     */
    public AjaxRequest setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
        return this;
    }







    /**
     * The JS code to execute in case a failure response is reported by the
     * server.
     *
     * @return the coe
     */
    public String getOnFailure() {
        return onFailure;
    }

    /**
     * The JS code to execute in case a failure response is reported by the
     * server.
     *
     * @param onFailure
     *            the JS code
     * @return this
     */
    public AjaxRequest setOnFailure(String onFailure) {
        this.onFailure = onFailure;
        return this;
    }

    /**
     * The JS code to execute in case of succes of the response.
     *
     * @return the code
     */
    public String getOnSuccess() {
        return onSuccess;
    }

    /**
     * The JS code to execute in case of succes of the response.
     *
     * @param onSucess
     *            JS code
     * @return this
     */
    public AjaxRequest setOnSuccess(String onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }



    /**
     * @return Returns the postWith.
     */
    public String getPostWith() {
        return postWith;
    }

    /**
     * A JavaScript String expression which is appended to the the request
     * parameters. The resulting String takes the form of name=value...
     *
     * @param js
     *            string expression
     * @return this
     */
    public AjaxRequest setPostWith(String js) {
        postWith = js;
        return this;
    }

    /**
     * Sets the {@link #setPostWith(String)} parameter to
     * "Form.serialize(this.form)". Meaning that all the form-fields except
     * buttons are send as parameters of the form where the AJAX link or button
     * is included
     *
     * @return this
     */
    public AjaxRequest setPostWithCurrentForm() {
        return setPostWith("Form.serialize(this.form)");
    }


    /**
     * The id of the form or any other html element which contains input
     * elements which will be submit as the part of this request.
     *
     * @return Returns id of the html element.
     */
    public String getPostFormId() {
        return postForm;
    }

    /**
     * The id of the form or any other html element which contains input
     * elements which will be submit as the part of this request.
     *
     * @param elementId
     *            html element id
     * @return this
     */
    public AjaxRequest setPostFormId(String elementId) {
        postForm = elementId;
        return this;
    }
    /**
     * Returns the map of parameters to be set.
     *
     * @return map
     */
    public Map/* <String,String> */getParamters() {
        if (parameters == null) {
            parameters = new HashMap/* <String, String> */();
        }
        return parameters;
    }
    
    public boolean hasParameters() {
        return parameters != null && !parameters.isEmpty();
    }
    /**
     * Clears all parameters.
     *
     * @return this
     */
    public AjaxRequest clearParameters() {
        parameters = null;
        return this;
    }


    /**
     * Adds the given parameter string. If value is null the paramter is
     * removed. The value is the plain string which will be send as a parameter
     * the string will be URLEncoded.
     *
     * @param name
     *            of the parameter
     * @param value
     *            String value of the paramter or null
     * @return this
     */
    public AjaxRequest addParameter(String name, String value) {
        if (name == null) {
            throw new NullPointerException("No name");
        }
        if (value == null) {
            if (hasParameters()) {
                getParamters().remove(name);
            }
        } else {
            value = StringEscapeUtils.escapeJavaScript(value);
            value = "'" + value + "'";
            getParamters().put(name, value);
        }
        return this;
    }

    /**
     * Adds a parameter to request, where the value is at request sendtime
     * caculated as JS.
     *
     * @param name
     *            of the parameter (normal string)
     * @param valueJS
     *            JS to caculate the value of the parameter (JS-String have to be ' quotes)
     * @return this
     */
    public AjaxRequest addJSParameter(String name, String valueJS) {
        if (name == null) {
            throw new NullPointerException("No name param");
        }
        name = name.trim();
        if (valueJS == null) {
            if (hasParameters()) {
                getParamters().remove(name);
            }
        } else {
            getParamters().put(name, valueJS);
        }
        return this;
    }
    
    public AjaxRequest addFieldParameter(String paraName, String fieldId) {
        if(StringUtils.isBlank(fieldId)) {
            throw new IllegalArgumentException("fieldId is blank");
        }
        addJSParameter(paraName, "$F($('"+fieldId+"'))");
        return this;
    }



    /**
     * JS code executed when the AJAX Request is completed.
     *
     * @return code
     */
    public String getOnComplete() {
        String ret = onComplete;
        if (progressImage != null) {
            String prS = "Element.hide('" + progressImage + "');";
            if (ret == null) {
                return prS;
            } else {
                ret = prS + ret;
            }
        }
        return ret;
    }

    /**
     * JS code to execute when the AJAX Request is completed.
     *
     * @param onComplete
     *            JS code
     * @return this
     */
    public AjaxRequest setOnComplete(String onComplete) {
        this.onComplete = onComplete;
        return this;
    }



    /**
     * JS Code to execute right after the AJAX request was initiated and before
     * onloading.
     *
     * @return JS code
     */
    public String getAfter() {
        return after;
    }

    /**
     * JS Code to execute right after the AJAX request was initiated and before
     * onloading.
     *
     * @param after
     *            JS code
     * @return this
     */
    public AjaxRequest setAfter(String after) {
        this.after = after;
        return this;
    }

    /**
     * JS Code to execute before the AJAX request is initiated (used ie to check
     * wheter a Ajax request should be made at all).
     *
     * @return JS Code
     */
    public String getBefore() {
        return before;
    }

    /**
     * JS Code to execute before the AJAX request is initiated (used ie to check
     * wheter a Ajax request should be made at all).
     *
     * @param before
     *            JS code
     * @return this
     */
    public AjaxRequest setBefore(String before) {
        this.before = before;
        return this;
    }

    /**
     * JS boolean expression which describes wheter an AJAX call should be made
     * at all.
     *
     * @return JS code
     */
    public String getCondition() {
        return condition;
    }

    /**
     * JS boolean expression which describes wheter an AJAX call should be made
     * at all.
     *
     * @param condition
     *            the Js expression
     * @return this
     */
    public AjaxRequest setCondition(String condition) {
        this.condition = condition;
        return this;
    }

    /**
     * Text to be shown in a confirm dialog before a request is done.
     *
     * @return dialog text
     */
    public String getConfirm() {
        return confirm;
    }

    /**
     * Text to be shown in a confirm dialog before a request is done.
     *
     * @param confirm text to show
     * @return this
     */
    public AjaxRequest setConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }

    /**
     * Element id of an image which is shown while the AJAX request is running.
     *
     * @return Returns the progressImage.
     */
    public String getProgressImage() {
        return progressImage;
    }

    /**
     * Element id of an html-element which should be shown while
     * the AJAX request is running. Typically some animated gif.
     *
     * @param elementId html-element id
     * @return this
     */
    public AjaxRequest setProgressImage(String elementId) {
        this.progressImage = elementId;
        return this;
    }

    /**
     * Creates the JS options for the Ajax Request without paranteses.
     *
     * @return JS map without parantesis
     */
    protected String createJSOptions(HtmlStringBuffer stB) {

        // general things
        stB.append("asynchronous : ");
        stB.append(asynchronous ? "true, " : "false, ");

        stB.append("method : 'post', ");

        // callbacks
        if(getOnSuccess() != null) {
            stB.append(", onSuccess: function(request,json) {");
            stB.append(getOnSuccess());
            stB.append("}");
        }
        
        if(getOnFailure() != null) {
            stB.append(", onFailure: function(request,json) {");
            stB.append(getOnFailure());
            stB.append("}");
        }
        
        // onComplete
        String onComplete = getOnComplete();
        if (onComplete != null) {
            stB.append(", onComplete : function(request,joson){");
            stB.append(onComplete);
            stB.append("}");
        }


        // parameters
        stB.append(", ");
        stB.append("parameters: ");
        stB.append(getParametersOption());
        return stB.toString();
    }

    /**
     * Returns the options representaiton of all Parameters.
     *
     * @return one JS option
     */
    protected String getParametersOption() {
        
        HtmlStringBuffer stB = new HtmlStringBuffer();
        stB.append("'_clickAjax=true'");
        
        //normal parameters
        if (hasParameters()) {
            for (Iterator it = getParamters().entrySet().iterator();
                            it.hasNext();) {
                Map.Entry/* < String, String> */para = (Map.Entry) it.next();
                stB.append("+'&'+");
                stB.append("encodeURIComponent('");
                stB.append(para.getKey());
                stB.append("')+'='+encodeURIComponent(");
                stB.append(para.getValue());
                stB.append(')');
            }
        }
        
        //form to post
        if (getPostFormId() != null) {
            stB.append(" + '&' + ");
            stB.append("Form.serialize(document.getElementById('");
            stB.append(getPostFormId());
            stB.append("'))");
        }
        
        //abitrary javascript to append
        if (getPostWith() != null) {
            stB.append(" + '&' + ");
            stB.append(getPostWith());
        }
        return stB.toString();
    }

    /**
     * create the JS for the AJAX call.
     *
     * @return JS
     */
    public String toJS() {
        String url = getUrl();
        if (url == null) {
            throw new IllegalStateException("No url set");
        }
        HtmlStringBuffer stB = new HtmlStringBuffer();

        stB.append("new Ajax.Request(");
        // the url
        stB.append("'");
        stB.append(url);
        stB.append("', {");

        // the params
        createJSOptions(stB);
        stB.append("})");

        String function = stB.toString();

        if (progressImage != null) {
            function = "Element.show('" + progressImage + "');" + function;
        }

        if (before != null) {
            function = before + ";" + function;
        }

        if (after != null) {
            function = function + "; " + after;
        }

        if (condition != null) {
            function = "if (" + condition + ") {" + function + ";}";
        }

        if (confirm != null) {
            function = "if (confirm('"
                    + StringEscapeUtils.escapeJavaScript(confirm) + "')) {"
                    + function + ";}";
        }

        return function;

    }



    /**
     * returns an empty string.
     *
     * @return ""
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return toJS();
    }


}
