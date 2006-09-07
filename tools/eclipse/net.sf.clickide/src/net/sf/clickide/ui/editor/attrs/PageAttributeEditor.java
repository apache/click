package net.sf.clickide.ui.editor.attrs;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.ui.wizard.NewClickPageWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * The implementation of <code>IElementEditor</code> for &lt;page&gt;.
 * 
 * @author Naoki Takezoe
 */
public class PageAttributeEditor implements IAttributeEditor {
	
	public Composite createForm(FormToolkit toolkit, Composite parent, final IDOMElement element) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Control[] controls = AttributeEditorUtils.createLinkText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.pages.path"),
				ClickPlugin.ATTR_PATH);
		final Hyperlink linkPath = (Hyperlink)controls[0];
		final Text textPath = (Text)controls[1];
		textPath.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textPath.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_PATH);
				} else {
					element.setAttribute(ClickPlugin.ATTR_PATH, textPath.getText());
				}
			}
		});
		
		final Text textClass = AttributeEditorUtils.createClassText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.pages.class"), 
				ClickPlugin.ATTR_CLASSNAME,
				ClickPlugin.CLICK_PAGE_CLASS, textPath);
		textClass.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textClass.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_CLASSNAME);
				} else {
					element.setAttribute(ClickPlugin.ATTR_CLASSNAME, textClass.getText());
				}
			}
		});
		
		linkPath.addHyperlinkListener(new HyperlinkAdapter(){
			public void linkActivated(HyperlinkEvent e){
				IFile file = (IFile)ClickUtils.getResource(element.getStructuredDocument());
				IProject project = file.getProject();
				String root = ClickUtils.getWebAppRootFolder(project);
				try {
					IFile targetFile = project.getFile(new Path(root).append(textPath.getText()));
					if(targetFile.exists()){
						IDE.openEditor(ClickUtils.getActivePage(), targetFile);
						return;
					}
				} catch(Exception ex){
				}
				
				NewClickPageWizard wizard = new NewClickPageWizard();
				wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(project));
				wizard.setInitialPageName(textPath.getText());
				wizard.setInitialClassName(textClass.getText());
				WizardDialog dialog = new WizardDialog(textPath.getShell(), wizard);
				dialog.open();
			}
		});
		
		
		return composite;
	}

}
