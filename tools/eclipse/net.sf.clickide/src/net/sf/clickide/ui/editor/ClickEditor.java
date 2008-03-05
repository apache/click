package net.sf.clickide.ui.editor;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ui.editor.forms.ClickControlsEditor;
import net.sf.clickide.ui.editor.forms.ClickFileItemFactoryEditor;
import net.sf.clickide.ui.editor.forms.ClickGeneralEditor;
import net.sf.clickide.ui.editor.forms.ClickHeadersEditor;
import net.sf.clickide.ui.editor.forms.ClickPagesEditor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * The editor for click.xml.
 * <p>
 * This editor provides the tree editor and the source editor 
 * as the multi-page editor. They can be toggled using tabs.
 * </p>
 * 
 * @author Naoki Takezoe
 */
public class ClickEditor extends MultiPageEditorPart implements IResourceChangeListener {
	
	private StructuredTextEditor sourceEditor;
	private ClickGeneralEditor generalEditor;
	private ClickHeadersEditor headerEditor;
	private ClickPagesEditor pageEditor;
	private ClickControlsEditor controlEditor;
	private ClickFileItemFactoryEditor fileItemFactoryEditior;
	
//	private int generalEditorIndex;
//	private int headerEditorIndex;
//	private int pageEditorIndex;
//	private int controlEditorIndex;
	private int sourceEditorIndex = 0;
	
	private IModelStateListener listener = new IModelStateListener(){
		public void modelAboutToBeChanged(IStructuredModel model) {
			modelUpdated(model);
		}
		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
			modelUpdated(structuredModel);
		}
		public void modelChanged(IStructuredModel model) {
			modelUpdated(model);
		}
		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
			modelUpdated(model);
		}
		public void modelReinitialized(IStructuredModel structuredModel) {
			modelUpdated(structuredModel);
		}
		public void modelResourceDeleted(IStructuredModel model) {
			modelUpdated(model);
		}
		public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {
			modelUpdated(newModel);
		}		
	};
	
	public ClickEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void createPages() {
		try {
			sourceEditor = new StructuredTextEditor();
			addPage(0, sourceEditor, getEditorInput());
			setPageText(0, ClickPlugin.getString("editor.clickXML.source"));
		} catch(Exception ex){
			ClickPlugin.log(ex);
		}
		
		IStructuredModel model = (IStructuredModel)sourceEditor.getAdapter(IStructuredModel.class);
		
		try {
			fileItemFactoryEditior = new ClickFileItemFactoryEditor();
			addPage(0, fileItemFactoryEditior, getEditorInput());
			fileItemFactoryEditior.initModel(model);
			setPageText(0, ClickPlugin.getString("editor.clickXML.fileUpload"));
			sourceEditorIndex++;
		} catch(Exception ex){
			removePage(0);
		}
		try {
			controlEditor = new ClickControlsEditor();
			addPage(0, controlEditor, getEditorInput());
			controlEditor.initModel(model);
			setPageText(0, ClickPlugin.getString("editor.clickXML.controls"));
			sourceEditorIndex++;
		} catch(Exception ex){
			removePage(0);
		}
		try {
			pageEditor = new ClickPagesEditor();
			addPage(0, pageEditor, getEditorInput());
			pageEditor.initModel(model);
			setPageText(0, ClickPlugin.getString("editor.clickXML.pages"));
			sourceEditorIndex++;
		} catch(Exception ex){
			removePage(0);
		}
		try {
			headerEditor = new ClickHeadersEditor();
			addPage(0, headerEditor, getEditorInput());
			headerEditor.initModel(model);
			setPageText(0, ClickPlugin.getString("editor.clickXML.headers"));
			sourceEditorIndex++;
		} catch(Exception ex){
			removePage(0);
		}
		try {
			generalEditor = new ClickGeneralEditor();
			addPage(0, generalEditor, getEditorInput());
			generalEditor.initModel(model);
			setPageText(0, ClickPlugin.getString("editor.clickXML.general"));
			sourceEditorIndex++;
		} catch(Exception ex){
			removePage(0);
		}		
		
		model.addModelStateListener(listener);
		
//		IContentOutlinePage outline 
//			= (IContentOutlinePage)sourceEditor.getAdapter(IContentOutlinePage.class);
//		outline.addSelectionChangedListener(new ISelectionChangedListener(){
//			public void selectionChanged(SelectionChangedEvent event){
//				setActivePage(4);
//			}
//		});
//		ConfigurableContentOutlinePage page = (ConfigurableContentOutlinePage)outline;
//		System.out.println(page.getControl());
		
		setActivePage(0);
	}

	public Object getAdapter(Class key) {
		if (key.equals(IContentOutlinePage.class)) {
			return sourceEditor.getAdapter(IContentOutlinePage.class);
		} else if (key.equals(IGotoMarker.class)) {
			setActivePage(sourceEditorIndex);
			return sourceEditor.getAdapter(IGotoMarker.class);
		} else {
			return super.getAdapter(key);
		}
	}

	public void doSave(IProgressMonitor progressMonitor) {
		sourceEditor.doSave(progressMonitor);
	}
	

	public void doSaveAs() {
		sourceEditor.doSaveAs();
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput)){
			throw new PartInitException("Unsupported editor input.");
		}
		super.init(site, editorInput);
		setPartName(editorInput.getName());
	}

	public void setFocus() {
		getControl(getActivePage()).setFocus();
	}
	
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		IStructuredModel model = (IStructuredModel)sourceEditor.getAdapter(IStructuredModel.class);
		model.removeModelStateListener(listener);
		super.dispose();
	}
	
	public void resourceChanged(final IResourceChangeEvent event){
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){
				if(!((FileEditorInput)sourceEditor.getEditorInput()).getFile().exists()){
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					page.closeEditor(ClickEditor.this, false);
				}
			}
		});
	}
	
	private void modelUpdated(IStructuredModel model){
		if(getActivePage()==4){
			generalEditor.modelUpdated(model);
			controlEditor.modelUpdated(model);
			headerEditor.modelUpdated(model);
			pageEditor.modelUpdated(model);
		}
	}
	
//	protected IEditorSite createSite(IEditorPart editor) {
//		if(editor instanceof StructuredTextEditor){
//			return new SourceEditorSite(this, editor, getEditorSite());
//		} else {
//			return super.createSite(editor);
//		}
//	}
//	
//	private class SourceEditorSite extends MultiPageEditorSite {
//		
//		private IEditorSite site;
//		
//		public SourceEditorSite(MultiPageEditorPart multiPageEditor,IEditorPart editor,IEditorSite site) {
//			super(multiPageEditor, editor);
//			this.site = site;
//		}
//		
//		public IEditorActionBarContributor getActionBarContributor() {
//			return site.getActionBarContributor();
//		}
//	}

}
