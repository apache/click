package net.sf.clickide.ui.editor;

import net.sf.clickide.ClickPlugin;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

/**
 * {@link IAutoEditStrategy} implementation for the {@link TemplateEditor}.
 * 
 * @see TemplateEditor
 * @see TemplateEditorConfiguration
 * 
 * @author Naoki Takezoe
 */
public class TemplateAutoEditStrategy implements IAutoEditStrategy {

	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		try {
			// ${}
			if(command.text.equals("{") && command.offset > 1 
					&& document.get(command.offset - 1, 1).equals("$")){
				command.text = command.text + "}";
				command.caretOffset = command.offset + 1;
				command.shiftsCaret = false;
				command.doit = false;
			}
		} catch(BadLocationException ex){
			ClickPlugin.log(ex);
		}
	}

}
