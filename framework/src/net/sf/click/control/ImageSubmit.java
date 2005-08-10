package net.sf.click.control;

import net.sf.click.util.ClickUtils;

/**
 * Provides an ImageSubmit control: &nbsp; &lt;input type='image'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <input type='image' value='img' title='ImageSubmit Control' src='images/myButtonImage.jpg'/>
 * </td></tr>
 * </table>
 *
 * TODO
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Phil Barnes
 * $Id$
 */
public class ImageSubmit extends Submit {

    // ----------------------------------------------------- Instance Variables

    /** The pixel x coordinate clicked on by the user. */
    protected int x;

    /** The pixel y coordinate clicked on by the user. */
    protected int y;

    // ----------------------------------------------------------- Constructors

    public ImageSubmit(String value) {
        super(value);
    }

    public ImageSubmit(String value, String imageSource) {
        super(value);
        setSrc(imageSource);
    }

    // ------------------------------------------------------ Public Attributes

    public String getType() {
        return "image";
    }

    public String getSrc() {
        return getAttribute("src");
    }

    public void setSrc(String src) {
        setAttribute("src", src);
    }

    /**
     * Return the image x pixel coordinate clicked on by the user.
     *
     * @return the image x pixel coordinate clicked on by the user
     */
    public int getX() {
        return x;
    }
 
    /**
     * Return the image y pixel coordinate clicked on by the user.
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
     * If the submit button is clicked and a Control listener is defined, the
     * listener method will be invoked and its boolean return value will be
     * returned by this method.
     * <p/>
     * Submit buttons will be processed after all the non Button Form Controls
     * have been processed. Submit buttons will be processed in the order
     * they were added to the Form.
     *
     * TODO: update doco
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        String value = getContext().getRequestParameter(getName());

        if (value != null) {
            clicked = getName().equals(ClickUtils.toName(value));

            String xValue = getContext().getRequestParameter(getName() + ".x");
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
