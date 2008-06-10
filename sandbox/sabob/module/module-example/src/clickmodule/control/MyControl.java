package clickmodule.control;

import javax.servlet.ServletContext;
import net.sf.click.control.AbstractControl;

/**
 *
 * @author Bob Schellink
 */
public class MyControl extends AbstractControl {

    public String getHtmlImports() {
        return null;
    }

    public void onDeploy(ServletContext servletContext) {
        
    }

    public void onDestroy() {
        
    }

    public void onInit() {
        
    }

    public boolean onProcess() {
        return true;
    }

    public void onRender() {
        
    }

    public void setListener(Object listener, String method) {
        
    }

}
