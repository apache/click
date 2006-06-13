package net.sf.click.sandbox.chrisichris.control;

import org.apache.commons.lang.StringEscapeUtils;

import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

public class ChildActionLink extends ActionLink {

    public ChildActionLink() {
        super();
    }

    public ChildActionLink(String name, Object listener, String method) {
        super(name, listener, method);
    }

    public ChildActionLink(String name, String label, Object listener, String method) {
        super(name, label, listener, method);
    }

    public ChildActionLink(String name, String label) {
        super(name, label);
    }

    public ChildActionLink(String name) {
        super(name);
    }
    
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");
        } else {
            String id = ControlUtils.getId(this);
            if(id != null) {
                setAttribute("id",id);
            }
            return id;
        }
    }
    
    /**
     * Return the ActionLink anchor &lt;a&gt; tag href attribute for the
     * given value. This method will encode the URL with the session ID
     * if required using <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @param value the ActionLink value parameter
     * @return the ActionLink HTML href attribute
     */
    public String getHref(Object value) {
        StatePage sP = StatePage.getStatePage(this);
        if(sP == null) {
            String uri = getContext().getRequest().getRequestURI();

            HtmlStringBuffer buffer =
                new HtmlStringBuffer(uri.length() + getName().length() + 40);

            buffer.append(uri);
            buffer.append("?");
            buffer.append(ACTION_LINK);
            buffer.append("=");
            buffer.append(getId());
            if (value != null) {
                buffer.append("&");
                buffer.append(VALUE);
                buffer.append("=");
                buffer.append(ClickUtils.encodeUrl(value, getContext()));
            }

            return getContext().getResponse().encodeURL(buffer.toString());
        } else {
            String sId = StringEscapeUtils.escapeJavaScript(sP.getStateForm().getId());
            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append("javascript: document.getElementById('");
            buffer.append(sId);
            buffer.append("').elements['");
            buffer.append(ACTION_LINK);
            buffer.append("'].value = '");
            buffer.append(StringEscapeUtils.escapeJavaScript(getId()));
            buffer.append("';");
            
            if(value != null) {
                buffer.append(" document.getElementById('");
                buffer.append(sId);
                buffer.append("').elements['");
                buffer.append(VALUE);
                buffer.append("'].value = '");
                buffer.append(StringEscapeUtils.escapeJavaScript(value.toString()));
                buffer.append("';");
            }
            
            buffer.append(" document.getElementById('");
            buffer.append(sId);
            buffer.append("').submit();");
            
            String ret = buffer.toString();
            return ret;
        }
    }

    /**
     * This method will set the {@link #isClicked()} property to true if the
     * ActionLink was clicked, and if an action callback listener was set
     * this will be invoked.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        clicked =
            getId().equals(getContext().getRequestParameter(ACTION_LINK));

        if (clicked) {
            setValue(getContext().getRequestParameter(VALUE));
            return onAction();

        } else {
            return true;
        }
    }
    
    protected boolean onAction() {
        if (listener != null && listenerMethod != null) {
            return ClickUtils.invokeListener(listener, listenerMethod);

        } else {
            return true;
        }
    }

    

}
