package net.sf.click.control;

/**
 * Provides an Image Button control: &nbsp; &lt;input type='image'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <input type='image' value='Button' title='Button Control' src='images/myButtonImage.jpg'/>
 * </td></tr>
 * </table>
 *
 * The ImageButton control is used to render a JavaScript enabled button which
 * can perform client side logic. The Button control provides no servier side
 * processing. If server side processing is required use {@link Submit} instead.
 * <p/>
 * The example below adds a back button to a form, which when clicked returns
 * to the previous page.
 *
 * <pre class="codeJava">
 * ImageButton backButton = <span class="kw">new</span> Button(<span class="st">" &lt Back ", "images/back.gif"</span>);
 * backButton.setOnClick(<span class="st">"history.back();"</span>);
 * backButton.setTitle(<span class="st">"Return to previous page"</span>);
 * form.add(backButton); </pre>
 *
 * HTML output:
 * <pre class="codeHtml">
 * &lt;input type='image' name='back' value=' &lt Back ' onclick='history.back();'
 *        title='Return to previous page'&gt; </pre>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Phil
 * @since Jul 9, 2005 @ 9:58:55 PM
 */
public class ImageButton extends Button {
    private String imageSource;

    public ImageButton(String value) {
        super(value);
    }

    public ImageButton(String value, String imageSource) {
        super(value);
        setImageSource(imageSource);
    }

    public String getType() {
        return "image";
    }

    public String getOnClick() {
        return "this.form.submit();";
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    /**
     * Return a HTML rendered Button string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(40);

        buffer.append("<input type='");
        buffer.append(getType());
        buffer.append("' name='");
        buffer.append(getName());
        buffer.append("' id='");
        buffer.append(getId());
        buffer.append("' value='");
        buffer.append(getValue());
        buffer.append("' src='");
        buffer.append(getImageSource());
        buffer.append("'");
        if (getTitle() != null) {
            buffer.append(" title='");
            buffer.append(getTitle());
            buffer.append("' ");
        }

        renderAttributes(buffer);

        buffer.append(getDisabled());

        buffer.append(">");

        return buffer.toString();
    }

}
