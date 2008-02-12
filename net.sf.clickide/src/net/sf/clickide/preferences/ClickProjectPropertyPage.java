package net.sf.clickide.preferences;

import java.util.ArrayList;
import java.util.List;

import net.sf.clickide.ClickPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * The project property page to define additonal Velocity variables.
 * 
 * @author Naoki Takezoe
 */
public class ClickProjectPropertyPage extends PropertyPage {
	
	private List models = new ArrayList();
	private TableViewer viewer;
	
	public ClickProjectPropertyPage(){
		setTitle("Click");
		setDescription("Define Velocity Variables");
	}
	
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TableViewer(composite);
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		
		TableColumn column1 = new TableColumn(table, SWT.NULL);
		column1.setText("Name");
		column1.setWidth(80);
		
		TableColumn column2 = new TableColumn(table, SWT.NULL);
		column2.setText("Type");
		column2.setWidth(200);
		
		Composite buttons = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		buttons.setLayout(layout);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		Button addButton = new Button(buttons, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				VariableModel model = new VariableModel();
				VelocityVariableDialog dialog = new VelocityVariableDialog(viewer.getTable().getShell(), model);
				if(dialog.open() == Dialog.OK){
					models.add(model);
					viewer.refresh();
				}
			}
		});
		
		final Button editButton = new Button(buttons, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				if(!sel.isEmpty()){
					VariableModel model = (VariableModel) sel.getFirstElement();
					VelocityVariableDialog dialog = new VelocityVariableDialog(viewer.getTable().getShell(), model);
					if(dialog.open() == Dialog.OK){
						viewer.refresh();
					}
				}
			}
		});
		editButton.setEnabled(false);
		
		final Button removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				if(!sel.isEmpty()){
					models.removeAll(sel.toList());
					viewer.refresh();
				}
			}
		});
		removeButton.setEnabled(false);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				editButton.setEnabled(!sel.isEmpty());
				removeButton.setEnabled(!sel.isEmpty());
			}
		});
		
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ITableLabelProvider(){
			public Image getColumnImage(Object row, int column) {
				return null;
			}
			public String getColumnText(Object row, int column) {
				if(column==0){
					return ((VariableModel) row).name;
				} else if(column==1){
					return ((VariableModel) row).type;
				}
				return null;
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}
			public boolean isLabelProperty(Object row, String property) {
				return false;
			}
			public void removeListener(ILabelProviderListener listener) {
			}
		});
		viewer.setInput(models);
		
		return composite;
	}
	
	private IProject getProject(){
		return (IProject)getElement();
	}
	
	/**
	 * The dialog to register / modify a Velocity variable.
	 */
	private class VelocityVariableDialog extends Dialog {
		
		private Text name;
		private Text type;
		private VariableModel model;
		
		public VelocityVariableDialog(Shell parent, VariableModel model){
			super(parent);
			this.model = model;
		}
		
		protected Point getInitialSize() {
			Point point = super.getInitialSize();
			point.x = 350;
			return point;
		}

		protected Control createDialogArea(Composite parent) {
			getShell().setText("Velocity Variable");
			
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout(3, false));
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			new Label(composite, SWT.NULL).setText("Name:");
			name = new Text(composite, SWT.BORDER);
			name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			name.setText(model.name);
			new Label(composite, SWT.NULL);
			
			new Label(composite, SWT.NULL).setText("Type:");
			type = new Text(composite, SWT.BORDER);
			type.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			type.setText(model.type);
			Button browseButton = new Button(composite, SWT.PUSH);
			browseButton.setText("Browse...");
			browseButton.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent evt){
					Shell shell = type.getShell();
					try {
						SelectionDialog dialog = JavaUI.createTypeDialog(
								shell,new ProgressMonitorDialog(shell),
								SearchEngine.createJavaSearchScope(new IJavaElement[]{JavaCore.create(getProject())}),
								IJavaElementSearchConstants.CONSIDER_CLASSES,false);
						
						if(dialog.open()==SelectionDialog.OK){
							Object[] result = dialog.getResult();
							type.setText(((IType)result[0]).getFullyQualifiedName());
						}
					} catch(Exception ex){
						ClickPlugin.log(ex);
					}
				}
			});
			
			
			return composite;
		}

		protected void okPressed() {
			model.name = name.getText();
			model.type = type.getText();
			super.okPressed();
		}
	}
	
	/**
	 * The model of Velocity variables.
	 */
	public static class VariableModel {
		public String name = "";
		public String type = "";
	}

}
