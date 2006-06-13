package net.sf.click.sandbox.chrisichris.control;

import java.util.List;
import java.util.Map;

import net.sf.click.Control;

/**
 * Version of {@link Container} where the various add metods are public.
 * @author Christian
 *
 */
public class Container extends Composite{

    public Container() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Container(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    protected void addControl(Control control) {
        // TODO Auto-generated method stub
        super.addControl(control);
    }

    protected void addModel(String name, Object value) throws IllegalArgumentException, NullPointerException {
        // TODO Auto-generated method stub
        super.addModel(name, value);
    }

    protected Control getControl(String name) {
        // TODO Auto-generated method stub
        return super.getControl(name);
    }

    protected List getControls() {
        // TODO Auto-generated method stub
        return super.getControls();
    }

    protected Map getModel() {
        // TODO Auto-generated method stub
        return super.getModel();
    }

    protected Object getModel(String name) {
        // TODO Auto-generated method stub
        return super.getModel(name);
    }

    protected boolean hasControls() {
        // TODO Auto-generated method stub
        return super.hasControls();
    }

    protected boolean removeControl(Control ctrl) {
        // TODO Auto-generated method stub
        return super.removeControl(ctrl);
    }

    protected boolean removeControl(String name) {
        // TODO Auto-generated method stub
        return super.removeControl(name);
    }

    protected Object removeModel(String name) {
        // TODO Auto-generated method stub
        return super.removeModel(name);
    }

}
