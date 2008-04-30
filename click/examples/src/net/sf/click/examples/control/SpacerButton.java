package net.sf.click.examples.control;

import net.sf.click.control.Button;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a button spacer control for adding spaces between buttons.
 *
 * @author Malcolm Edgar
 */
public class SpacerButton extends Button {

    private static final long serialVersionUID = 1L;

    public SpacerButton() {
        setName(String.valueOf(System.currentTimeMillis()));
    }

    public void render(HtmlStringBuffer buffer) {
        buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
    }

}
