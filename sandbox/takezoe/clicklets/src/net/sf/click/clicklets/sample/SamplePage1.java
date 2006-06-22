package net.sf.click.clicklets.sample;

import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Form;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.RegexField;

public class SamplePage1 extends Page {
	
	private Form form;
	private TextField text;
	private DateField date1;
	private DateField date2;
	private TextArea textarea;
	private Checkbox checkbox;
	private Select select;
	private RadioGroup radio;
	private RegexField version;
	private Submit submit;
	
	public void onInit(){
		version = new RegexField("version");
		version.setPattern("[0-9]+\\.[0-9]+\\.[0-9]+");
		version.setRequired(true);
		
		date1 = new DateField("date1");
		date2 = new DateField("date2");
		
		submit = new Submit("submit");
		new PublicAction(submit);
//		new ClickAction(submit){
//			public boolean actionPerformed() {
//				System.out.println("** ClickAction invoked **");
//				onClick();
//				return true;
//			}
//		};
		
		form.setJavaScriptValidation(true);
		form.add(submit);
		form.add(version);
		form.add(date1);
		form.add(date2);
	}
	
	public boolean onClick(){
		if(form.isValid()){
			System.out.println("text="+text.getValue());
			System.out.println("textarea="+textarea.getValue());
			System.out.println("checkbox="+checkbox.isChecked());
			System.out.println("radio="+radio.getValue());
			System.out.println("select="+select.getValue());
		}
		return true;
	}
	
	public boolean linkClick(){
		System.out.println("link was clicked!");
		return true;
	}
	
}
