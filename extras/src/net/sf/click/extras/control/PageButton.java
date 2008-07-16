package net.sf.click.extras.control;

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a Page link Button control: &nbsp; &lt;input type='button'&gt;.
 *
 * <table class='htmlHeader cellspacing='6'>
 * <tr>
 * <td><input type='button' value='Page Button' title='PageButton Control'/></td>
 * </tr>
 * </table>
 *
 * The PageButton is provides link style navigation button to a Page that is
 * rendered in form of a <code>button</code> (an <code>input</code> HTML element
 * with the <code>type="button"</code> more precisely) plus javascript getting
 * the borwser to the location of that Page.
 *
 * <h4>PageButton Advantages</h4>
 * <ul>
 *      <li>Over {@link net.sf.click.control.Button Button} + direct javascript trick:
 *          <ul>
 *               <li>encoding of parameters works correctly due to the {@link PageLink} it extends.</li>
 *          </ul>
 *      </li>
 *      <li>Over {@link PageLink}:
 *          <ul>
 *               <li>crawlers/spiders (or other browser plug-ins) won't follow the target since it's a button.</li>
 *               <li>the look and feel is native - much better than half-working CSS hacks
 *                  to simulate buttons with <code>Link</code> elements.</li>
 *          </ul>
 *      </li>
 *      <li>Over {@link net.sf.click.control.ActionButton ActionButton} + forward/redirect trick:
 *          <ul>
 *               <li>there's no roundtrip to the server.</li>
 *               <li><code>net.sf.click.Page#redirect(Class) does not support parameters</code> and
 *               <code>net.sf.click.Page#redirect(String)</code> is prone to errors when hardcoding
 *               them in the <code>String</code>.</li>
 *          </ul>
 *      </li>
 * </ul>
 * <p/>
 * <b>Note:</b> this control is related to {@link PageLink} and not to {@link net.sf.click.extras.control.PageSubmit}.
 *
 * @author Ahmed Mohombe
 */
public class PageButton extends PageLink {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constructors

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

    // --------------------------------------------------------- Public Methods

    /**
     * Return the links html tag: <tt>input</tt>.
     *
     * @see net.sf.click.control.AbstractControl#getTag()
     *
     * @return this controls html tag
     */
    public String getTag() {
        return "input";
    }

    /**
     * Render the HTML representation of the button. Note the button label is
     * rendered as the HTML "value" attribute.
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart(getTag());

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
        render(buffer);
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
