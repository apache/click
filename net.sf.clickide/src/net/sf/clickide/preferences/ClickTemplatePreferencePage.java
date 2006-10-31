package net.sf.clickide.preferences;

import java.util.List;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The preference page to configure templates.
 * These templates are used in the New Click Page Creation Wizard.
 * 
 * @author Naoki Takezoe
 */
public class ClickTemplatePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private TableViewer tableViewer;
	private List tableModel = Template.loadFromPreference();
	
	public ClickTemplatePreferencePage() {
		super();
		setPreferenceStore(ClickPlugin.getDefault().getPreferenceStore());
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Initializes TableViewer
		tableViewer = new TableViewer(composite, 
				SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER|SWT.FULL_SELECTION);
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		column1.setText(ClickPlugin.getString("preferences.template.name"));
		column1.setWidth(80);
		
		TableColumn column2 = new TableColumn(table, SWT.LEFT);
		column2.setText(ClickPlugin.getString("preferences.template.pageClass"));
		column2.setWidth(100);
		
		TableColumn column3 = new TableColumn(table, SWT.LEFT);
		column3.setText(ClickPlugin.getString("preferences.template.htmlTemplate"));
		column3.setWidth(100);
		
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new ITableLabelProvider(){
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
			public String getColumnText(Object element, int columnIndex) {
				Template template = (Template)element;
				switch(columnIndex){
				case 0: return template.getName();
				case 1: return template.getPageClass();
				case 2: return template.getHtmlTemplate();
				}
				return null;
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			public void removeListener(ILabelProviderListener listener) {
			}
		});
		tableViewer.setInput(tableModel);
		
		// Initializes Buttons
		Composite buttons = new Composite(composite, SWT.NULL);
		buttons.setLayout(new GridLayout(1, false));
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 80;
		buttons.setLayoutData(gd);
		
		Button add = new Button(buttons, SWT.PUSH);
		add.setText(ClickPlugin.getString("action.add"));
		add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		add.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				TemplateDialog dialog = new TemplateDialog(getShell());
				if(dialog.open()==Dialog.OK){
					tableModel.add(dialog.getTemplate());
					tableViewer.refresh();
				}
			}
		});
		
		final Button edit = new Button(buttons, SWT.PUSH);
		edit.setText(ClickPlugin.getString("action.edit"));
		edit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
				Template template = (Template)selection.getFirstElement();
				TemplateDialog dialog = new TemplateDialog(getShell(), template);
				if(dialog.open()==Dialog.OK){
					tableViewer.refresh();
				}
			}
		});
		
		final Button remove = new Button(buttons, SWT.PUSH);
		remove.setText(ClickPlugin.getString("action.remove"));
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.setEnabled(false);
		remove.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
				tableModel.removeAll(selection.toList());
				tableViewer.refresh();
			}
		});
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event){
				if(tableViewer.getTable().getSelectionCount()==0){
					edit.setEnabled(false);
					remove.setEnabled(false);
				} else {
					edit.setEnabled(true);
					remove.setEnabled(true);
				}
			}
		});
		
		return composite;
	}

	public void init(IWorkbench workbench) {
	}
	
	public boolean performOk() {
		Template.saveToPreference(tableModel);
		return super.performOk();
	}
	
	protected void performDefaults() {
		String xml = getPreferenceStore().getDefaultString(ClickPlugin.PREF_TEMPLATES);
		
		tableModel.clear();
		tableModel.addAll(Template.loadFromXML(xml));
		tableViewer.refresh();
		
		super.performDefaults();
	}
	
	/**
	 * The dialog to create/edit templates.
	 */
	private class TemplateDialog extends Dialog {

		private Text name;
		private Text pageClass;
		private Text htmlTemplate;
		private Template template;
		
		public TemplateDialog(Shell parentShell) {
			this(parentShell, null);
		}

		public TemplateDialog(Shell parentShell, Template template) {
			super(parentShell);
			setShellStyle(getShellStyle()|SWT.RESIZE);
			this.template = template;
		}
		
		protected Point getInitialSize() {
			return new Point(400,350);
		}
		
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			updateButtonStatus();
		}
		
		protected Control createDialogArea(Composite parent) {
			getShell().setText(ClickPlugin.getString("preferences.template"));
			
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			ClickUtils.createLabel(composite, 
					ClickPlugin.getString("preferences.template.name")+":");
			name = new Text(composite, SWT.BORDER);
			name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			if(template!=null){
				name.setText(template.getName());
			}
			name.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e){
					updateButtonStatus();
				}
			});
			
			TabFolder tabFolder = new TabFolder(composite,SWT.NULL);
			tabFolder.setLayoutData(ClickUtils.createGridData(2, GridData.FILL_BOTH));
			
			// Page Class Tab
			TabItem item1 = new TabItem(tabFolder,SWT.NULL);
			item1.setText(ClickPlugin.getString("preferences.template.pageClass"));
			Composite composite1 = new Composite(tabFolder, SWT.NULL);
			composite1.setLayout(createPanelLayout());
			
			pageClass = new Text(composite1, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
			pageClass.setLayoutData(new GridData(GridData.FILL_BOTH));
			if(template!=null){
				pageClass.setText(template.getPageClass());
			}
			ClickUtils.createLabel(composite1, 
					ClickPlugin.getString("preferences.template.dialog.variables"));
			ClickUtils.createLabel(composite1, 
					"${package}, ${classname}, ${superclass}");
			item1.setControl(composite1);
			
			// HTML Template Tab
			TabItem item2 = new TabItem(tabFolder,SWT.NULL);
			item2.setText(ClickPlugin.getString("preferences.template.htmlTemplate"));
			Composite composite2 = new Composite(tabFolder, SWT.NULL);
			composite2.setLayout(createPanelLayout());
			
			htmlTemplate = new Text(composite2, SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
			htmlTemplate.setLayoutData(new GridData(GridData.FILL_BOTH));
			if(template!=null){
				htmlTemplate.setText(template.getHtmlTemplate());
			}
			ClickUtils.createLabel(composite2, 
					ClickPlugin.getString("preferences.template.dialog.variables"));
			ClickUtils.createLabel(composite2, "${charset}");
			item2.setControl(composite2);
			
			return composite;
		}
		
		private GridLayout createPanelLayout(){
			GridLayout layout = new GridLayout(1, false);
			layout.marginTop = 0;
			layout.marginBottom = 0;
			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			return layout;
		}
		
		private void updateButtonStatus(){
			getButton(IDialogConstants.OK_ID).setEnabled(name.getText().length()>0);
		}
		
		protected void okPressed() {
			if(template==null){
				template = new Template();
			}
			template.setName(name.getText());
			template.setPageClass(pageClass.getText());
			template.setHtmlTemplate(htmlTemplate.getText());
			
			super.okPressed();
		}
		
		public Template getTemplate(){
			return template;
		}
	}

}
