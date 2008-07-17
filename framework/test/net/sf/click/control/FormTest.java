package net.sf.click.control;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.Page;
import net.sf.click.servlet.MockRequest;

/**
 * @author Bob Schellink
 */
public class FormTest extends TestCase {

    /**
     * CLK-267. Test that it is not possible to add a duplicate SUBMIT_CHECK
     * HiddenField.
     */
    public void testDuplicateOnSubmitCheck() {
        MockContext context = MockContext.initContext("test-form.htm");
        MockRequest request = (MockRequest) context.getMockRequest();
        request.setParameter("form_name", "form");

        Page page = new Page();

        // Set the page to stateful
        page.setStateful(true);
        Form form = new Form("form");

        // Construct name of submit token
        String submitCheckName = Form.SUBMIT_CHECK + form.getName() + "_" + context.getResourcePath();

        // Simulate a submit check
        boolean valid = form.onSubmitCheck(page, "/invalid-submit.html");
        Assert.assertTrue(valid);

        // Assert that the submitCheck hidden field was created
        Field submitCheckField = form.getField(submitCheckName);
        Assert.assertNotNull(submitCheckField);

        // Add submitCheckField as a request parameter
        request.getParameterMap().put(Form.SUBMIT_CHECK + form.getName() + "_" + context.getResourcePath(), submitCheckField.getValue());
        
        // Simulate a second submit check.
        valid = form.onSubmitCheck(page, "/invalid-submit.html");
        
        // Assert the second onSubmitCheck did succeeded as well.
        Assert.assertTrue(valid);
    }

    /**
     * CLK-289. Test Form#onSubmitCheck when the SUBMIT_CHECK parameter is
     * missing.
     */
    public void testOnSubmitCheckMissingParam() {
        MockContext context = (MockContext) MockContext.initContext("test-form.htm");
        MockRequest request = (MockRequest) context.getMockRequest();
        request.getParameterMap().put("form_name", "form");
        Page page = new Page();
        Form form = new Form("form");

        // Construct name of submit token
        String submitTokenName = Form.SUBMIT_CHECK + form.getName() + "_" + context.getResourcePath();

        // Ensure there are no submitCheck hidden field yet
        Field submitCheckField = form.getField(submitTokenName);
        Assert.assertNull(submitCheckField);

        // Simulate a submit check
        boolean valid = form.onSubmitCheck(page, "/invalid-submit.html");
        Assert.assertTrue(valid);

        // Add the submitCheckField name and value to the parameters.
        submitCheckField = form.getField(submitTokenName);
        request.setParameter(submitTokenName, submitCheckField.getValue());

        // If we submit again, the assert should be true because the submit
        // token is set in the request parameters.
        valid = form.onSubmitCheck(page, "/invalid-submit.html");
        Assert.assertTrue(valid);
        
        // Now imagine the SUBMIT_CHECK token is removed by a hacker. To simulate
        // such a scenario we remove the submitTokenName from the request paramters.
        request.removeParameter(submitTokenName);
        valid = form.onSubmitCheck(page, "/invalid-submit.html");
        Assert.assertFalse(valid);
    }

    /**
     * Test that form processing binds a request parameter to a field value.
     */
    public void testFormOnProcess() {
        // Create a mock context
        MockContext context = (MockContext) MockContext.initContext("test-form.htm");
        MockRequest request = (MockRequest) context.getMockRequest();

        // The request value that should be set as the textField value
        String requestValue = "one";

        // Set form name and field name parameters
        request.setParameter("form_name", "form");
        request.setParameter("name", requestValue);

        // Create form and fields
        Form form = new Form("form");
        TextField nameField = new TextField("name");
        form.add(nameField);

        // Check that nameField value is null
        Assert.assertNull(nameField.getValueObject());

        // Simulate a form onProcess callback
        form.onProcess();
        
        // Check that nameField value is now bound to request value
        Assert.assertEquals(requestValue, nameField.getValueObject());
    }
}
