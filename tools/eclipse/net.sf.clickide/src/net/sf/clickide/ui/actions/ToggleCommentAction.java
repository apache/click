package net.sf.clickide.ui.actions;

import net.sf.clickide.ClickPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 *
 * @author Naoki Takezoe
 */
public class ToggleCommentAction extends Action {

	private ITextEditor editor;

	public ToggleCommentAction(ITextEditor editor) {
		super(ClickPlugin.getString("action.toggleComment"));
		this.editor = editor;
		setId(ToggleCommentAction.class.getName());
		setActionDefinitionId(ToggleCommentAction.class.getName());
		setAccelerator(SWT.CTRL | '/');
	}

	public void run() {
		ITextSelection sel = (ITextSelection) editor.getSelectionProvider().getSelection();
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());

		try {
			int offset = doc.getLineOffset(sel.getStartLine());
			String text = doc.get(offset, sel.getLength() + (sel.getOffset() - offset));

			if(text.startsWith("##")){
				text = text.replaceAll("(^|\r\n|\r|\n)##", "$1");
			} else {
				text = "##" + text.replaceAll("(\r\n|\r|\n)", "$1##");
				if(text.endsWith("##")){
					text = text.substring(0, text.length() - 2);
				}
			}

			doc.replace(offset, sel.getLength() + (sel.getOffset() - offset), text);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
