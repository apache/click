/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.click.control;

import java.util.List;
import java.util.Map;
import net.sf.click.control.Container;
import net.sf.click.Control;
import net.sf.click.util.HtmlStringBuffer;

/**
 *
 * @author Bob Schellink
 */
public class ContainerField extends Field implements Container {
    
    private AbstractContainer container = new AbstractContainer() {
    };

    // ------------------------------------------------------ Constructorrs
    
    public ContainerField() {
    }

    public ContainerField(String name) {
        super(name);
    }

    // ------------------------------------------------------ Public methods

    public Control addControl(Control control) {
        return container.addControl(control);
    }

    public boolean removeControl(Control control) {
        return container.removeControl(control);
    }

    public Control getControl(String controlName) {
        return container.getControl(controlName);
    }

    public boolean contains(Control control) {
        return container.contains(control);
    }

    public List getControls() {
        return container.getControls();
    }

    public boolean hasControls() {
        return container.hasControls();
    }

    public boolean onProcess() {
        return container.onProcess();
    }

    public String getHtmlImportsAll() {
        return container.getHtmlImportsAll();
    }

    public void onDestroy() {
        container.onDestroy();
    }

    public void onInit() {
        container.onInit();
    }

    public void onRender() {
        container.onRender();
    }

    public void render(HtmlStringBuffer buffer) {
        container.render(buffer);
    }

    public String toString() {
        return container.toString();
    }

    //-------------------------------------------- protected methods

    protected Map getControlMap() {
        return container.getControlMap();
    }

    protected int getControlSizeEst() {
        return container.getControlSizeEst();
    }

}
