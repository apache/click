package net.sf.click.clicklets;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.FuzzyXMLTextImpl;
import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Field;
import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Option;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;

/**
 * This handler handles form fields.
 * 
 * 
 * <table border="1">
 * <tr>
 *   <th>Tag</th><th>Field</th>
 * </tr>
 * <tr>
 *   <td>input type=&quot;text&quot;</td>
 *   <td>net.sf.click.control.TextField</td>
 * </tr>
 * <tr>
 *   <td>input type=&quot;submit&quot;</td>
 *   <td>net.sf.click.control.Submit</td>
 * </tr>
 * <tr>
 *   <td>input type=&quot;password&quot;</td>
 *   <td>net.sf.click.control.PasswordField</td>
 * </tr>
 * <tr>
 *   <td>input type=&quot;checkbox&quot;</td>
 *   <td>net.sf.click.control.Checkbox</td>
 * </tr>
 * <tr>
 *   <td>input type=&quot;radio&quot;</td>
 *   <td>net.sf.click.control.Radio / net.sf.click.control.RadioGroup</td>
 * </tr>
 * <tr>
 *   <td>input type=&quot;hidden&quot;</td>
 *   <td>net.sf.click.control.HiddenField</td>
 * </tr>
 * <tr>
 *   <td>input type=&quot;file&quot;</td>
 *   <td>net.sf.click.control.FileField</td>
 * </tr>
 * <tr>
 *   <td>textarea</td>
 *   <td>net.sf.click.control.TextArea</td>
 * </tr>
 * <tr>
 *   <td>select / option</td>
 *   <td>net.sf.click.control.Select / net.sf.click.control.Option</td>
 * </tr>
 * </table>
 * 
 * @author Naoki Takezoe
 */
public class FieldHandler implements ElementHandler {

	public boolean handleElement(Page page, FuzzyXMLElement element, Form form) {
		if(form!=null){
			Field field = createField(page, element, form);
			if(field!=null){
				form.add(field);
				FuzzyXMLElement parent = (FuzzyXMLElement)element.getParentNode();
				parent.replaceChild(new FuzzyXMLTextImpl("$" + form.getName() + ".fields." + field.getName() + "\n"), element);
				return false;
			}
		}
		return true;
	}
	
	private Field createField(Page page, FuzzyXMLElement element, Form form){
		if(element.getName().equalsIgnoreCase("input")){
			String type = element.getAttributeValue("type");
			if(type!=null){
				// input type="text"
				if(type.equalsIgnoreCase("text")){
					TextField field = new TextField();
					field.setName(element.getAttributeValue("name"));
					field.setLabel(element.getAttributeValue("label"));
					field.setValue(element.getAttributeValue("value"));
					if(element.getAttributeValue("maxlength")!=null){
						field.setMaxLength(Integer.parseInt(element.getAttributeValue("maxlength")));
					}
					if(element.getAttributeValue("minlength")!=null){
						field.setMinLength(Integer.parseInt(element.getAttributeValue("minlength")));
					}
					if(element.getAttributeValue("required")!=null){
						field.setRequired(Boolean.parseBoolean(element.getAttributeValue("required")));
					}
					if(element.getAttributeValue("size")!=null){
						field.setSize(Integer.parseInt(element.getAttributeValue("size")));
					}
					if(element.getAttributeValue("disabled")!=null){
						field.setDisabled(Boolean.parseBoolean(element.getAttributeValue("disabled")));
					}
					if(element.getAttributeValue("readonly")!=null){
						field.setReadonly(Boolean.parseBoolean(element.getAttributeValue("readonly")));
					}
					return field;
				}
				// input type="submit"
				if(type.equalsIgnoreCase("submit")){
					Submit field = new Submit();
					field.setName(element.getAttributeValue("name"));
					field.setLabel(element.getAttributeValue("value"));
					field.setListener(page, element.getAttributeValue("action"));
					if(element.getAttributeValue("disabled")!=null){
						field.setDisabled(Boolean.parseBoolean(element.getAttributeValue("disabled")));
					}
					if(element.getAttributeValue("onclick")!=null){
						field.setOnClick(element.getAttributeValue("onclick"));
					}
					return field;
				}
				// input type="password"
				if(type.equalsIgnoreCase("password")){
					PasswordField field = new PasswordField();
					field.setName(element.getAttributeValue("name"));
					field.setLabel(element.getAttributeValue("label"));
					field.setValue(element.getAttributeValue("value"));
					if(element.getAttributeValue("maxlength")!=null){
						field.setMaxLength(Integer.parseInt(element.getAttributeValue("maxlength")));
					}
					if(element.getAttributeValue("minlength")!=null){
						field.setMinLength(Integer.parseInt(element.getAttributeValue("minlength")));
					}
					if(element.getAttributeValue("required")!=null){
						field.setRequired(Boolean.parseBoolean(element.getAttributeValue("required")));
					}
					if(element.getAttributeValue("size")!=null){
						field.setSize(Integer.parseInt(element.getAttributeValue("size")));
					}
					if(element.getAttributeValue("disabled")!=null){
						field.setDisabled(Boolean.parseBoolean(element.getAttributeValue("disabled")));
					}
					if(element.getAttributeValue("readonly")!=null){
						field.setReadonly(Boolean.parseBoolean(element.getAttributeValue("readonly")));
					}
					return field;
				}
				// input type="hidden"
				if(type.equalsIgnoreCase("hidden")){
					HiddenField field = new HiddenField();
					field.setName(element.getAttributeValue("name"));
					field.setValue(element.getAttributeValue("value"));
					return field;
				}
				// input type="checkbox"
				if(type.equalsIgnoreCase("checkbox")){
					Checkbox field = new Checkbox();
					field.setName(element.getAttributeValue("name"));
					field.setValue(element.getAttributeValue("value"));
					field.setLabel(element.getAttributeValue("label"));
					if(element.getAttributeValue("required")!=null){
						field.setRequired(Boolean.parseBoolean(element.getAttributeValue("required")));
					}
					if(element.getAttributeValue("checked")!=null){
						field.setChecked(Boolean.parseBoolean(element.getAttributeValue("checked")));
					}
					if(element.getAttributeValue("disabled")!=null){
						field.setDisabled(Boolean.parseBoolean(element.getAttributeValue("disabled")));
					}
					if(element.getAttributeValue("readonly")!=null){
						field.setReadonly(Boolean.parseBoolean(element.getAttributeValue("readonly")));
					}
					return field;
				}
				// input type="radio"
				if(type.equalsIgnoreCase("radio")){
					// TODO radio handling must be improved...
					RadioGroup group = (RadioGroup)form.getField(element.getAttributeValue("name"));
					boolean create = false;
					if(group == null){
						group = new RadioGroup();
						group.setName(element.getAttributeValue("name"));
						group.setLabel(element.getAttributeValue("label"));
						create = true;
					}
					Radio field = new Radio();
					field.setValue(element.getAttributeValue("value"));
					field.setLabel(element.getAttributeValue("label"));
					if(element.getAttributeValue("checked")!=null){
						field.setChecked(Boolean.parseBoolean(element.getAttributeValue("checked")));
					}
					if(element.getAttributeValue("required")!=null){
						field.setRequired(Boolean.parseBoolean(element.getAttributeValue("required")));
					}
					if(element.getAttributeValue("disabled")!=null){
						field.setDisabled(Boolean.parseBoolean(element.getAttributeValue("disabled")));
					}
					if(element.getAttributeValue("readonly")!=null){
						field.setReadonly(Boolean.parseBoolean(element.getAttributeValue("readonly")));
					}
					group.add(field);
					
					if(create){
						return group;
					}
					
					FuzzyXMLElement parent = (FuzzyXMLElement)element.getParentNode();
					parent.removeChild(element);
					
					return null;
				}
				// input type="file"
				if(type.equalsIgnoreCase("file")){
					FileField field = new FileField();
					field.setName(element.getAttributeValue("name"));
					field.setLabel(element.getAttributeValue("label"));
					if(element.getAttributeValue("required")!=null){
						field.setRequired(Boolean.parseBoolean(element.getAttributeValue("required")));
					}
					if(element.getAttributeValue("size")!=null){
						field.setSize(Integer.parseInt(element.getAttributeValue("size")));
					}
					if(element.getAttributeValue("disabled")!=null){
						field.setDisabled(Boolean.parseBoolean(element.getAttributeValue("disabled")));
					}
					if(element.getAttributeValue("readonly")!=null){
						field.setReadonly(Boolean.parseBoolean(element.getAttributeValue("readonly")));
					}
					return field;
				}
//				// TODO input type="button"
//				if(type.equalsIgnoreCase("button")){
//					Button field = new Button();
//					return field;
//				}
//				// TODO input type="reset"
//				if(type.equalsIgnoreCase("reset")){
//					Reset field = new Reset();
//					return field;
//				}
			}
		}
		
		if(element.getName().equalsIgnoreCase("textarea")){
			TextArea field = new TextArea();
			field.setName(element.getAttributeValue("name"));
			field.setLabel(element.getAttributeValue("label"));
			if(element.getAttributeValue("maxlength")!=null){
				field.setMaxLength(Integer.parseInt(element.getAttributeValue("maxlength")));
			}
			if(element.getAttributeValue("minlength")!=null){
				field.setMinLength(Integer.parseInt(element.getAttributeValue("minlength")));
			}
			if(element.getAttributeValue("required")!=null){
				field.setRequired(Boolean.parseBoolean(element.getAttributeValue("required")));
			}
			if(element.getAttributeValue("cols")!=null){
				field.setCols(Integer.parseInt(element.getAttributeValue("cols")));
			}
			if(element.getAttributeValue("rows")!=null){
				field.setRows(Integer.parseInt(element.getAttributeValue("rows")));
			}
			return field;
		}
		
		if(element.getName().equalsIgnoreCase("select")){
			Select field = new Select();
			field.setName(element.getAttributeValue("name"));
			field.setLabel(element.getAttributeValue("label"));
			if(element.getAttributeValue("required")!=null){
				field.setRequired(Boolean.parseBoolean(element.getAttributeValue("required")));
			}
			
			FuzzyXMLNode[] children = element.getChildren();
			for(int i=0;i<children.length;i++){
				if(children[i] instanceof FuzzyXMLElement && 
						((FuzzyXMLElement)children[i]).getName().equalsIgnoreCase("option")){
					FuzzyXMLElement option = (FuzzyXMLElement)children[i];
					
					String rendered = option.getAttributeValue(ClickHTMLConstants.C_RENDERED);
					if(rendered!=null && rendered.equals("false")){
						continue;
					}
					
					String value = option.getAttributeValue("value");
					String label = option.getValue();
					if(value==null){
						value = label;
					}
					if(label==null){
						label = value;
					}
					field.add(new Option(value, label));
				}
			}
			return field;
		}
		
		return null;
	}

}
