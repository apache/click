package net.sf.click.examples.control.html.list;

import net.sf.click.Control;
import net.sf.click.control.AbstractContainer;

/**
 * This control provides HTML ordered and unordered lists.
 *
 * @author Bob Schellink
 */
public class HtmlList extends AbstractContainer {

    public static final int UNORDERED_LIST = 0;

    public static final int ORDERED_LIST = 1;

    private int listMode = UNORDERED_LIST;

    public HtmlList() {
    }

    public HtmlList(int listMode) {
        this.listMode = listMode;
    }

    public HtmlList(String name) {
        super(name);
    }

    public HtmlList(String name, int listMode) {
        this(name);
        this.listMode = listMode;
    }

    public Control add(Control control) {
        if (!(control instanceof ListItem)) {
            throw new IllegalArgumentException("Only list items can be added.");
        }
        return super.add(control);
    }

    public String getTag() {
        if (isUnorderedList()) {
            return "ul";
        } else {
            return "ol";
        }
    }

    public int getListMode() {
        return listMode;
    }

    public void setListMode(int listMode) {
        this.listMode = listMode;
    }

    public boolean isUnorderedList() {
        return listMode == UNORDERED_LIST;
    }
}
