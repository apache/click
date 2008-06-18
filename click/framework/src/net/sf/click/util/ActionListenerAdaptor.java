package net.sf.click.util;

import java.io.Serializable;

import net.sf.click.ActionListener;
import net.sf.click.Control;

/**
 * Provides an ActionListener adaptor instance.
 *
 * @author Malcolm Edgar
 */
public class ActionListenerAdaptor implements ActionListener, Serializable {

	private static final long serialVersionUID = 1L;

	/** The target listener object. */
    protected final Object listener;

    /** The target listener method name. */
    protected final String method;

    /**
     * Create an ActionListener adaptor instance for the given listener target
     * object and listener method.
     *
     * @param target the listener object
     * @param method the target listener method name
     */
    public ActionListenerAdaptor(Object target, String method) {
        this.listener = target;
        this.method = method;
    }

    /**
     * @see ActionListener#onAction(Control)
     *
     * @param source the source of the action event
     * @return true if control and page processing should continue or false
     * otherwise.
     */
    public boolean onAction(Control source) {
        return ClickUtils.invokeListener(listener, method);
    }

}
