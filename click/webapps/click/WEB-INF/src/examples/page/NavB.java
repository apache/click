package examples.page;


/**
 * Provides an navigation example Page demonstrating forward and redirect 
 * page navigation. See NavA page for details.
 * 
 *
 * @author Malcolm Edgar
 */
public class NavB extends NavA {
    
    public String getTarget() {
        return "navigation-a.htm";
    }
}
