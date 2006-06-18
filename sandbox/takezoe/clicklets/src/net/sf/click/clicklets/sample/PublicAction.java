package net.sf.click.clicklets.sample;

import net.sf.click.Control;

public class PublicAction extends ClickAction {
	
	public PublicAction(Control ctrl) {
		super(ctrl);
	}

	public boolean actionPerformed() {
		System.out.println("** ClickAction invoked **");
		return true;
	}

}
