/*
 * Copyright 2004-2006 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 */
public class ImageSubmit extends Submit {

    private static final long serialVersionUID = 1L;

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
     * Create a ImageSubmit button with the given name.
     *
     * @param name the button name
     */
    public ImageSubmit(String name) {
        super(name);
    }

    /**
     * Create a ImageSubmit button with the given name and image src path.
     *
     * @param name the button name
     * @param src the image src path attribute
     */
    public ImageSubmit(String name, String src) {
        super(name);
        setSrc(src);
    }

    /**
     * Create an ImageSubmit button with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public ImageSubmit() {
        super();
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
     * Bind the request submission, setting the field {@link Submit#clicked},
     * {@link #x} and {@link #y} if defined in the request.
     */
    public void bindRequestValue() {

        //  Note IE does not submit name
        String xValue = getContext().getRequestParameter(getName() + ".x");

        if (xValue != null) {
            this.clicked = true;

            try {
                this.x = Integer.parseInt(xValue);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            String yValue = getContext().getRequestParameter(getName() + ".y");
            try {
                this.y = Integer.parseInt(yValue);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
    }
}
