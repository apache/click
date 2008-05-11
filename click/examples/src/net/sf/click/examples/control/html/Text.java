package net.sf.click.examples.control.html;

import net.sf.click.control.AbstractControl;
import net.sf.click.util.HtmlStringBuffer;

/**
 * This control renders a string of text.
 *
 * @author Bob Schellink
 */
public class Text extends AbstractControl {

    private Object text;

    public Text() {
        
    }

    public Text(Object text) {
        this.text = text;
    }

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }

    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(20);
        render(buffer);
        return buffer.toString();
    }
    
    public void render(HtmlStringBuffer buffer) {
        if(getText() != null) {
            buffer.append(getText());
        }
    }
}
