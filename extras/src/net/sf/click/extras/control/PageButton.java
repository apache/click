package net.sf.click.extras.control;

import net.sf.click.control.PageLink;
import net.sf.click.util.HtmlStringBuffer;

/**
 * PageButton Control, representing a <i>link</i> to a Page that is rendered in form of a <code>button</code>
 * (an <code>input</code> HTML element with the <code>type="button"</code> more precisely) plus javascript
 * getting the borwser to the location of that Page.
 * <p/>
 * <b>Note:</b> this control is not related at all to {@link net.sf.click.extras.control.PageSubmit} but
 * to {@link PageLink}.
 * <p/> 
 * <b>Advantages:</b>
 * <ul>
 *      <li>over {@link net.sf.click.control.Button Button} + direct javascript trick:
 *          <ul>
 *               <li>encoding of parameters works correctly due to the {@link PageLink} it extends.</li>
 *          </ul>
 *      </li>
 *      <li>over {@link PageLink}:
 *          <ul>
 *               <li>crawlers/spiders (or other browser plug-ins) won't follow the target since it's a button.</li>
 *               <li>the look and feel is native - much better than half-working CSS hacks
 *                  to simulate buttons with <code>Link</code> elements.</li>
 *          </ul>
 *      </li>
 *      <li>over {@link net.sf.click.control.ActionButton ActionButton} + forward/redirect trick:
 *          <ul>
 *               <li>there's no roundtrip to the server.</li>
 *               <li><code>Page#redirect(Class) does not support parameters</code> and <code>Page#redirect(String)</code> is
 *                   prone to errors when hardcoding them in the <code>String</code>.</li>
 *          </ul>
 *      </li>
 * </ul>
 *
 * @author Ahmed Mohombe
 */
public class PageButton extends PageLink {

    /**
     * Create a PageButton for the given name.
     *
     * @param name the page button name
     * @throws IllegalArgumentException if the name is null
     */
    public PageButton(String name) {
        super(name);
    }

    /**
     * Create a PageButton for the given name and target Page class.
     *
     * @param name the page button name
     * @param targetPage the target page class
     * @throws IllegalArgumentException if the name is null
     */
    public PageButton(String name, Class targetPage) {
        super(name, targetPage);
    }

    /**
     * Create a PageButton for the given name, label and target Page class.
     *
     * @param name       the page button name
     * @param label      the page button label
     * @param targetPage the target page class
     * @throws IllegalArgumentException if the name is null
     */
    public PageButton(String name, String label, Class targetPage) {
        super(name, label, targetPage);
    }

    /**
     * Create a PageButton for the given target Page class.
     *
     * @param targetPage the target page class
     * @throws IllegalArgumentException if the name is null
     */
    public PageButton(Class targetPage) {
        super(targetPage);
    }

    /**
     * Create a PageButton with no name defined.
     * <p/>
     * <b>Please note</b> the control's name and target pageClass must be
     * defined before it is valid.
     */
    public PageButton() {
    }

    /**
     * Return a HTML rendered Button string. Note the button label is rendered
     * as the HTML "value" attribute.
     *
     * @see Object#toString()
     *
     * @return a HTML rendered Button string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(40);

        buffer.elementStart("input");

        buffer.appendAttribute("type", "button");
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("value", getLabel());
        buffer.appendAttribute("title", getTitle());
        if (getTabIndex() > 0) {
            buffer.appendAttribute("tabindex", getTabIndex());
        }

        String onClickAction = " onclick=\"" + getOnClick() + "\"";
        buffer.append(onClickAction);

        appendAttributes(buffer);

        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }

        buffer.elementEnd();

        return buffer.toString();
    }

    /**
     * Return the Button's <code>onClick()</code> <code>String</code>, representing
     * a javascript location of the Page to get.
     *
     * @return the Button's <code>onClick()</code> <code>String</code>, representing
     * a javascript location of the Page to get.
     */
    public String getOnClick() {
        return "javascript:document.location.href='"
               + getHref()
               + "';";
    }
}
