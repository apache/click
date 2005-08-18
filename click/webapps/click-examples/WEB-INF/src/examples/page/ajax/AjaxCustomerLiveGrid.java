package examples.page.ajax;

import java.util.List;

import examples.domain.CustomerDAO;
import net.sf.click.Page;

/**
 * Retrieves the current page and "buffered" Customer list using the given offset
 * and page_size parameters and adds them to the Page model.
 * <p/>
 * This Page class is configured to return its content type as "text/xml". Note
 * this can also be done by configuring a &lt;header&gt; element:
 * <p/>
 * <pre class="codeConfig">
 * &lt;page path="ajax-customer-live-grid.htm" classname="examples.page.AjaxCustomerLiveGrid"&gt;
 * &lt;header name="Content-Type" value="text/xml"/&gt;
 * &lt;/page&gt;
 * </pre>
 *
 * @author Phil Barnes
 */
public class AjaxCustomerLiveGrid extends Page {

	/**
	 * Process the AJAX request and return XML customer table.  This method
	 * retreives the "buffered" results for the live grid component, starting
	 * with the current displayed page + the "page_size" number of additional
	 * results for smooth scrolling and instant result display purposes
	 *
	 * @see net.sf.click.Page#onGet()
	 */
	public void onGet() {
		String offset = getContext().getRequest().getParameter("offset");
		String pageSize = getContext().getRequest().getParameter("page_size");

		if (offset != null && pageSize != null) {
			List customers = CustomerDAO.findCustomersByPage(
					Integer.parseInt(offset),
					Integer.parseInt(pageSize));
			// add the BUFFERED paginated results to the model for XML response
			addModel("customers", customers);
			// add the offset back so we know where to start numbering the results
			addModel("offset", new Integer(offset));
		}

	}

	/**
	 * Ensure the Http response Content-type is "text/xml".
	 *
	 * @see net.sf.click.Page#getContentType()
	 */
	public String getContentType() {
		return "text/xml";
	}
}
