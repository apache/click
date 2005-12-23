package click.cayenne.page;

import net.sf.click.control.ActionLink;

import java.util.List;

/**
 * Provides a Persons viewer page with an ActonLink to create new
 * persons.
 *
 */
public class PersonsViewer  extends BorderedPage {

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        addModel("head-include", "ajax-head-person.htm");
        addModel("body-onload", "registerAjaxStuff();");

        addControl(new ActionLink("newLink", this, "onNewClick"));
    }

    /**
     * Perform a configured "PersonSearch" and add the results to the pages
     * model for display.
     *
     * @see net.sf.click.Page#onGet()
     */
    public void onGet() {
        List personList =
            getDataContext().performQuery("PersonSearch", true);
        addModel("persons", personList);
    }

    /**
     * Handle the create new Person click, forwarding to the
     * <tt>PersonEditor</tt> page.
     *
     * @return false
     */
    public boolean onNewClick() {
        setForward("person-editor.htm");
        return false;
    }

}
