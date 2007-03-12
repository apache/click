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

import org.apache.commons.lang.StringUtils;

import net.sf.click.util.HtmlStringBuffer;

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
     * The image path src attribute. If the src value is prefixed with
     * '/' then the request context path will be prefixed to the src value when
     * rendered by the control.
     */
    protected String src;

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
     * If the src path value is prefixed with "/" the request context path will
     * be prefixed to the src value when rendered in the control.
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
     * Return the image src path attribute.  If the src value is prefixed with
     * '/' then the request context path will be prefixed to the src value when
     * rendered by the control.
     *
     * @return the image src path attribute
     */
    public String getSrc() {
        return src;
    }

    /**
     * Set the image src path attribute. If the src value is prefixed with
     * '/' then the request context path will be prefixed to the src value when
     * rendered by the control.
     *
     * @param src the image src path attribute
     */
    public void setSrc(String src) {
        this.src = src;
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


    /**
     * Return a HTML rendered ImageButton string.
     *
     * @see Object#toString()
     *
     * @return a HTML rendered Button string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(40);

        buffer.elementStart("input");

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("value", getLabel());
        buffer.appendAttribute("title", getTitle());

        String src = getSrc();
        if (StringUtils.isNotBlank(src)) {
            if (src.charAt(0) == '/') {
                src = getContext().getRequest().getContextPath() + src;
            }
            buffer.appendAttribute("src", src);
        }

        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }
        if (hasStyles()) {
            buffer.appendStyleAttributes(getStyles());
        }

        buffer.elementEnd();

        return buffer.toString();
    }
}
