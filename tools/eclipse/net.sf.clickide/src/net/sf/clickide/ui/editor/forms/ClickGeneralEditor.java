package net.sf.clickide.ui.editor.forms;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.ui.editor.attrs.AttributeEditorUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.NodeList;

/**
 * The form editor to editing Click general informations.
 * 
 * @author Naoki Takezoe
 */
public class ClickGeneralEditor extends AbstractFormEditor {
	
	private Text textCharset;
	private Text textLocale;
	private Text textFormat;
	private Combo comboMode;
	
	public void initModel(IStructuredModel model){
		Section generic = toolkit.createSection(form.getBody(), Section.DESCRIPTION|Section.TITLE_BAR);
		generic.setText(ClickPlugin.getString("editor.clickXML.general"));
		generic.setLayoutData(ClickUtils.createGridData(2, GridData.FILL_HORIZONTAL));
		
		IDOMDocument doc = ((IDOMModel)model).getDocument();
		NodeList nodes = doc.getElementsByTagName(ClickPlugin.TAG_CLICK_APP);
		final IDOMElement clickApp = (IDOMElement)nodes.item(0);
		IDOMElement format = ClickUtils.getElement(clickApp, ClickPlugin.TAG_FORMAT);
		IDOMElement mode   = ClickUtils.getElement(clickApp, ClickPlugin.TAG_MODE);
		
		textCharset = AttributeEditorUtils.createText(
				toolkit, form.getBody(), clickApp, 
				ClickPlugin.getString("editor.clickXML.general.charset"), 
				ClickPlugin.ATTR_CHARSET);
		textCharset.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textCharset.getText().equals("")){
					clickApp.removeAttribute(ClickPlugin.ATTR_CHARSET);
				} else {
					clickApp.setAttribute(ClickPlugin.ATTR_CHARSET, textCharset.getText());
				}
			}
		});
		
		textLocale = AttributeEditorUtils.createText(
				toolkit, form.getBody(), clickApp,
				ClickPlugin.getString("editor.clickXML.general.locale"), 
				ClickPlugin.ATTR_LOCALE);
		textLocale.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textLocale.getText().equals("")){
					clickApp.removeAttribute(ClickPlugin.ATTR_LOCALE);
				} else {
					clickApp.setAttribute(ClickPlugin.ATTR_LOCALE, textLocale.getText());
				}
			}
		});
		
		IFile file = (IFile)ClickUtils.getResource(clickApp.getStructuredDocument());
		IJavaProject project = JavaCore.create(file.getProject());
		
		textFormat = AttributeEditorUtils.createClassText(
				project, toolkit, form.getBody(), format, 
				ClickPlugin.getString("editor.clickXML.general.format"), 
				ClickPlugin.ATTR_CLASSNAME, null, null);
		textFormat.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				IDOMElement format = ClickUtils.getElement(clickApp, ClickPlugin.TAG_FORMAT);
				if(textFormat.getText().equals("")){
					if(format!=null){
						clickApp.removeChild(format);
					}
				} else {
					if(format==null){
						format = (IDOMElement)clickApp.getOwnerDocument().createElement(ClickPlugin.TAG_FORMAT);
						IDOMElement[] elements = {
								ClickUtils.getElement(clickApp, ClickPlugin.TAG_MODE),
								ClickUtils.getElement(clickApp, ClickPlugin.TAG_CONTROLS)};
						for(int i=0;i<elements.length;i++){
							if(elements[i]!=null){
								clickApp.insertBefore(format, elements[i]);
								break;
							}
						}
						if(ClickUtils.getElement(clickApp, ClickPlugin.TAG_FORMAT)==null){
							clickApp.appendChild(format);
						}
					}
					format.setAttribute(ClickPlugin.ATTR_CLASSNAME, textFormat.getText());
				}
			}
		});
		
		comboMode = AttributeEditorUtils.createCombo(
				toolkit, form.getBody(), mode, 
				ClickPlugin.getString("editor.clickXML.general.mode"), 
				ClickPlugin.ATTR_VALUE, 
				ClickUtils.createComboValues(ClickPlugin.MODE_VALUES));
		comboMode.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				IDOMElement mode = ClickUtils.getElement(clickApp, ClickPlugin.TAG_MODE);
				if(comboMode.getText().equals("")){
					if(mode!=null){
						mode.removeAttribute(ClickPlugin.ATTR_VALUE);
						if(mode.getAttributes().getLength()==0){
							clickApp.removeChild(mode);
						}
					}
				} else {
					if(mode==null){
						mode = (IDOMElement)clickApp.getOwnerDocument().createElement(ClickPlugin.TAG_MODE);
						IDOMElement control = ClickUtils.getElement(clickApp, ClickPlugin.TAG_CONTROLS);
						if(control!=null){
							clickApp.insertBefore(mode, control);
						} else {
							clickApp.appendChild(mode);
						}
					}
					mode.setAttribute(ClickPlugin.ATTR_VALUE, comboMode.getText());
				}
			}
		});
		
		form.getBody().layout();
	}

	public void modelUpdated(IStructuredModel model){
		IDOMDocument doc = ((IDOMModel)model).getDocument();
		NodeList nodes = doc.getElementsByTagName(ClickPlugin.TAG_CLICK_APP);
		IDOMElement clickApp = (IDOMElement)nodes.item(0);
		IDOMElement format = ClickUtils.getElement(clickApp, ClickPlugin.TAG_FORMAT);
		IDOMElement mode   = ClickUtils.getElement(clickApp, ClickPlugin.TAG_MODE);

		updateText(textCharset, clickApp, ClickPlugin.ATTR_CHARSET);
		updateText(textFormat, format, ClickPlugin.ATTR_CLASSNAME);
		updateCombo(comboMode, mode, ClickPlugin.ATTR_VALUE);
//		updateCombo(comboLogTo, mode, ClickPlugin.ATTR_LOGTO);
	}
	
	private void updateText(Text text, IDOMElement element, String attrName){
		if(element!=null && element.getAttribute(attrName)!=null){
			if(!text.getText().equals(element.getAttribute(attrName))){
				text.setText(element.getAttribute(attrName));
			}
		} else {
			if(!text.getText().equals("")){
				text.setText("");
			}
		}
	}
	
	private void updateCombo(Combo combo, IDOMElement element, String attrName){
		if(element!=null && element.getAttribute(attrName)!=null){
			if(!combo.getText().equals(element.getAttribute(attrName))){
				combo.setText(element.getAttribute(attrName));
			}
		} else {
			if(!combo.getText().equals("")){
				combo.setText("");
			}
		}
	}
	
	public void setFocus() {
		form.setFocus();
	}
	
	
}
