package net.sf.click.examples.control.html;

import net.sf.click.control.AbstractControl;
import net.sf.click.util.HtmlStringBuffer;

/**
 * This control allows rendering of arbitrary html.
 *
 * @author Bob Schellink
 */
public class Html extends AbstractControl {
    
    private String text;
    
    public Html() {
        
    }
    public Html(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }
    
    public void render(HtmlStringBuffer buffer) {
        if(getText() != null) {
            buffer.append(getText());
        }
    }
}
