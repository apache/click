package net.sf.click.examples.control;

import java.util.Date;

import javax.servlet.ServletContext;

import net.sf.click.control.Form;
import net.sf.click.control.Panel;
import net.sf.click.extras.control.DateField;
import net.sf.click.util.ClickUtils;

/**
 * Provides a custom date FilterPanel.
 *
 * @author Malcolm Edgar
 */
public class FilterPanel extends Panel {

    private static final long serialVersionUID = 1L;
    
    private Form filterForm = new Form("filterForm");
    private DateField startDate = new DateField("startDate");
    private DateField endDate = new DateField("endDate");

    public FilterPanel() {
        startDate.setFormatPattern("dd MMM yyyy");
        startDate.setSize(11);

        endDate.setFormatPattern("dd MMM yyyy");
        endDate.setSize(11);

        filterForm.add(startDate);
        filterForm.add(endDate);

        addControl(filterForm);
    }
    
    public Date getEndDate() {
        return endDate.getDate();
    }

    public Date getStartDate() {
        return startDate.getDate();
    }

    /**
     * @see net.sf.click.Control#onDeploy(ServletContext)
     */
    public void onDeploy(ServletContext servletContext) {
        String[] files = new String[] {
            "/net/sf/click/examples/control/twistie-down.png",
            "/net/sf/click/examples/control/twistie-up.png"
        };

        ClickUtils.deployFiles(servletContext, files, "images");
    }

}
