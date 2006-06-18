package net.sf.click.clicklets.sample;

import net.sf.click.Control;

public abstract class ClickAction {
	
    public ClickAction(Control ctrl) {
        super();
        ctrl.setListener(this, "actionPerformed");
    }

    public final boolean actionInt(){
       return actionPerformed();
    }

    public abstract boolean actionPerformed();
    
}
