package net.sf.click.control;


/**
 * Provides an ImageSubmit control: &nbsp; &lt;input type='image' src='edit.gif'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <input type='image' value='img' title='ImageSubmit Control' src='image-submit.gif'/>
 * </td></tr>
 * </table>
 *
 * The ImageSubmit control is useful for creating custom form buttons. This
 * control can also be used for creating image areas where the user clicks on
 * a point on the image and the clicked x and y coordinates are submitted
 * with the name of the control.
 * <p/>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @see Submit
 * @see Button
 *
 * @author Phil Barnes
 * @author Malcolm Edgar
 * @version $Id$
 */
public class ImageSubmit extends Submit {

    // ----------------------------------------------------- Instance Variables

    /**
     * The image pixel x coordinate clicked on by the user, the default value
     * is -1. A value of -1 which means the value has not been set.
     */
    protected int x = -1;

    /**
     * The image pixel y coordinate clicked on by the user, the default value
     * is -1.  A value of -1 means the value has not been set.
     */
    protected int y = -1;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a ImageSubmit button with the given value. The value cannot
     * contain the HTML characters <tt>&amp;nbsp;</tt> as the submitted value
     * cannot be processed correctly.
     * <p/>
     * The field name will be Java property representation of the given value.
     *
     * @param value the button value
     */
    public ImageSubmit(String value) {
        super(value);
    }

    /**
     * Create a ImageSubmit button with the given value and image src path.
     * The value cannot contain the HTML characters <tt>&amp;nbsp;</tt> as the
     * submitted value cannot be processed correctly.
     * <p/>
     * The field name will be Java property representation of the given value.
     *
     * @param value the button value
     * @param src the image src path attribute
     */
    public ImageSubmit(String value, String src) {
        super(value);
        setSrc(src);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the input type: '<tt>image</tt>'.
     *
     * @return the input type: '<tt>image</tt>'
     */
    public String getType() {
        return "image";
    }

    /**
     * Return the image src path attribute.
     *
     * @return the image src attribute
     */
    public String getSrc() {
        return getAttribute("src");
    }

    /**
     * Set the image src path attribute.
     *
     * @param src the image src attribute
     */
    public void setSrc(String src) {
        setAttribute("src", src);
    }

    /**
     * Return the image x pixel coordinate clicked on by the user. The x
     * pixel value will be set after control has been processed. The default x
     * value is -1 which means the value has not been set.
     *
     * @return the image x pixel coordinate clicked on by the user
     */
    public int getX() {
        return x;
    }
 
    /**
     * Return the image y pixel coordinate clicked on by the user. The y
     * pixel value will be set after control has been processed. The default y
     * value is -1 which means the value has not been set.
     *
     * @return the image y pixel coordinate clicked on by the user
     */
    public int getY() {
        return y;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the submit event and return true to continue event processing.
     * <p/>
     * If the image is clicked and a Control listener is defined, the
     * listener method will be invoked and its boolean return value will be
     * returned by this method.
     * <p/>
     * Submit button controls will be processed after all the non Button
     * Controls have been processed. Submit buttons will be processed in the
     * order they were added to the Form.
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        //  Note IE does not submit name
        String xValue = getContext().getRequestParameter(getName() + ".x");

        if (xValue != null) {
            clicked = true;

            try {
                x = Integer.parseInt(xValue);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            String yValue = getContext().getRequestParameter(getName() + ".y");
            try {
                y = Integer.parseInt(yValue);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            if (clicked) {
                return invokeListener();
            }
        }

        return true;
    }
}
