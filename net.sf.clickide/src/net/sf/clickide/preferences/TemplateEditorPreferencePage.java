package net.sf.clickide.preferences;

import net.sf.clickide.ClickPlugin;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class TemplateEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public TemplateEditorPreferencePage(){
		super(GRID);
		setPreferenceStore(ClickPlugin.getDefault().getPreferenceStore());
	}
	
	protected void createFieldEditors() {
		setTitle(ClickPlugin.getString("preferences.templateEditor"));
		Composite parent = getFieldEditorParent();
		
		ColorFieldEditor variable = new ColorFieldEditor(
				ClickPlugin.PREF_COLOR_VAR, 
				ClickPlugin.getString("preferences.templateEditor.colorVariable"), parent);
		addField(variable);
		
		ColorFieldEditor directive = new ColorFieldEditor(
				ClickPlugin.PREF_COLOR_DIR,
				ClickPlugin.getString("preferences.templateEditor.colorDirective"), parent);
		addField(directive);
		
		ColorFieldEditor comment = new ColorFieldEditor(
				ClickPlugin.PREF_COLOR_CMT, 
				ClickPlugin.getString("preferences.templateEditor.colorComment"), parent);
		addField(comment);
	}

	public void init(IWorkbench workbench) {
	}

}
