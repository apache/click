/*
 * Copyright 2004-2008 Malcolm A. Edgar
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
package net.sf.click.extras.control;

import net.sf.click.control.FieldSet;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides an HTML FieldSet container to create composite based fieldsets:
 * &nbsp; &lt;fieldset&gt;.
 * <p/>
 * {@link net.sf.click.control.FieldSet} uses an HTML Table to layout its fields
 * and controls. HtmlFieldSet on the other hand does not provide a layout and
 * instead relies on the developer to compose a layout programmatically.
 * <p/>
 * This allows developers to provide a more flexible and CSS friendly layout.
 * <p/>
 * See this <a href="example">example</a> of how HtmlFieldSet is used to provide
 * a custom layout.
 *
 * @author Bob Schellink
 */
public class HtmlFieldSet extends FieldSet {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    // --------------------------------------------------------- Public Methods

    /**
     * Render the HTML representation of the FieldSet.
     * <p/>
     * If FieldSet is contained within a {@link Form} instance, this method will
     * delegate rendering to {@link Form#renderFieldSet(net.sf.click.util.HtmlStringBuffer, net.sf.click.control.FieldSet)}.
     * <p/>
     * The size of buffer is determined by {@link #getControlSizeEst()}.
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {

        if (getShowBorder()) {
            renderTagBegin(getTag(), buffer);
            buffer.closeTag();
            if (hasControls()) {
                buffer.append("\n");
            }

            renderContent(buffer);

            renderTagEnd(getTag(), buffer);
        } else {
            renderChildren(buffer);
        }
    }

    /**
     * Returns the HTML representation of the FieldSet.
     * <p/>
     * The rendering of the FieldSet is delegated to
     * {@link #render(net.sf.click.util.HtmlStringBuffer)}. The size of buffer
     * is determined by {@link #getControlSizeEst()}.
     *
     * @see Object#toString()
     *
     * @return the HTML representation of this control
     */
    public String toString() {
        return super.toString();
    }

    //-------------------------------------------- protected methods

    /**
     * @see AbstractControl#renderTagBegin(java.lang.String, net.sf.click.util.HtmlStringBuffer)
     *
     * @param tagName the name of the tag to render
     * @param buffer the buffer to append the output to
     */
    protected void renderTagBegin(String tagName, HtmlStringBuffer buffer) {
        if (tagName == null) {
            throw new IllegalStateException("Tag cannot be null");
        }

        buffer.elementStart(tagName);

        String id = getId();
        if (id != null) {
            buffer.appendAttribute("id", id);
        }

        appendAttributes(buffer);
    }

    /**
     * @see AbstractContainer#renderContent(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        String fsLegend = getLegend();
        if (fsLegend != null && fsLegend.length() > 0) {
            buffer.elementStart("legend");
            if (hasLegendAttributes()) {
                Object legendId = getLegendAttributes().get("id");
                if (legendId != null) {
                    buffer.appendAttribute("id", legendId);
                }
                buffer.appendAttributes(getLegendAttributes());
            }
            buffer.closeTag();
            buffer.append(fsLegend);
            buffer.elementEnd("legend");
            buffer.append("\n");
        }
        super.renderContent(buffer);
    }

}
