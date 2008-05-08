package net.sf.click.examples.page.general;

/**
 * Provides an navigation example Page demonstrating forward and redirect
 * page navigation. See NavigationA page for details.
 *
 * @author Malcolm Edgar
 */
public class NavigationB extends NavigationA {

    public String getTarget() {
        return "/general/navigation-a.htm";
    }
}
