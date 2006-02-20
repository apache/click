package net.sf.clickide.ui.editor.forms;

import java.util.ArrayList;
import java.util.List;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.ui.editor.actions.ElementAppendAction;
import net.sf.clickide.ui.editor.actions.ElementRemoveAction;
import net.sf.clickide.ui.editor.attrs.ControlAttributeEditor;
import net.sf.clickide.ui.editor.attrs.IAttributeEditor;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickControlsEditor extends AbstractFormEditor {
	
	private SashForm sash;
	private TreeViewer viewer;
	private Composite currentEditor;
	
	private MenuManager menu;
	private MenuManager newMenu;
	private ElementRemoveAction deleteAction = new ElementRemoveAction();

	public void initModel(IStructuredModel model){
		sash = new SashForm(form.getBody(), SWT.HORIZONTAL);
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		sash.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		final Composite left = toolkit.createComposite(sash);
		left.setLayoutData(new GridData(GridData.FILL_BOTH));
		left.setLayout(new GridLayout(1, false));
		
		final Composite right = toolkit.createComposite(sash);
		right.setLayoutData(new GridData(GridData.FILL_BOTH));
		right.setLayout(new GridLayout(2, false));
		
		Section headerSection = toolkit.createSection(left, Section.DESCRIPTION|Section.TITLE_BAR);
		headerSection.setText(ClickPlugin.getString("editor.clickXML.controls"));
		headerSection.setLayoutData(ClickUtils.createGridData(1, GridData.FILL_HORIZONTAL));
		
		Tree tree = new Tree(left,SWT.NULL);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		List acceptElements = new ArrayList();
		acceptElements.add(ClickPlugin.TAG_CONTROLS);
		acceptElements.add(ClickPlugin.TAG_CONTROL);
		
		viewer = new TreeViewer(tree);
		viewer.setContentProvider(new ClickTreeContentProvider(acceptElements));
		viewer.setLabelProvider(new ClickTreeLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event){
				if(currentEditor!=null){
					currentEditor.dispose();
				}
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				Object obj = selection.getFirstElement();
				if(obj!=null && obj instanceof IDOMElement){
					if(((IDOMElement)obj).getNodeName().equals(ClickPlugin.TAG_CONTROL)){
						IAttributeEditor editor = new ControlAttributeEditor();
						currentEditor = editor.createForm(toolkit, right, (IDOMElement)obj);
					}
				}
				right.layout();
			}
		});
		
		viewer.setInput(model);
		viewer.expandAll();
		
		menu = new MenuManager();
		newMenu = new MenuManager(ClickPlugin.getString("action.new"));
		menu.add(newMenu);
		menu.add(new Separator());
		menu.add(deleteAction);
		tree.setMenu(menu.createContextMenu(tree));
		tree.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				updateMenu();
			}
		});
		
		toolkit.paintBordersFor(left);
		
		Section detailSection = toolkit.createSection(right, Section.DESCRIPTION|Section.TITLE_BAR);
		detailSection.setText(ClickPlugin.getString("editor.clickXML.details"));
		detailSection.setLayoutData(ClickUtils.createGridData(2, GridData.FILL_HORIZONTAL));
	}
	
	public void modelUpdated(IStructuredModel model){
		viewer.setSelection(null);
		viewer.refresh();
	}
	
	public void setFocus() {
		form.setFocus();
	}
	
	public void updateMenu(){
		newMenu.removeAll();
		
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		Object obj = selection.getFirstElement();
		
		if(obj instanceof IDOMElement){
			IDOMElement element = (IDOMElement)obj;
			
			if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP)){
				deleteAction.setEnabled(false);
			} else {
				deleteAction.setEnabled(true);
				deleteAction.setElement(element);
			}
			
			if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP) && 
					ClickUtils.getElement(element, ClickPlugin.TAG_CONTROLS)==null){
				newMenu.add(new ElementAppendAction(ClickPlugin.TAG_CONTROLS, element, null, this));
			}
			
			if(element.getNodeName().equals(ClickPlugin.TAG_CONTROLS)){
				newMenu.add(new ElementAppendAction(ClickPlugin.TAG_CONTROL, element, null, this));
			}
		}
	}

	public Object getAdapter(Class adapter) {
		if(adapter.equals(TreeViewer.class)){
			return this.viewer;
		}
		return super.getAdapter(adapter);
	}

}
