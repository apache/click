package net.sf.click.clicklets.sample;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.Page;
import net.sf.click.control.Column;
import net.sf.click.control.Table;
import net.sf.click.extras.panel.BasicPanel;
import net.sf.click.extras.panel.TabbedPanel;

public class SamplePage2 extends Page {

	public void onInit(){
		List items = new ArrayList();
		items.add(new Entry("1", "Ken", "ken@example.com"));
		items.add(new Entry("2", "Ben", "ben@example.com"));
		items.add(new Entry("3", "Linda", "linda@example.com"));
		
		Table table = new Table("table");
		table.addColumn(new Column("id"));
		table.addColumn(new Column("name"));
		table.addColumn(new Column("mail"));
		table.setRowList(items);
		addControl(table);
		
		TabbedPanel tab = new TabbedPanel("tab");
		tab.addPanel(new BasicPanel("panel1", "panel1.htm"), true);
		tab.addPanel(new BasicPanel("panel2", "panel2.htm"), false);
		tab.setPage(this);
		addModel(tab.getName(), tab);
		
		addModel("items", items);
	}
	
}
