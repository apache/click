package net.sf.click.webcode.examples.page;

import net.sf.click.Page;
import net.sf.click.util.HtmlStringBuffer;

/**
 * 
 */
public class DecoratorPage extends Page {
    public DecoratorPage() {
        String className = getClass().getName();

        String shortName = className.substring(className.lastIndexOf('.') + 1);
        HtmlStringBuffer title = new HtmlStringBuffer();
        title.append(shortName.charAt(0));
        for (int i = 1; i < shortName.length(); i++) {
            char aChar = shortName.charAt(i);
            if (Character.isUpperCase(aChar)) {
                title.append(' ');
            }
            title.append(aChar);
        }
        addModel("title", title);
    }
    public String getTemplate() {
        return "/decorator.htm";
    }
}
